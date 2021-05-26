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

    @ManyToMany(mappedBy = "cards")
    private List<Deck> inDeck;

    @Column
    private float nCoordinate;

    @Column
    private float eCoordinate;

    @Column
    private long population;

    @Column(nullable = false)
    private String name;

    public Card() { }

    public List<Deck> getInDeck() {
        return inDeck;
    }

    public void setInDeck(List<Deck> inDeck) {
        this.inDeck = inDeck;
    }

    public long getPopulation() {
        return population;
    }

    public void setPopulation(long population) {
        this.population = population;
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
