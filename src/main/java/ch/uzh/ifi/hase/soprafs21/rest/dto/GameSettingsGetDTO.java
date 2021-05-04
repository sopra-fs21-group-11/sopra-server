package ch.uzh.ifi.hase.soprafs21.rest.dto;
import ch.uzh.ifi.hase.soprafs21.entity.ValueCategories.ValueCategory;

public class GameSettingsGetDTO {
    private int playersMin;
    private int playersMax;
    //private int cardsBeforeEvaluation;
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

    //public int getCardsBeforeEvaluation() { return cardsBeforeEvaluation; }

    //public void setCardsBeforeEvaluation(int cardsBeforeEvaluation) { this.cardsBeforeEvaluation = cardsBeforeEvaluation; }

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

    public void setVisibleAfterDoubtCountdown(int visibleAfterDoubtCountdown) { this.visibleAfterDoubtCountdown = visibleAfterDoubtCountdown; }

    public int getPlayerTurnCountdown() {
        return playerTurnCountdown;
    }

    public void setPlayerTurnCountdown(int playerTurnCountdown) {
        this.playerTurnCountdown = playerTurnCountdown;
    }

    public int getTokenGainOnCorrectGuess() {
        return tokenGainOnCorrectGuess;
    }

    public void setTokenGainOnCorrectGuess(int tokenGainOnCorrectGuess) { this.tokenGainOnCorrectGuess = tokenGainOnCorrectGuess; }

    public ValueCategory getHorizontalValueCategoryId() {
        return horizontalValueCategoryId;
    }

    public void setHorizontalValueCategoryId(ValueCategory horizontalValueCategoryId) { this.horizontalValueCategoryId = horizontalValueCategoryId; }

    public ValueCategory getVerticalValueCategoryId() {
        return verticalValueCategoryId;
    }

    public void setVerticalValueCategoryId(ValueCategory verticalValueCategoryId) { this.verticalValueCategoryId = verticalValueCategoryId; }

    public int getNrOfStartingTokens() {
        return nrOfStartingTokens;
    }

    public void setNrOfStartingTokens(int nrOfStartingTokens) {
        this.nrOfStartingTokens = nrOfStartingTokens;
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

    public void setEvaluationCountdownVisible(int evaluationCountdownVisible) { this.evaluationCountdownVisible = evaluationCountdownVisible; }

    public long getDeckId() {
        return deckId;
    }

    public void setDeckId(long deckId) {
        this.deckId = deckId;
    }
}
