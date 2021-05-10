package ch.uzh.ifi.hase.soprafs21.entity.Cards;


public abstract class Card {
    private float nsCoordinates;
    private float ewCoordinates;
    private String locationName;
    private long cardId;
    private Card lowerNeighbour;
    private Card higherNeighbour;
    private Card leftNeighbour;
    private Card rightNeighbour;

    public float getNsCoordinates() {
        return nsCoordinates;
    }

    public void setNsCoordinates(float nsCoordinates) {
        this.nsCoordinates = nsCoordinates;
    }

    public float getEwCoordinates() {
        return ewCoordinates;
    }

    public void setEwCoordinates(float ewCoordinates) {
        this.ewCoordinates = ewCoordinates;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public long getCardId() {
        return cardId;
    }

    public void setCardId(long cardId) {
        this.cardId = cardId;
    }

    public Card getLowerNeighbour() {
        return lowerNeighbour;
    }

    public void setLowerNeighbour(Card lowerNeighbour) {
        this.lowerNeighbour = lowerNeighbour;
    }

    public Card getHigherNeighbour() {
        return higherNeighbour;
    }

    public void setHigherNeighbour(Card higherNeighbour) {
        this.higherNeighbour = higherNeighbour;
    }

    public Card getLeftNeighbour() {
        return leftNeighbour;
    }

    public void setLeftNeighbour(Card leftNeighbour) {
        this.leftNeighbour = leftNeighbour;
    }

    public Card getRightNeighbour() {
        return rightNeighbour;
    }

    public void setRightNeighbour(Card rightNeighbour) {
        this.rightNeighbour = rightNeighbour;
    }
}
