package ch.uzh.ifi.hase.soprafs21.entity;

import ch.uzh.ifi.hase.soprafs21.entity.Cards.Card;

import java.util.LinkedList;

public class Board {
    private LinkedList<Card> horizontalAxis;
    private LinkedList<Card> verticalAxis;
    private Card startingCard;

    public Board(Card startingCard){
        this.startingCard = startingCard;
    }
}
