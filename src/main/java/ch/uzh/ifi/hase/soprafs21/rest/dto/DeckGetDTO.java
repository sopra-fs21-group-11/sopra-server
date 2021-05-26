package ch.uzh.ifi.hase.soprafs21.rest.dto;

import java.util.ArrayList;
import java.util.List;

public class DeckGetDTO {

    private Long id;
    private String name;
    private List<CardGetDTO> cards;
    private String description;
    private List<CompareTypeGetDTO> compareTypes;
    private int size;
    private boolean readyToPlay;
    private long createdBy;

    public long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(long createdBy) {
        this.createdBy = createdBy;
    }

    public boolean isReadyToPlay() {
        return readyToPlay;
    }

    public void setReadyToPlay(boolean readyToPlay) {
        this.readyToPlay = readyToPlay;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CardGetDTO> getCards() {
        return cards;
    }

    public void setCards(List<CardGetDTO> cards) {
        this.cards = cards;
    }

    public void addCard(CardGetDTO cardGetDTO){
        if(cards == null){
            cards = new ArrayList<>();
        }
        cards.add(cardGetDTO);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<CompareTypeGetDTO> getCompareTypes() {
        return compareTypes;
    }

    public void setCompareTypes(List<CompareTypeGetDTO> compareTypes) {
        this.compareTypes = compareTypes;
    }
}
