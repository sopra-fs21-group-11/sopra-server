package ch.uzh.ifi.hase.soprafs21.rest.socketDTO;

public class GameDoubtDTO {
    private int placedCard;
    private int doubtedCard;
    private long gameId;

    public int getPlacedCard() {
        return placedCard;
    }

    public void setPlacedCard(int placedCard) {
        this.placedCard = placedCard;
    }

    public long getGameId() {
        return gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    public int getDoubtedCard() {
        return doubtedCard;
    }

    public void setDoubtedCard(int doubtedCard) {
        this.doubtedCard = doubtedCard;
    }
}
