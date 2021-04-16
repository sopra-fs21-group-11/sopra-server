package ch.uzh.ifi.hase.soprafs21.entities;

import ch.uzh.ifi.hase.soprafs21.entity.Cards.Card;
import ch.uzh.ifi.hase.soprafs21.entity.Deck;
import ch.uzh.ifi.hase.soprafs21.entity.ValueCategories.ECoordinateCategory;
import ch.uzh.ifi.hase.soprafs21.entity.ValueCategories.NCoordinateCategory;
import ch.uzh.ifi.hase.soprafs21.entity.ValueCategories.ValueCategory;
import org.hibernate.mapping.Value;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class DeckTest {

    @Test
    public void TestDeckWithVauleCategory() throws Exception{
        //current standard-Deck has only swisslocationcards.
        Deck deck = new Deck();
        ValueCategory nscat = new NCoordinateCategory();
        ValueCategory ewcat = new ECoordinateCategory();


        while(!deck.isEmpty()){
            Card card = deck.pop();
            assertTrue(nscat.isCardValidInCategory(card));
            assertTrue(ewcat.isCardValidInCategory(card));
        }
    }
}
