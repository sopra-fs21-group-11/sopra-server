package ch.uzh.ifi.hase.soprafs21.entity;

import ch.uzh.ifi.hase.soprafs21.entity.Cards.Card;
import ch.uzh.ifi.hase.soprafs21.entity.ValueCategories.ValueCategory;
import java.io.Serializable;
import java.util.*;

public class Deck extends Stack implements Serializable {


    //This is not the definitive implementation. I just add the cards of the csv to test functionalities.
    private Stack<Card> cards;
    private List<ValueCategory> possibleComparisonStrategies;

    public Deck(){ cards = new Stack<>(); }

    @Override
    public int size(){
        return cards.size();
    }

    @Override
    public boolean isEmpty(){
        return cards.isEmpty();
    }

    public void shuffleDeck(){
        Collections.shuffle(this.cards);
    }


    public void setCards(Stack<Card> cards) {
        this.cards = cards;
    }
    public void addValueCategory(ValueCategory category){
        if(possibleComparisonStrategies == null){
            possibleComparisonStrategies = new ArrayList<>();
        }
        possibleComparisonStrategies.add(category);
    }

    /**
     * Picks the top card and returns it
     * @return The card that is picked.
     */
    public Card pop(){
        return this.cards.pop();
    }
}
