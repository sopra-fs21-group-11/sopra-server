package ch.uzh.ifi.hase.soprafs21.rest.dto;

import ch.uzh.ifi.hase.soprafs21.entity.RepositoryObjects.Deck;


public class CardGetDTO {

    private Long id;
    private String name;
    private float nCoordinate;
    private float eCoordinate;
    private long population;

    public Long getId() {
        return id;
    }

    public long getPopulation() {
        return population;
    }

    public void setPopulation(long population) {
        this.population = population;
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
