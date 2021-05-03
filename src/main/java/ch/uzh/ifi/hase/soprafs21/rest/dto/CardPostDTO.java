package ch.uzh.ifi.hase.soprafs21.rest.dto;

public class CardPostDTO {
    private String name;
    private float nCoordinate;
    private float eCoordinate;
    private long population;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPopulation() {
        return population;
    }

    public void setPopulation(long population) {
        this.population = population;
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
