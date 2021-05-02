package ch.uzh.ifi.hase.soprafs21.rest.socketDTO;

public class CardDTO {
    private long id;
    private long lowerNeighbour;
    private long higherNeighbour;
    private long rightNeighbour;
    private long leftNeighbour;
    private float ncoord;
    private float ecoord;
    private String name;
    private long population;
    private float area;
    private long height;
    private String canton;
    private int position;

    public CardDTO() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getLowerNeighbour() {
        return lowerNeighbour;
    }

    public void setLowerNeighbour(long lowerNeighbour) {
        this.lowerNeighbour = lowerNeighbour;
    }

    public long getHigherNeighbour() {
        return higherNeighbour;
    }

    public long getLeftNeighbour() {
        return leftNeighbour;
    }

    public void setLeftNeighbour(long leftNeighbour) {
        this.leftNeighbour = leftNeighbour;
    }

    public void setHigherNeighbour(long higherNeighbour) {
        this.higherNeighbour = higherNeighbour;
    }

    public long getRightNeighbour() {
        return rightNeighbour;
    }

    public void setRightNeighbour(long rightNeighbour) {
        this.rightNeighbour = rightNeighbour;
    }

    public float getNcoord() {
        return ncoord;
    }

    public void setNcoord(float ncoord) {
        this.ncoord = ncoord;
    }

    public float getEcoord() {
        return ecoord;
    }

    public void setEcoord(float ecoord) {
        this.ecoord = ecoord;
    }

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

    public float getArea() {
        return area;
    }

    public void setArea(float area) {
        this.area = area;
    }

    public long getHeight() {
        return height;
    }

    public void setHeight(long height) {
        this.height = height;
    }

    public String getCanton() {
        return canton;
    }

    public void setCanton(String canton) {
        this.canton = canton;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
