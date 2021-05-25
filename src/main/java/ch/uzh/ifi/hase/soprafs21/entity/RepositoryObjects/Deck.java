package ch.uzh.ifi.hase.soprafs21.entity.RepositoryObjects;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "DECKS")
public class Deck implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private int size;

    @Column(nullable = false)
    private String name;

    @ManyToMany
    private List<Card> cards;

    @Column
    private String description;

    @Column
    private Long createdBy;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "compareType_deck",
    joinColumns = @JoinColumn(name="compareType_id", referencedColumnName = "id"),
    inverseJoinColumns = @JoinColumn(name="deck_id", referencedColumnName = "id"))
    private List<CompareType> compareTypes;

    @Column
    private boolean readyToPlay;

    public Deck() {
        size = 0;
        cards = new ArrayList<>();
    }

    public void addCard(Card card){
        cards.add(card);
        size++;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public boolean isReadyToPlay() {
        return readyToPlay;
    }

    public void setReadyToPlay(boolean readyToPlay) {
        this.readyToPlay = readyToPlay;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<CompareType> getCompareTypes() {
        return compareTypes;
    }

    public void setCompareTypes(List<CompareType> compareTypes) {
        this.compareTypes = compareTypes;
    }
}
