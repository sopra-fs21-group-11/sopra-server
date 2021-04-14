package ch.uzh.ifi.hase.soprafs21.entity;

import ch.uzh.ifi.hase.soprafs21.entity.Cards.Card;

import java.util.LinkedList;
import java.util.Locale;

public class Board {
    private LinkedList<Card> horizontalAxis;
    private LinkedList<Card> verticalAxis;
    private Card startingCard;
    private int startingCardIndexVertical=0;
    private int startingCardIndexHorizontal=0;

    public Board(Card startingCard){
        this.horizontalAxis = new LinkedList<>();
        this.verticalAxis = new LinkedList<>();
        this.startingCard = startingCard;
        this.horizontalAxis.add(startingCard);
        this.verticalAxis.add(startingCard);
    }

   public void placeCard(Card cardToPlace, int index, String axis){
        //horizontalAxis.
       if(axis.toLowerCase(Locale.ROOT).contains("horizontal")){
           int indexToInsert = index+startingCardIndexHorizontal+1;

           if(indexToInsert == 0){//no left neighbour -> placed at the border
               horizontalAxis.get(0).setLeftNeighbour(cardToPlace);
               cardToPlace.setRightNeighbour(horizontalAxis.get(0));
               horizontalAxis.add(0, cardToPlace);
           }else if (indexToInsert == horizontalAxis.size()) {//index to insert is the same as length -> no right neighbour -> right border
                horizontalAxis.get(indexToInsert-1).setRightNeighbour(cardToPlace);
                cardToPlace.setLeftNeighbour(horizontalAxis.get(indexToInsert-1));
                horizontalAxis.add(indexToInsert, cardToPlace);
           } else{
               horizontalAxis.get(indexToInsert-1).setRightNeighbour(cardToPlace);
               horizontalAxis.get(indexToInsert).setLeftNeighbour(cardToPlace);
               cardToPlace.setLeftNeighbour(horizontalAxis.get(indexToInsert-1));
               cardToPlace.setRightNeighbour(horizontalAxis.get(indexToInsert));
               horizontalAxis.add(indexToInsert, cardToPlace);
           }


       }else {
           //vertical
           int indexToInsert = index + startingCardIndexVertical + 1;

           if (indexToInsert == 0) {//no lower neighbour -> placed at the border
               verticalAxis.get(0).setLowerNeighbour(cardToPlace);
               cardToPlace.setHigherNeighbour(verticalAxis.get(0));
               verticalAxis.add(0, cardToPlace);
           }
           else if (indexToInsert == verticalAxis.size()) {//index to insert is the same as length -> no right neighbour -> right border
               verticalAxis.get(indexToInsert - 1).setHigherNeighbour(cardToPlace);
               cardToPlace.setLowerNeighbour(verticalAxis.get(indexToInsert - 1));
               verticalAxis.add(indexToInsert, cardToPlace);
           }
           else {
               verticalAxis.get(indexToInsert - 1).setHigherNeighbour(cardToPlace);
               verticalAxis.get(indexToInsert).setLowerNeighbour(cardToPlace);
               cardToPlace.setLowerNeighbour(verticalAxis.get(indexToInsert - 1));
               cardToPlace.setHigherNeighbour(verticalAxis.get(indexToInsert));
               verticalAxis.add(indexToInsert, cardToPlace);
           }
       }

       //reset index
       startingCardIndexHorizontal = horizontalAxis.indexOf(startingCard);
       startingCardIndexVertical = verticalAxis.indexOf(startingCard);
    }

    public LinkedList<Card> getHorizontalAxis() {
        return horizontalAxis;
    }

    public LinkedList<Card> getVerticalAxis() {
        return verticalAxis;
    }

    public Card getStartingCard() {
        return startingCard;
    }
}
