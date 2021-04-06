package ch.uzh.ifi.hase.soprafs21.entity;

import ch.uzh.ifi.hase.soprafs21.constant.GameState;

import java.util.*;

public class Game {
    private Queue<Map.Entry<User, String>> players;
    private long id;
    private Board activeBoard;
    private User currentPlayer;
    private GameState activeState;
    private boolean hasWinner = false;
    private Deck deckStack;
    private long hostPlayerId;
    private List<User> waitingForPlayers;

    private final GameSettings currentSettings;

    public Game(GameLobby lobby){
        //set up the game-object with all details of the lobby:
        this.waitingForPlayers = lobby.getPlayers();
        this.currentPlayer = lobby.getHost(); //host always starts.
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
        if(!this.waitingForPlayers.contains(user)){
            return false; //already joined or not in lobby
        }
        this.waitingForPlayers.remove(user);
        this.players.add(new AbstractMap.SimpleEntry<User, String>(user, sessionId));//add token/user-combo to our players queue.
        return true;
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
}
