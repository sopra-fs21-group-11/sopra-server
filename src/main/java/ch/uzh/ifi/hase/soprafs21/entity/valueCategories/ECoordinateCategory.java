package ch.uzh.ifi.hase.soprafs21.entity.valueCategories;

import ch.uzh.ifi.hase.soprafs21.entity.cards.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ECoordinateCategory implements ValueCategory {
    private String name = "EW-Coordinates";
    private String description = "Compares the cards with their East-West coordinate value.";
    private long id = 1L;
    //List defines the allowed cards.
    private List<Type> validTypes = new ArrayList<>(Arrays.asList(
            NormalLocationCard.class,
            SwissLocationCard.class
            //extendable with more types.
    ));

    public boolean isCardValidInCategory(Card card) {
        return validTypes.contains(card.getClass());
    }

    public boolean isPlacementCorrect(Card referenceCard, Card cardInQuestion) throws Exception {
        if (referenceCard.getHigherNeighbour() == cardInQuestion) {
            return referenceCard.getEwCoordinates() <= cardInQuestion.getEwCoordinates();
        }
        else if (referenceCard.getLowerNeighbour() == cardInQuestion) {
            return referenceCard.getEwCoordinates() >= cardInQuestion.getEwCoordinates();
        }
        else if (referenceCard.getLeftNeighbour() == cardInQuestion) {
            return referenceCard.getEwCoordinates() >= cardInQuestion.getEwCoordinates();
        }
        else if (referenceCard.getRightNeighbour() == cardInQuestion) {
            return referenceCard.getEwCoordinates() <= cardInQuestion.getEwCoordinates();
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
