package ch.uzh.ifi.hase.soprafs21.rest.socketDTO;

import ch.uzh.ifi.hase.soprafs21.entity.Cards.Card;

public class DoubtResultDTO {
    private CardDTO referenceCard;
    private CardDTO doubtedCard;
    private boolean isDoubtRightous;

    public CardDTO getReferenceCard() {
        return referenceCard;
    }

    public void setReferenceCard(CardDTO referenceCard) {
        this.referenceCard = referenceCard;
    }

    public CardDTO getDoubtedCard() {
        return doubtedCard;
    }

    public void setDoubtedCard(CardDTO doubtedCard) {
        this.doubtedCard = doubtedCard;
    }

    public boolean isDoubtRightous() {
        return isDoubtRightous;
    }

    public void setDoubtRightous(boolean doubtRightous) {
        isDoubtRightous = doubtRightous;
    }
}
