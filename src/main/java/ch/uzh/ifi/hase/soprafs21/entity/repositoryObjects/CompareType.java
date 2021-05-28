package ch.uzh.ifi.hase.soprafs21.entity.repositoryObjects;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "COMPARETYPES")
public class CompareType implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private long id;

    @Column(nullable = false)
    private String name;

    @ManyToMany(mappedBy = "compareTypes")
    private List<Deck> inDeck;

    @Column
    private String description;

    public CompareType() { }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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
}
