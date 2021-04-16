package ch.uzh.ifi.hase.soprafs21.entities;


import ch.uzh.ifi.hase.soprafs21.entity.Cards.Card;
import ch.uzh.ifi.hase.soprafs21.entity.Cards.NormalLocationCard;
import ch.uzh.ifi.hase.soprafs21.entity.Cards.SwissLocationCard;
import ch.uzh.ifi.hase.soprafs21.entity.Deck;
import ch.uzh.ifi.hase.soprafs21.entity.ValueCategories.ECoordinateCategory;
import ch.uzh.ifi.hase.soprafs21.entity.ValueCategories.NCoordinateCategory;
import ch.uzh.ifi.hase.soprafs21.entity.ValueCategories.ValueCategory;
import org.hibernate.mapping.Value;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CompareCardTest {

    @Test
    public void compareTwoCards() throws Exception{//whatch out. There will be null-pointer exceptions that are handled by the try-catch blocks. Dont let your eyes disturb you :).
        Card card1 = new SwissLocationCard();
        card1.setEwCoordinates(1.0f);
        card1.setNsCoordinates(5.0f);

        //our second card is correctly placed if it is on the right and on top of card1.
        Card card2 = new NormalLocationCard();
        card2.setEwCoordinates(2.0f);
        card2.setNsCoordinates(6.0f);

        ValueCategory ns = new NCoordinateCategory();
        ValueCategory ew = new ECoordinateCategory();

        card1.setRightNeighbour(card2);// c1    c2
        card2.setLeftNeighbour(card1);
        assertTrue(ns.isPlacementCorrect(card1, card2));
        assertTrue(ew.isPlacementCorrect(card1, card2));
        //cleanup
        card1.setRightNeighbour(null);
        card2.setLeftNeighbour(null);

        card1.setLowerNeighbour(card2);//    c1
        card2.setHigherNeighbour(card1);//   c2
        assertFalse(ew.isPlacementCorrect(card1, card2));
        assertFalse(ns.isPlacementCorrect(card1,card2));

        //should make no difference in which order the arguments are called:
        assertFalse(ew.isPlacementCorrect(card2, card1));
        assertFalse(ns.isPlacementCorrect(card2,card1));

        //no try it with a mocked startingcard and should be the same result.:
        card1.setLeftNeighbour(card2);
        card1.setRightNeighbour(card2);
        assertFalse(ew.isPlacementCorrect(card1, card2));
        assertFalse(ns.isPlacementCorrect(card1,card2));

    }
}
