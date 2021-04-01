package ch.uzh.ifi.hase.soprafs21.entity;

public class GameSettings {
    private int playersMin;
    private int playersMax;
    private int nrOfEvaluations;
    private int doubtCountdown;
    private int visibleAfterDoubtCountdown;
    private int playerTurnCountdown;
    private int tokenGainOnCorrectGuess;
    private int tokenGainOnNearestGuess;
    private long horizontalValueCategoryId;
    private long verticalValueCategoryId;


    public GameSettings() {
        //default settings:
        this.playersMin = 2;
        this.playersMax = 6;
        this.nrOfEvaluations = 2;
        this.doubtCountdown = 30;
        this.visibleAfterDoubtCountdown = 5;
        this.playerTurnCountdown = 30;
        this.tokenGainOnCorrectGuess = 2;
        this.tokenGainOnNearestGuess = 1;
        this.horizontalValueCategoryId = 1;
        this.verticalValueCategoryId = 2;
    }

    public int getTokenGainOnNearestGuess() {
        return tokenGainOnNearestGuess;
    }

    public void setTokenGainOnNearestGuess(int tokenGainOnNearestGuess) {
        this.tokenGainOnNearestGuess = tokenGainOnNearestGuess;
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


}
