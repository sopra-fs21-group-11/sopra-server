package ch.uzh.ifi.hase.soprafs21.entity.ValueCategories;

import ch.uzh.ifi.hase.soprafs21.entity.Cards.*;
import org.hibernate.mapping.Value;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ECoordinateCategory implements ValueCategory {
    private String name = "EW-Coordinates";
    private String description = "Compares the cards with their East-West coordinate value.";
    private long id = 1L;
    //List defines the allowed cards.
    private List<Type> validTypes = new ArrayList<Type>(Arrays.asList(
            MountainCard.class,
            NormalLocationCard.class,
            RiverCard.class,
            SeightSeeingCard.class,
            SwissLocationCard.class
            //extendable with more types.
    ));


    public boolean isCardValidInCategory(Card card) {
        if (validTypes.contains(card.getClass())) {
            return true;
        }
        else {
            return false;
        }
    }


    public boolean isPlacementCorrect(Card referenceCard, Card cardInQuestion) throws Exception {

        float referenceEcoord = referenceCard.getEwCoordinates();//we can assign it directly since every card obj has coordinates...
        float questionEcoord = cardInQuestion.getEwCoordinates();

        //we have to check if any left/right/higher/lower neighbour is null:
        if ((referenceCard.getRightNeighbour() == null && referenceCard.getLeftNeighbour() == null) ||   // Either the referencecard or the card in question must not be a starting card.
                cardInQuestion.getRightNeighbour() == null && cardInQuestion.getLeftNeighbour() == null) { // If one of the cards has no left/right neighbour, we are in vertical axis.
            try {
                if (referenceCard.getHigherNeighbour().getCardId() == cardInQuestion.getCardId()) { // reference card is placed under questioncard
                    return (referenceEcoord <= questionEcoord);
                }
            }
            catch (Exception ex) {
            }
            try {
                if (referenceCard.getLowerNeighbour().getCardId() == cardInQuestion.getCardId()) {//reference card is placed on top of questioncard {}

                    return (referenceEcoord >= questionEcoord);
                }
            }
            catch (Exception ex) {
            }
            //cards are not next to each other:
            throw new Exception("Questionable cards are not next to each other.");

        }

        if ((referenceCard.getLowerNeighbour() == null && referenceCard.getHigherNeighbour() == null) ||
                cardInQuestion.getLowerNeighbour() == null && cardInQuestion.getHigherNeighbour() == null) {
            try {
                if (referenceCard.getRightNeighbour().getCardId() == cardInQuestion.getCardId()) {//reference card is placed left to card in question
                    return (referenceEcoord <= questionEcoord);
                }
            }
            catch (Exception e) {
            }

            try {
                if (referenceCard.getLeftNeighbour().getCardId() == cardInQuestion.getCardId()) { //reference card is placed right to card in question
                    return (referenceEcoord >= questionEcoord);
                }
            }
            catch (Exception e) {
            }

            //cards are not next to each other:
                throw new Exception("Questionable cards are not next to each other.");


        }
        throw new Exception("No valid compare request. (Two startingcards?)");
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
