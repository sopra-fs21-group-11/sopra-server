package ch.uzh.ifi.hase.soprafs21.entity.ValueCategories;

import ch.uzh.ifi.hase.soprafs21.entity.Cards.Card;
import ch.uzh.ifi.hase.soprafs21.entity.Cards.NormalLocationCard;
import ch.uzh.ifi.hase.soprafs21.entity.Cards.SwissLocationCard;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PopulationValueCategory implements ValueCategory{
    private String name = "Population";
    private String description = "Compares the cards with their population.";
    private long id = 3L;

    //List defines the allowed cards.
    private List<Type> validTypes = new ArrayList<Type>(Arrays.asList(
            SwissLocationCard.class,
            NormalLocationCard.class
    ));

    public boolean isCardValidInCategory(Card card) {
        if(validTypes.contains(card.getClass())){
            return true;
        }else{
            return false;
        }
    }

    public boolean isPlacementCorrect(Card referenceCard, Card cardInQuestion) throws Exception{

        float referenceValue = 0;
        float questionValue = 0;
        try{
            referenceValue = ((SwissLocationCard)referenceCard).getPopulation();
        }catch (Exception ex){}
        try{
            referenceValue = ((NormalLocationCard)referenceCard).getPopulation();
        }catch (Exception ex){}

        try{
            questionValue = ((SwissLocationCard)cardInQuestion).getPopulation();
        }catch (Exception ex){}
        try{
            questionValue = ((NormalLocationCard)cardInQuestion).getPopulation();
        }catch (Exception ex){}

        //we have to check if any left/right/higher/lower neighbour is null:
        if((referenceCard.getRightNeighbour() == null && referenceCard.getLeftNeighbour() == null) ||   // Either the referencecard or the card in question must not be a starting card.
                cardInQuestion.getRightNeighbour() == null && cardInQuestion.getLeftNeighbour()==null){ // If one of the cards has no left/right neighbour, we are in vertical axis.
            try {
                if(referenceCard.getHigherNeighbour().getCardId() == cardInQuestion.getCardId()){ // reference card is placed under questioncard
                    return (referenceValue <= questionValue);
                }
            }
            catch (Exception e) {
            }
            try {
                if (referenceCard.getLowerNeighbour().getCardId() == cardInQuestion.getCardId()){//reference card is placed on top of questioncard
                    return (referenceValue >= questionValue);
                }
            }
            catch (Exception e) {
            }
            //cards are not next to each other:
                throw new Exception("Questionable cards are not next to each other.");
        }

        if((referenceCard.getLowerNeighbour() == null && referenceCard.getHigherNeighbour() == null) ||
                cardInQuestion.getLowerNeighbour() == null && cardInQuestion.getHigherNeighbour() == null){
            try {
                if(referenceCard.getRightNeighbour().getCardId()== cardInQuestion.getCardId()){//reference card is placed left to card in question
                    return (referenceValue <= questionValue);
                }
            }
            catch (Exception e) {
            }
            try {
                if(referenceCard.getLeftNeighbour().getCardId()== cardInQuestion.getCardId()){ //reference card is placed right to card in question
                    return (referenceValue >= questionValue);
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
        try{this.isPlacementCorrect(null, null);} catch (Exception ex ){}
        return description;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
