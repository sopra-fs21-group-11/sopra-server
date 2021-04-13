package ch.uzh.ifi.hase.soprafs21.entity.RepositoryObjects;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "CARDS")
public class Cards implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @ForeignKey


    @Column(nullable = false)
    private Long size;

    @Column(nullable = false)
    private String name;

}
