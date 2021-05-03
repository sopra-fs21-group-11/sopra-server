package ch.uzh.ifi.hase.soprafs21.entity.RepositoryObjects;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "CARDS")
public class Card implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;


    @ManyToMany(mappedBy="cards")
    private List<Deck> decks; //a card can be in multiple decks.

    @Column
    private float nCoordinate;

    @Column
    private float eCoordinate;

    @Column(nullable = false)
    private String name;


    public Card() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Deck> getDecks() {
        return decks;
    }

    public void setDeck(List<Deck> decks) {
        this.decks = decks;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getnCoordinate() {
        return nCoordinate;
    }

    public void setnCoordinate(float nCoordinate) {
        this.nCoordinate = nCoordinate;
    }

    public float geteCoordinate() {
        return eCoordinate;
    }

    public void seteCoordinate(float eCoordinate) {
        this.eCoordinate = eCoordinate;
    }
}
