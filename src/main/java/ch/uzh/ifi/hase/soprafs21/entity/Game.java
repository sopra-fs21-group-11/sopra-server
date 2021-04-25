package ch.uzh.ifi.hase.soprafs21.entity;

import ch.uzh.ifi.hase.soprafs21.constant.GameState;
import ch.uzh.ifi.hase.soprafs21.entity.Cards.Card;
import ch.uzh.ifi.hase.soprafs21.entity.ValueCategories.ValueCategory;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.CardMapper;
import ch.uzh.ifi.hase.soprafs21.rest.socketDTO.CardDTO;
import ch.uzh.ifi.hase.soprafs21.rest.socketDTO.EvaluatedCardDTO;
import ch.uzh.ifi.hase.soprafs21.rest.socketDTO.EvaluatedGameStateDTO;
import ch.uzh.ifi.hase.soprafs21.rest.socketDTO.GameStateDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.*;

public class Game {
    private Queue<Map.Entry<User, String>> players;
    private long id;
    private Board activeBoard;
    private Map.Entry<User, String> currentPlayer;
    private GameState activeState;
    private boolean hasWinner = false;
    private Deck deckStack;
    private long hostPlayerId;
    private List<User> waitingForPlayers;
    private Card nextCard;
    private ValueCategory horizontalValueCategory;
    private ValueCategory verticalValueCategory;

    private final GameSettings currentSettings;

    private SimpMessagingTemplate template;


    public Game(GameLobby lobby){
        //set up the game-object with all details of the lobby:
        this.waitingForPlayers = lobby.getPlayers();
        this.currentSettings = lobby.getSettings();
        this.hostPlayerId = lobby.getHostId();
        this.activeState = GameState.CARDPLACEMENT;
        this.id = lobby.getId();


        this.deckStack = new Deck();//Initializes the standard testing deck. (30 cards out of csv. All SwissLocationCard)

        //We set the starting-card and the nextCard right away:
        this.activeBoard = new Board(deckStack.pop());
        this.nextCard = deckStack.pop();

        this.players = new LinkedList<>();

    }

    public boolean joinGame(User user, String sessionId){
        boolean waitingFor = false;
        for(User waitingForUser : this.waitingForPlayers){
            if(waitingForUser.getId() == user.getId()){
                waitingFor = true;
                break;
            }
        }
        if(!waitingFor) {
            return false; //already joined or not in lobby
        }

        for(User waitingForUser : this.waitingForPlayers){
            if(waitingForUser.getId() == user.getId()){
                this.waitingForPlayers.remove(waitingForUser);
                break;
            }
        }
        if(this.hostPlayerId == user.getId()){
            this.currentPlayer = new AbstractMap.SimpleEntry<>(user, sessionId); //host starts the game.
        }
        this.players.add(new AbstractMap.SimpleEntry<User, String>(user, sessionId));//add token/user-combo to our players queue.
        return true;
    }

    public void performTurn(long userid, Card cardToPlace, int placementIndex, String axis){
        if(currentPlayer.getKey().getId() != userid){
            return;
        }
        //set next player
        players.add(currentPlayer);
        currentPlayer = players.remove();
        //place card
        activeBoard.placeCard(cardToPlace, placementIndex,axis);
        //set next card
        nextCard = deckStack.pop();

    }

    public GameStateDTO convertToDTO(){
        GameStateDTO gameStateDTO = new GameStateDTO();
        //add starting card first.
        Card startingCard = this.activeBoard.getStartingCard();
        CardDTO startingCardDTO = CardMapper.ConvertEntityToCardDTO(startingCard);


        gameStateDTO.setStartingCard(startingCardDTO);
        int positionCounter = 1;
        Card loopCard = startingCard;
        while(loopCard.getLeftNeighbour() !=null){ //get left cards
            CardDTO cardDTO = CardMapper.ConvertEntityToCardDTO(loopCard);
            cardDTO.setPosition(positionCounter);
            gameStateDTO.addLeftCard(cardDTO);
            loopCard = loopCard.getLeftNeighbour();
            positionCounter++;
        }
        positionCounter = 1;
        loopCard = startingCard;
        while(loopCard.getRightNeighbour() !=null){ //get right cards
            CardDTO cardDTO = CardMapper.ConvertEntityToCardDTO(loopCard);
            cardDTO.setPosition(positionCounter);
            gameStateDTO.addRightCard(cardDTO);
            loopCard = loopCard.getRightNeighbour();
            positionCounter++;
        }
        positionCounter = 1;
        loopCard = startingCard;
        while(loopCard.getHigherNeighbour() !=null){ //get top cards
            CardDTO cardDTO = CardMapper.ConvertEntityToCardDTO(loopCard);
            cardDTO.setPosition(positionCounter);
            gameStateDTO.addTopCard(cardDTO);
            loopCard = loopCard.getHigherNeighbour();
            positionCounter++;
        }
        positionCounter = 1;
        loopCard = startingCard;
        while(loopCard.getLowerNeighbour() !=null){ //get bottom cards
            CardDTO cardDTO = CardMapper.ConvertEntityToCardDTO(loopCard);
            cardDTO.setPosition(positionCounter);
            gameStateDTO.addBottomCard(cardDTO);
            loopCard = loopCard.getLowerNeighbour();
            positionCounter++;
        }

        CardDTO nextCard = CardMapper.ConvertEntityToCardDTO(this.nextCard);

        gameStateDTO.setGamestate(this.activeState.toString());
        gameStateDTO.setPlayertokens(1);
        gameStateDTO.setNextCardOnStack(nextCard);

        gameStateDTO.setPlayersturn(this.currentPlayer.getKey().getId());

        return gameStateDTO;

    }

