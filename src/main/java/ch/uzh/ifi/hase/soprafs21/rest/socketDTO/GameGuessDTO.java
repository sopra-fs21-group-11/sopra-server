package ch.uzh.ifi.hase.soprafs21.rest.socketDTO;

public class GameGuessDTO {
    private int nrOfWrongPlacedCards;
    private long gameId;

    public int getNrOfWrongPlacedCards() {
        return nrOfWrongPlacedCards;
    }

    public void setNrOfWrongPlacedCards(int nrOfWrongPlacedCards) {
        this.nrOfWrongPlacedCards = nrOfWrongPlacedCards;
    }

    public long getGameId() {
        return gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }
}
