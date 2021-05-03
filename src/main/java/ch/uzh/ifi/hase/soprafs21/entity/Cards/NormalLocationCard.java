package ch.uzh.ifi.hase.soprafs21.entity.Cards;


public class NormalLocationCard extends Card{
    private int population;
    private float area;
    private int height;

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public float getArea() {
        return area;
    }

    public void setArea(float area) {
        this.area = area;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
