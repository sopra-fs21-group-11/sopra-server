package ch.uzh.ifi.hase.soprafs21.entity;

import ch.uzh.ifi.hase.soprafs21.constant.GameState;
import ch.uzh.ifi.hase.soprafs21.entity.Cards.Card;
import ch.uzh.ifi.hase.soprafs21.entity.ValueCategories.ValueCategory;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.CardMapper;
import ch.uzh.ifi.hase.soprafs21.rest.socketDTO.CardDTO;
import ch.uzh.ifi.hase.soprafs21.rest.socketDTO.EvaluatedCardDTO;
import ch.uzh.ifi.hase.soprafs21.rest.socketDTO.EvaluatedGameStateDTO;
import ch.uzh.ifi.hase.soprafs21.rest.socketDTO.GameStateDTO;
import ch.uzh.ifi.hase.soprafs21.service.CountdownHelper;
import ch.uzh.ifi.hase.soprafs21.service.GameService;
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

    private GameService gameService;

    private CountdownHelper doubtCountdown;
    private Thread visibleCountdown;
    private Thread turnCountdown;


    public Game(GameLobby lobby){
        //set up the game-object with all details of the lobby:
        this.waitingForPlayers = lobby.getPlayers();
        this.currentSettings = lobby.getSettings();
        this.hostPlayerId = lobby.getHostId();
        this.activeState = GameState.CARDPLACEMENT;
        this.id = lobby.getId();

        this.verticalValueCategory = lobby.getSettings().getVerticalValueCategory();
        this.horizontalValueCategory = lobby.getSettings().getHorizontalValueCategory();


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
        //start doubtingphase after a player placed a card:
        doubtingPhase();
    }

    private void doubtingPhase(){
        CountdownHelper countdown = new CountdownHelper(currentSettings.getDoubtCountdown(), this);
        this.doubtCountdown = countdown;
        countdown.start();
        //doubt incoming because loop exit (anyone else has stopped this.countdownRunning):
        //do nothing while visibleAfterDoubt

        //continue with next turn.


    }

    public void performDoubt(String sessionId, int placedCard, int doubtedCard){
        if(!doubtCountdown.isAlive()){//countdown isnt running -> we dont accept.
            return;
        }
        User doubtingUser = null;
        for(var user : players){
            if(user.getValue() == sessionId){
                doubtingUser = user.getKey();
            }
        }
        User doubtedUser = currentPlayer.getKey();
        if(!evaluateDoubt(placedCard, doubtedCard)){
            //doubt is rightous -> remove and handle tokens
            //first get card obj from id (I know this could be refactored beautiful...
            Card cardToRemove = null;
            for(Card card : activeBoard.getVerticalAxis()){
                if(card.getCardId() == doubtedCard){
                    cardToRemove = card;
                }
            }
            if(cardToRemove == null){
                for(Card card : activeBoard.getHorizontalAxis()){
                    if(card.getCardId() == doubtedCard){
                        cardToRemove = card;
                    }
                }
            }
            //remove card:
            activeBoard.removeCard(cardToRemove);
            doubtedUser.currentToken--;
            doubtingUser.currentToken++;
        } else{//doubt is wrong
            doubtedUser.currentToken++;
            doubtingUser.currentToken--;
        }
        doubtCountdown.doStop();
        //doubt has occured and we have to start the visible countdown:
        visibleCountdown = new CountdownHelper(currentSettings.getVisibleAfterDoubtCountdown(), this);
        visibleCountdown.start();
    }
    public void startTurnCd(){
        //start a new turn cd and send state
        turnCountdown = new CountdownHelper(currentSettings.getPlayerTurnCountdown(), this);
        gameService.sendGameStateToUsers(id);
        return;
    }


    private boolean evaluateDoubt(int cardId, int questionableCardId){
        //horizontal check:
        for(Card card : activeBoard.getHorizontalAxis()){
            if(card.getCardId() == cardId){
                for(Card card2 : activeBoard.getHorizontalAxis()){
                    if(questionableCardId == card2.getCardId()){
                        //both cards are in vertical axis:
                        try{
                            return horizontalValueCategory.isPlacementCorrect(card, card2);
                        }catch (Exception ex){}
                    }
                }
                break; //second card is not horizontal -> skip the rest since we have no duplicates
            }

        }
        //vertical check:
        for(Card card : activeBoard.getVerticalAxis()){
            if(card.getCardId() == cardId){
                for(Card card2 : activeBoard.getVerticalAxis()){
                    if(questionableCardId == card2.getCardId()){
                        //both cards are in vertical axis:
                        try{
                            return verticalValueCategory.isPlacementCorrect(card, card2);
                        }catch (Exception ex){}
                    }
                }
                break; //second card is not horizontal -> skip the rest since we have no duplicates
            }

        }
        return true; //this is bullshit :/
    }


    public GameStateDTO convertToDTO(){
        GameStateDTO gameStateDTO = new GameStateDTO();
        //add starting card first.
        Card startingCard = this.activeBoard.getStartingCard();
        CardDTO startingCardDTO = CardMapper.ConvertEntityToCardDTO(startingCard);
        gameStateDTO.setStartingCard(startingCardDTO);

        gameStateDTO.addCard(startingCardDTO);
        for(Card card : this.activeBoard.getHorizontalAxis()){
            if(card.getCardId()==startingCard.getCardId()){
                continue;
            }
            CardDTO nextCardonHorizontalAxisDTO = CardMapper.ConvertEntityToCardDTO(card);
            gameStateDTO.addCard(nextCardonHorizontalAxisDTO);

        }
        for(Card card : this.activeBoard.getVerticalAxis()){
            if(card.getCardId()==startingCard.getCardId()){
                continue;
            }
            CardDTO nextCardonVerticalAxisDTO = CardMapper.ConvertEntityToCardDTO(card);
            gameStateDTO.addCard(nextCardonVerticalAxisDTO);

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
        List<EvaluatedCardDTO> evaluatedCards = new ArrayList<>();
        ValueCategory verticalCategory = this.getCurrentSettings().getVerticalValueCategory();
        ValueCategory horizontalCategory = this.getCurrentSettings().getHorizontalValueCategory();

        Card loopCard = activeBoard.getStartingCard();//start with startingcard
        //go up
        while(loopCard.getHigherNeighbour()!= null){
            try {
                boolean correct = verticalCategory.isPlacementCorrect(loopCard, loopCard.getHigherNeighbour());
                evaluatedCards.add(CardMapper.ConvertEntityToEvaluatedCardDTO(loopCard.getHigherNeighbour(), correct));//add neighbour
            } catch (Exception e){}
            loopCard= loopCard.getHigherNeighbour();
        }


        loopCard = activeBoard.getStartingCard();//start with startingcard
        //go down
        while(loopCard.getLowerNeighbour()!= null){
            try {
                boolean correct = verticalCategory.isPlacementCorrect(loopCard, loopCard.getLowerNeighbour());
                evaluatedCards.add(CardMapper.ConvertEntityToEvaluatedCardDTO(loopCard.getLowerNeighbour(), correct));//add neighbour
            }
            catch (Exception e) {           }

            loopCard= loopCard.getLowerNeighbour();
        }
        loopCard = activeBoard.getStartingCard();//start with startingcard



        //go left
        while(loopCard.getLeftNeighbour()!= null){
            try {
                boolean correct = horizontalCategory.isPlacementCorrect(loopCard, loopCard.getLeftNeighbour());
                evaluatedCards.add(CardMapper.ConvertEntityToEvaluatedCardDTO(loopCard.getLeftNeighbour(), correct));//add neighbour
            }
            catch (Exception e) {           }
            loopCard= loopCard.getLeftNeighbour();
        }
        loopCard = activeBoard.getStartingCard();//start with startingcard


        //go right
        while(loopCard.getRightNeighbour()!= null){
            try {
                boolean correct = horizontalCategory.isPlacementCorrect(loopCard, loopCard.getRightNeighbour());
                evaluatedCards.add(CardMapper.ConvertEntityToEvaluatedCardDTO(loopCard.getRightNeighbour(), correct));//add neighbour
            }
            catch (Exception e) {           }
            loopCard= loopCard.getRightNeighbour();
        }

        evaluationState.setCards(evaluatedCards);
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

    public void setGameService(GameService gameService) {
        this.gameService = gameService;
    }

}
