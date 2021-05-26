package ch.uzh.ifi.hase.soprafs21.rest.socket_dto;

import java.util.ArrayList;
import java.util.List;

public class DoubtResultDTO {
    private CardDTO referenceCard;
    private CardDTO doubtedCard;
    private boolean isDoubtRightous;
    private List<Long> doubtedCardNeighbours;

    public CardDTO getReferenceCard() {
        doubtedCardNeighbours = new ArrayList<>();
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

    public List<Long> getDoubtedCardNeighbours() {
        return doubtedCardNeighbours;
    }

    public void setDoubtedCardNeighbours(List<Long> doubtedCardNeighbours) {
        this.doubtedCardNeighbours = doubtedCardNeighbours;
    }
}
