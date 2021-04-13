package ch.uzh.ifi.hase.soprafs21.entity.RepositoryObjects;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "DECKS")
public class Deck implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Long size;

    @Column(nullable = false)
    private String name;

}
