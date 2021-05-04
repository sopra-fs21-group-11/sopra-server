package ch.uzh.ifi.hase.soprafs21.entity;

import ch.uzh.ifi.hase.soprafs21.entity.ValueCategories.ECoordinateCategory;
import ch.uzh.ifi.hase.soprafs21.entity.ValueCategories.NCoordinateCategory;
import ch.uzh.ifi.hase.soprafs21.entity.ValueCategories.ValueCategory;
import org.hibernate.mapping.Value;

public class GameSettings {
    private int playersMin;
    private int playersMax;
    private int cardsBeforeEvaluation;
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

    public GameSettings() {
        //default settings:
        this.playersMin = 2;
        this.playersMax = 6;
        this.cardsBeforeEvaluation = 4;
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
        this.deckId=1;
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

    public ValueCategory getHorizontalValueCategory() {
        return horizontalValueCategoryId;
    }

    public void setHorizontalValueCategory(ValueCategory horizontalValueCategoryId) {
        this.horizontalValueCategoryId = horizontalValueCategoryId;
    }

    public ValueCategory getVerticalValueCategory() {
        return verticalValueCategoryId;
    }

    public void setVerticalValueCategory(ValueCategory verticalValueCategoryId) {
        this.verticalValueCategoryId = verticalValueCategoryId;
    }

    public int getCardsBeforeEvaluation() {
        return cardsBeforeEvaluation;
    }

    public void setCardsBeforeEvaluation(int cardsBeforeEvaluation) {
        this.cardsBeforeEvaluation = cardsBeforeEvaluation;
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
}
