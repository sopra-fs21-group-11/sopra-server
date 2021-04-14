package ch.uzh.ifi.hase.soprafs21.entity;

import ch.uzh.ifi.hase.soprafs21.constant.GameState;
import ch.uzh.ifi.hase.soprafs21.entity.Cards.Card;
import ch.uzh.ifi.hase.soprafs21.rest.socketDTO.CardDTO;
import ch.uzh.ifi.hase.soprafs21.rest.socketDTO.GameStateDTO;
import org.springframework.beans.factory.annotation.Autowired;
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

        //We set the starting-card right away:
        this.activeBoard = new Board(deckStack.pop());

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

    public void performTurn(long userid, Card cardToPlace){
        if(currentPlayer.getKey().getId() != userid){
            return;
        }
        players.add(currentPlayer);
        currentPlayer = players.remove();
        activeBoard.placeCard(deckStack.pop(), -1,"horizontal");
        activeBoard.placeCard(deckStack.pop(), 1,"horizontal");
        //activeBoard.placeCard(deckStack.pop(), -1,"horizontal");



    }

    public GameStateDTO convertToDTO(){
        GameStateDTO gameStateDTO = new GameStateDTO();
        //add starting card first.
        Card startingCard = this.activeBoard.getStartingCard();
        CardDTO startingCardDTO = new CardDTO();
        startingCardDTO.setId(startingCard.getCardId());
        startingCardDTO.setLowerNeighbour(((startingCard.getLowerNeighbour() != null) ? startingCard.getLowerNeighbour().getCardId() : 0));
        startingCardDTO.setHigherNeighbour(((startingCard.getHigherNeighbour() != null) ? startingCard.getHigherNeighbour().getCardId() : 0));
        startingCardDTO.setLeftNeighbour(((startingCard.getLeftNeighbour() != null) ? startingCard.getLeftNeighbour().getCardId() : 0));
        startingCardDTO.setRightNeighbour(((startingCard.getRightNeighbour() != null) ? startingCard.getRightNeighbour().getCardId() : 0));
        startingCardDTO.setNcoord(startingCard.getNsCoordinates());
        startingCardDTO.setEcoord(startingCard.getEwCoordinates());
        startingCardDTO.setName(startingCard.getLocationName());
        /*startingCardDTO.setPopulation(0);
        startingCardDTO.setArea(1.2f);
        startingCardDTO.setCanton("ZH");
        startingCardDTO.setHeight(2002);*/

        gameStateDTO.addCard(startingCardDTO);
        for(Card card : this.activeBoard.getHorizontalAxis()){
            if(card.getCardId()==startingCard.getCardId()){
                continue;
            }
            CardDTO nextCardonHorizontalAxisDTO = new CardDTO();
            nextCardonHorizontalAxisDTO.setId(card.getCardId());
            nextCardonHorizontalAxisDTO.setLeftNeighbour(((card.getLeftNeighbour() != null) ? card.getLeftNeighbour().getCardId() : 0));
            nextCardonHorizontalAxisDTO.setRightNeighbour(((card.getRightNeighbour() != null) ? card.getRightNeighbour().getCardId() : 0));
            nextCardonHorizontalAxisDTO.setNcoord(card.getNsCoordinates());
            nextCardonHorizontalAxisDTO.setEcoord(card.getEwCoordinates());
            nextCardonHorizontalAxisDTO.setName(card.getLocationName());
            gameStateDTO.addCard(nextCardonHorizontalAxisDTO);

        }
        for(Card card : this.activeBoard.getVerticalAxis()){
            if(card.getCardId()==startingCard.getCardId()){
                continue;
            }
            CardDTO nextCardonVerticalAxisDTO = new CardDTO();
            nextCardonVerticalAxisDTO.setId(card.getCardId());
            nextCardonVerticalAxisDTO.setLowerNeighbour(((card.getLowerNeighbour() != null) ? card.getLowerNeighbour().getCardId() : 0));
            nextCardonVerticalAxisDTO.setHigherNeighbour(((card.getHigherNeighbour() != null) ? card.getHigherNeighbour().getCardId() : 0));
            nextCardonVerticalAxisDTO.setNcoord(card.getNsCoordinates());
            nextCardonVerticalAxisDTO.setEcoord(card.getEwCoordinates());
            nextCardonVerticalAxisDTO.setName(card.getLocationName());
            gameStateDTO.addCard(nextCardonVerticalAxisDTO);

        }



        CardDTO nextCard = new CardDTO();
        /*nextCard.setId(card.getCardId());
        nextCard.setNcoord(card.getNsCoordinates());
        nextCard.setEcoord(card.getEwCoordinates());
        nextCard.setName(card.getLocationName());*/

        gameStateDTO.setGamestate(this.activeState.toString());
        gameStateDTO.setPlayertokens(1);
        gameStateDTO.setNextCardOnStack(nextCard);

        gameStateDTO.setPlayersturn(this.currentPlayer.getKey().getId());

        return gameStateDTO;

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<User> getJoinedPlayer() {
        return waitingForPlayers;
    }

    public Queue<Map.Entry<User, String>> getPlayers() {
        return players;
    }


    public GameSettings getCurrentSettings() {
        return currentSettings;
    }
}