    public EvaluatedGameStateDTO evaluate(){
        EvaluatedGameStateDTO evaluationState = new EvaluatedGameStateDTO();
        List<EvaluatedCardDTO> evaluatedTop = new ArrayList<>();
        List<EvaluatedCardDTO> evaluatedBottom = new ArrayList<>();
        List<EvaluatedCardDTO> evaluatedLeft = new ArrayList<>();
        List<EvaluatedCardDTO> evaluatedRight = new ArrayList<>();

        ValueCategory verticalCategory = this.getCurrentSettings().getVerticalValueCategory();
        ValueCategory horizontalCategory = this.getCurrentSettings().getHorizontalValueCategory();

        Card loopCard = activeBoard.getStartingCard();//start with startingcard
        int positionCounter = 1;
        //go up
        while(loopCard.getHigherNeighbour()!= null){
            try {
                boolean correct = verticalCategory.isPlacementCorrect(loopCard, loopCard.getHigherNeighbour());
                EvaluatedCardDTO evaluatedCardDTO = CardMapper.ConvertEntityToEvaluatedCardDTO(loopCard.getHigherNeighbour(), correct);
                evaluatedCardDTO.setPosition(positionCounter);
                evaluatedTop.add(evaluatedCardDTO);//add neighbour
            } catch (Exception e){}
            loopCard= loopCard.getHigherNeighbour();
            positionCounter++;
        }

        positionCounter = 1;
        loopCard = activeBoard.getStartingCard();//start with startingcard
        //go down
        while(loopCard.getLowerNeighbour()!= null){
            try {
                boolean correct = verticalCategory.isPlacementCorrect(loopCard, loopCard.getLowerNeighbour());
                EvaluatedCardDTO evaluatedCardDTO = CardMapper.ConvertEntityToEvaluatedCardDTO(loopCard.getLowerNeighbour(), correct);
                evaluatedCardDTO.setPosition(positionCounter);
                evaluatedBottom.add(evaluatedCardDTO);//add neighbour
                 }
            catch (Exception e) {           }

            loopCard= loopCard.getLowerNeighbour();
        }
        loopCard = activeBoard.getStartingCard();//start with startingcard


        positionCounter = 1;
        //go left
        while(loopCard.getLeftNeighbour()!= null){
            try {
                boolean correct = horizontalCategory.isPlacementCorrect(loopCard, loopCard.getLeftNeighbour());
                EvaluatedCardDTO evaluatedCardDTO = CardMapper.ConvertEntityToEvaluatedCardDTO(loopCard.getLeftNeighbour(), correct);
                evaluatedCardDTO.setPosition(positionCounter);
                evaluatedLeft.add(evaluatedCardDTO);//add neighbour
                            }
            catch (Exception e) {           }
            loopCard= loopCard.getLeftNeighbour();
        }
        loopCard = activeBoard.getStartingCard();//start with startingcard

        positionCounter = 1;
        //go right
        while(loopCard.getRightNeighbour()!= null){
            try {
                boolean correct = horizontalCategory.isPlacementCorrect(loopCard, loopCard.getRightNeighbour());
                EvaluatedCardDTO evaluatedCardDTO = CardMapper.ConvertEntityToEvaluatedCardDTO(loopCard.getRightNeighbour(), correct);
                evaluatedCardDTO.setPosition(positionCounter);
                evaluatedRight.add(evaluatedCardDTO);//add neighbour
                }
            catch (Exception e) {           }
            loopCard= loopCard.getRightNeighbour();
        }
        evaluationState.setTop(evaluatedTop);
        evaluationState.setBottom(evaluatedBottom);
        evaluationState.setLeft(evaluatedLeft);
        evaluationState.setRight(evaluatedRight);

        //evaluationState.setCards(evaluatedCards);
        evaluationState.setGamestate(this.activeState.toString());
        evaluationState.setPlayersturn(this.currentPlayer.getKey().getId());
        evaluationState.setNextCardOnStack(CardMapper.ConvertEntityToCardDTO(this.nextCard));
        return evaluationState;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Card getNextCard() {
        return nextCard;
    }

    public List<User> getJoinedPlayer() {
        return waitingForPlayers;
    }

    public Queue<Map.Entry<User, String>> getPlayers() {
        return players;
    }

    public Map.Entry<User, String> getCurrentPlayer() {
        return currentPlayer;
    }

    public GameSettings getCurrentSettings() {
        return currentSettings;
    }
}
