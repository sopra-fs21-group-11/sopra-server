package ch.uzh.ifi.hase.soprafs21.entity.valueCategories;

import ch.uzh.ifi.hase.soprafs21.entity.cards.Card;

public interface ValueCategory {
    public boolean isCardValidInCategory(Card card);
    public boolean isPlacementCorrect(Card referenceCard, Card cardInQuestion) throws Exception;
    public String getName();
    public String getDescription();
    public long getId();
}
