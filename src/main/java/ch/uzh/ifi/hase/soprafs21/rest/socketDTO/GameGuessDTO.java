package ch.uzh.ifi.hase.soprafs21.rest.socketDTO;

public class GameGuessDTO {
    private String nrOfWrongPlacedCards;
    private long gameId;

    public String getNrOfWrongPlacedCards() {
        return nrOfWrongPlacedCards;
    }

    public void setNrOfWrongPlacedCards(String nrOfWrongPlacedCards) {
        this.nrOfWrongPlacedCards = nrOfWrongPlacedCards;
    }

    public long getGameId() {
        return gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }
}
