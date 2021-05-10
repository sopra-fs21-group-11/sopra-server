package ch.uzh.ifi.hase.soprafs21.entity;

import java.util.ArrayList;
import java.util.List;

public class GameLobby {
    //private String gamePassword;
    private String name;
    private long id;
    //private boolean isPublic;
    private List<User> players;
    private User host;
    private GameSettings settings;

    public GameLobby(User host) {
        this.host= host;
        this.players = new ArrayList<>();
        players.add(host);
        this.settings = new GameSettings();//Init game with default settings.
        this.name = "New Game";

    }

    public Game StartGame(Deck deck ){
        Game newGame = new Game(this, deck);
        return newGame;
    }

    public GameLobby removePlayer(User player){
        for(User user : players){
            if(user.getId() == player.getId()){
                players.remove(user);
                return this;
            }
        }
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void addPlayer(User user){
        this.players.add(user);
    }

    public List<User> getPlayers() {
        return players;
    }

    public User getHost() {
        return host;
    }

    public GameSettings getSettings() {
        return settings;
    }

    public void setSettings(GameSettings settings) {
        this.settings = settings;
    }

    public long getHostId(){
        return this.host.getId();
    }
}
