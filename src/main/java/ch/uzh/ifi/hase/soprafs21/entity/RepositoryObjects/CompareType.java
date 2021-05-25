package ch.uzh.ifi.hase.soprafs21.entity.RepositoryObjects;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "COMPARETYPES")
public class CompareType implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    public CompareType() {
    }

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
