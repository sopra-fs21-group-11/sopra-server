package ch.uzh.ifi.hase.soprafs21.rest.dto;

import java.util.List;

public class DeckPutDTO {
    private String name;
    private String description;
    private List<Long> cards;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Long> getCards() {
        return cards;
    }

    public void setCards(List<Long> cards) {
        this.cards = cards;
    }
}
