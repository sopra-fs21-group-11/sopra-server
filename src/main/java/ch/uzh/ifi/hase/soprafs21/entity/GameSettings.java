package ch.uzh.ifi.hase.soprafs21.entity;

import ch.uzh.ifi.hase.soprafs21.entity.ValueCategories.ECoordinateCategory;
import ch.uzh.ifi.hase.soprafs21.entity.ValueCategories.NCoordinateCategory;
import ch.uzh.ifi.hase.soprafs21.entity.ValueCategories.ValueCategory;
import org.hibernate.mapping.Value;

public class GameSettings {
    private int playersMin;
    private int playersMax;
    private int nrOfEvaluations;
    private int doubtCountdown;
    private int visibleAfterDoubtCountdown;
    private int playerTurnCountdown;
    private int evaluationCountdown;
    private int evaluationCountdownVisible;
    private int tokenGainOnCorrectGuess;
    private int tokenGainOnNearestGuess;
    private ValueCategory horizontalValueCategoryId;
    private ValueCategory verticalValueCategoryId;
    private int nrOfStartingTokens;
    private long deckId;
    private boolean settingsValid;
    private String remark;

    public GameSettings() {
        //default settings:
        this.playersMin = 2;
        this.playersMax = 6;
        this.nrOfEvaluations = 2;
        this.doubtCountdown = 10;
        this.visibleAfterDoubtCountdown = 5;
        this.playerTurnCountdown = 30;
        this.evaluationCountdown = 30;
        this.evaluationCountdownVisible = 30;
        this.tokenGainOnCorrectGuess = 2;
        this.tokenGainOnNearestGuess = 1;
        //has to be set by id. id is hardcoded in ValueCategories. This is still standardsetup
        this.horizontalValueCategoryId = new ECoordinateCategory();
        this.verticalValueCategoryId = new NCoordinateCategory();
        this.nrOfStartingTokens = 4;
        this.deckId = 1L;
        this.validateSettings();
    }

    public boolean validateSettings(){
        if(6 < playersMin || playersMin < 2){
            settingsValid = false;
            remark = "playersMin is invalid.";
        }
        else if(6 < playersMax || playersMax < 2){
            settingsValid = false;
            remark = "playersMax is invalid.";
        }
        else if(5 < nrOfEvaluations || nrOfEvaluations < 1){
            settingsValid = false;
            remark = "nrOfEvaluations is invalid.";
        }
        else if(300 < doubtCountdown || doubtCountdown < 1){
            settingsValid = false;
            remark = "doubtCountdown is invalid.";
        }
        else if(300 < visibleAfterDoubtCountdown || visibleAfterDoubtCountdown < 1){
            settingsValid = false;
            remark = "visibleAfterDoubtCountdown is invalid.";
        }
        else if(300 < playerTurnCountdown || playerTurnCountdown < 1){
            settingsValid = false;
            remark = "playerTurnCountdown is invalid.";
        }
        else if(300 < evaluationCountdown || evaluationCountdown < 1){
            settingsValid = false;
            remark = "evaluationCountdown is invalid.";
        }
        else if(300 < evaluationCountdownVisible || evaluationCountdownVisible < 1){
            settingsValid = false;
            remark = "evaluationCountdownVisible is invalid.";
        }
        else if(5 < tokenGainOnCorrectGuess || tokenGainOnCorrectGuess < 0){
            settingsValid = false;
            remark = "tokenGainOnCorrectGuess is invalid.";
        }
        else if(5 < tokenGainOnNearestGuess || tokenGainOnNearestGuess < 0){
            settingsValid = false;
            remark = "tokenGainOnNearestGuess is invalid.";
        }
        else if(20 < nrOfStartingTokens || nrOfStartingTokens < 1){
            settingsValid = false;
            remark = "nrOfStartingTokens is invalid.";
        }
        else{
            settingsValid = true;
            remark = "";
        }
        return settingsValid;
    }

    public int getTokenGainOnNearestGuess() {
        return tokenGainOnNearestGuess;
    }

    public void setTokenGainOnNearestGuess(int tokenGainOnNearestGuess) { this.tokenGainOnNearestGuess = tokenGainOnNearestGuess; }

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

    public int getNrOfEvaluations() { return nrOfEvaluations; }

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

    public int getNrOfStartingTokens() {
        return nrOfStartingTokens;
    }

    public void setNrOfStartingTokens(int nrOfStartingTokens) {
        this.nrOfStartingTokens = nrOfStartingTokens;
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

    public ValueCategory getVerticalValueCategory() { return verticalValueCategoryId; }

    public ValueCategory getHorizontalValueCategory() {
        return horizontalValueCategoryId;
    }

    public int getEvaluationCountdown() {
        return evaluationCountdown;
    }

    public void setEvaluationCountdown(int evaluationCountdown) {
        this.evaluationCountdown = evaluationCountdown;
    }

    public int getEvaluationCountdownVisible() {
        return evaluationCountdownVisible;
    }

    public void setEvaluationCountdownVisible(int evaluationCountdownVisible) {
        this.evaluationCountdownVisible = evaluationCountdownVisible;
    }

    public long getDeckId() {
        return deckId;
    }

    public void setDeckId(long deckId) {
        this.deckId = deckId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public boolean isSettingsValid() {
        return settingsValid;
    }
}

