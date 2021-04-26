package ch.uzh.ifi.hase.soprafs21.rest.dto;

import java.util.List;

public class GameGetDTO {
    private long id;
    private int playersMin;
    private int playersMax;
    private int nrOfEvaluations;
    private int doubtCountdown;
    private int visibleAfterDoubtCountdown;
    private int playerTurnCountdown;
    private int tokenGainOnCorrectGuess;
    private int tokenGainOnNearestGuess;
    private int nrOfStartingTokens;

    private long horizontalValueCategoryId;
    private long verticalValueCategoryId;
    private String name;
    private long hostId;
    private List<Long> players;
    private boolean gameStarted;

    public int getTokenGainOnNearestGuess() {
        return tokenGainOnNearestGuess;
    }

    public void setTokenGainOnNearestGuess(int tokenGainOnNearestGuess) {
        this.tokenGainOnNearestGuess = tokenGainOnNearestGuess;
    }

    public List<Long> getPlayers() {
        return players;
    }

    public void setPlayers(List<Long> players) {
        this.players = players;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getPlayersMin() {
        return playersMin;
    }

    public void setPlayersMin(int playersMin) {
        this.playersMin = playersMin;
    }

    public int getPlayersMax() {
        return playersMax;
    }

    public void setPlayersMax(int playersMax) {
        this.playersMax = playersMax;
    }

    public int getNrOfEvaluations() {
        return nrOfEvaluations;
    }

    public void setNrOfEvaluations(int nrOfEvaluations) {
        this.nrOfEvaluations = nrOfEvaluations;
    }

    public int getDoubtCountdown() {
        return doubtCountdown;
    }

    public void setDoubtCountdown(int doubtCountdown) {
        this.doubtCountdown = doubtCountdown;
    }

    public int getVisibleAfterDoubtCountdown() {
        return visibleAfterDoubtCountdown;
    }

    public void setVisibleAfterDoubtCountdown(int visibleAfterDoubtCountdown) {
        this.visibleAfterDoubtCountdown = visibleAfterDoubtCountdown;
    }

    public int getPlayerTurnCountdown() {
        return playerTurnCountdown;
    }

    public void setPlayerTurnCountdown(int playerTurnCountdown) {
        this.playerTurnCountdown = playerTurnCountdown;
    }

    public int getTokenGainOnCorrectGuess() {
        return tokenGainOnCorrectGuess;
    }

    public void setTokenGainOnCorrectGuess(int tokenGainOnCorrectGuess) {
        this.tokenGainOnCorrectGuess = tokenGainOnCorrectGuess;
    }

    public long getHorizontalValueCategoryId() {
        return horizontalValueCategoryId;
    }

    public void setHorizontalValueCategoryId(long horizontalValueCategoryId) {
        this.horizontalValueCategoryId = horizontalValueCategoryId;
    }

    public long getVerticalValueCategoryId() {
        return verticalValueCategoryId;
    }

    public void setVerticalValueCategoryId(long verticalValueCategoryId) {
        this.verticalValueCategoryId = verticalValueCategoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getHostId() {
        return hostId;
    }

    public void setHostId(long hostId) {
        this.hostId = hostId;
    }

    public int getNrOfStartingTokens() {
        return nrOfStartingTokens;
    }

    public void setNrOfStartingTokens(int nrOfStartingTokens) {
        this.nrOfStartingTokens = nrOfStartingTokens;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }
}
