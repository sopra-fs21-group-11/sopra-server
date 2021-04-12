package ch.uzh.ifi.hase.soprafs21.entity;

import ch.uzh.ifi.hase.soprafs21.service.GameService;

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

    public Game StartGame(){
        Game newGame = new Game(this);
        return newGame;
    }

    public void removePlayer(User player){
        this.players.remove(player);
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
