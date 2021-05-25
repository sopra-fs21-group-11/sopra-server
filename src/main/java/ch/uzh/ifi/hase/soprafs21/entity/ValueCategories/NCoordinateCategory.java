package ch.uzh.ifi.hase.soprafs21.entity.ValueCategories;

import ch.uzh.ifi.hase.soprafs21.entity.Cards.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NCoordinateCategory implements ValueCategory {
    private String name = "NS-Coordinates";
    private String description = "Compares the cards with their North-South coordinate value.";
    private long id = 2L;

    //List defines the allowed cards.
    private List<Type> validTypes = new ArrayList<Type>(Arrays.asList(
            MountainCard.class,
            NormalLocationCard.class,
            RiverCard.class,
            SightSeeingCard.class,
            SwissLocationCard.class
            //extendable with more types.
    ));

    public boolean isCardValidInCategory(Card card) {
        if(validTypes.contains(card.getClass())){
            return true;
        }else{
            return false;
        }
    }

    public boolean isPlacementCorrect(Card referenceCard, Card cardInQuestion) throws Exception {
        if (referenceCard.getHigherNeighbour() == cardInQuestion) {
            return referenceCard.getNsCoordinates() <= cardInQuestion.getNsCoordinates();
        }
        else if (referenceCard.getLowerNeighbour() == cardInQuestion) {
            return referenceCard.getNsCoordinates() >= cardInQuestion.getNsCoordinates();
        }
        else if (referenceCard.getLeftNeighbour() == cardInQuestion) {
            return referenceCard.getNsCoordinates() >= cardInQuestion.getNsCoordinates();
        }
        else if (referenceCard.getRightNeighbour() == cardInQuestion) {
            return referenceCard.getNsCoordinates() <= cardInQuestion.getNsCoordinates();
        }
        throw new Exception("Questionable cards are not next to each other.");
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }
}
