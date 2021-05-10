package ch.uzh.ifi.hase.soprafs21.entity;

import ch.uzh.ifi.hase.soprafs21.entity.Cards.Card;
import java.util.LinkedList;
import java.util.Locale;

public class Board {
    //private LinkedList<Card> horizontalAxis;
    //private LinkedList<Card> verticalAxis;
    private LinkedList<Card> topList;
    private LinkedList<Card> bottomList;
    private LinkedList<Card> leftList;
    private LinkedList<Card> rightList;
    private Card startingCard;
    //private int startingCardIndexVertical=0;
    //private int startingCardIndexHorizontal=0;

    private int placedCard;

    public Board(Card startingCard){
        this.topList = new LinkedList<>();
        this.bottomList = new LinkedList<>();
        this.leftList = new LinkedList<>();
        this.rightList = new LinkedList<>();
        this.startingCard = startingCard;
        placedCard=0;
    }
    public void placeCard(Card cardToPlace, int index, String axis){
        placedCard++;
        if(axis.toLowerCase(Locale.ROOT).contains("top")){
            //lower neighbour is starting card
            if(index == 0){
                startingCard.setHigherNeighbour(cardToPlace);
                cardToPlace.setLowerNeighbour(startingCard);
                if(!topList.isEmpty()){
                    //not the first card
                    cardToPlace.setHigherNeighbour(topList.get(index));
                    topList.get(index).setLowerNeighbour(cardToPlace);
                }
                topList.addFirst(cardToPlace);
            }
            //no upper neighbour
            else if(topList.size()== index){
                topList.get(index-1).setHigherNeighbour(cardToPlace);
                cardToPlace.setLowerNeighbour(topList.get(index-1));
                topList.addLast(cardToPlace);
            }
            else {
                cardToPlace.setLowerNeighbour(topList.get(index-1));
                cardToPlace.setHigherNeighbour(topList.get(index));
                topList.get(index-1).setHigherNeighbour(cardToPlace);
                topList.get(index).setLowerNeighbour(cardToPlace);
                topList.add(index,cardToPlace);
            }

        }
        else if(axis.toLowerCase(Locale.ROOT).contains("bottom")){
            //higher neighbour is starting card
            if(index == 0){
                startingCard.setLowerNeighbour(cardToPlace);
                cardToPlace.setHigherNeighbour(startingCard);
                if(!bottomList.isEmpty()){
                    //not the first card
                    cardToPlace.setLowerNeighbour(bottomList.get(index));
                    bottomList.get(index).setHigherNeighbour(cardToPlace);
                }
                bottomList.addFirst(cardToPlace);
            }
            //no lower neighbour
            else if(bottomList.size()== index){
                bottomList.get(index-1).setLowerNeighbour(cardToPlace);
                cardToPlace.setHigherNeighbour(bottomList.get(index-1));
                bottomList.addLast(cardToPlace);
            }
            else {
                cardToPlace.setHigherNeighbour(bottomList.get(index-1));
                cardToPlace.setLowerNeighbour(bottomList.get(index));
                bottomList.get(index-1).setLowerNeighbour(cardToPlace);
                bottomList.get(index).setHigherNeighbour(cardToPlace);
                bottomList.add(index,cardToPlace);
            }
        }
        else if(axis.toLowerCase(Locale.ROOT).contains("left")){
            //right neighbour is starting card
            if(index == 0){
                startingCard.setLeftNeighbour(cardToPlace);
                cardToPlace.setRightNeighbour(startingCard);
                if(!leftList.isEmpty()){
                    //not the first card
                    cardToPlace.setLeftNeighbour(leftList.get(index));
                    leftList.get(index).setRightNeighbour(cardToPlace);
                }
                leftList.addFirst(cardToPlace);
            }
            //no left neighbour
            else if(leftList.size() == index){
                leftList.get(index-1).setLeftNeighbour(cardToPlace);
                cardToPlace.setRightNeighbour(leftList.get(index-1));
                leftList.addLast(cardToPlace);
            }
            else {
                cardToPlace.setRightNeighbour(leftList.get(index-1));
                cardToPlace.setLeftNeighbour(leftList.get(index));
                leftList.get(index-1).setLeftNeighbour(cardToPlace);
                leftList.get(index).setRightNeighbour(cardToPlace);
                leftList.add(index,cardToPlace);
            }
        }
        else if(axis.toLowerCase(Locale.ROOT).contains("right")){
            //left neighbour is starting card
            if(index == 0){
                startingCard.setRightNeighbour(cardToPlace);
                cardToPlace.setLeftNeighbour(startingCard);
                if(!rightList.isEmpty()){
                    //not the first card
                    cardToPlace.setRightNeighbour(rightList.get(index));
                    rightList.get(index).setLeftNeighbour(cardToPlace);
                }
                rightList.addFirst(cardToPlace);
            }
            //no right neighbour
            else if(rightList.size()== index){
                rightList.get(index-1).setRightNeighbour(cardToPlace);
                cardToPlace.setLeftNeighbour(rightList.get(index-1));
                rightList.addLast(cardToPlace);
            }
            else {
                cardToPlace.setLeftNeighbour(rightList.get(index-1));
                cardToPlace.setRightNeighbour(rightList.get(index));
                rightList.get(index-1).setRightNeighbour(cardToPlace);
                rightList.get(index).setLeftNeighbour(cardToPlace);
                rightList.add(index,cardToPlace);
            }
        }
    }

    public void removeCard(Card cardToRemove){
        Card rightNeighbour = cardToRemove.getRightNeighbour();
        Card leftNeighbour = cardToRemove.getLeftNeighbour();
        Card higherNeighbour = cardToRemove.getHigherNeighbour();
        Card lowerNeighbour = cardToRemove.getLowerNeighbour();
        //rearrange pointers:

        if(topList.contains(cardToRemove)){
            if(cardToRemove.getHigherNeighbour() != null){
                cardToRemove.getHigherNeighbour().setLowerNeighbour(cardToRemove.getLowerNeighbour());
                cardToRemove.getLowerNeighbour().setHigherNeighbour(cardToRemove.getHigherNeighbour());
            }
            else{
                cardToRemove.getLowerNeighbour().setHigherNeighbour(null);
            }
            topList.remove(cardToRemove);
        }
        else if(bottomList.contains(cardToRemove)){
            if(cardToRemove.getLowerNeighbour() != null){
                cardToRemove.getLowerNeighbour().setHigherNeighbour(cardToRemove.getHigherNeighbour());
                cardToRemove.getHigherNeighbour().setLowerNeighbour(cardToRemove.getLowerNeighbour());
            }
            else{
                cardToRemove.getHigherNeighbour().setLowerNeighbour(null);
            }
            bottomList.remove(cardToRemove);
        }
        else if(leftList.contains(cardToRemove)){
            if(cardToRemove.getLeftNeighbour() != null){
                cardToRemove.getLeftNeighbour().setRightNeighbour(cardToRemove.getRightNeighbour());
                cardToRemove.getRightNeighbour().setLeftNeighbour(cardToRemove.getLeftNeighbour());
            }
            else{
                cardToRemove.getRightNeighbour().setLeftNeighbour(null);
            }
            leftList.remove(cardToRemove);
        }
        else if(rightList.contains(cardToRemove)){
            if(cardToRemove.getRightNeighbour() != null){
                cardToRemove.getRightNeighbour().setLeftNeighbour(cardToRemove.getLeftNeighbour());
                cardToRemove.getLeftNeighbour().setRightNeighbour(cardToRemove.getRightNeighbour());
            }
            else{
                cardToRemove.getLeftNeighbour().setRightNeighbour(null);
            }
            rightList.remove(cardToRemove);
        }
    }

    public void clearBoard(Card nextStartingCard){
        topList.clear();
        bottomList.clear();
        leftList.clear();
        rightList.clear();
        startingCard=nextStartingCard;
        placedCard=0;
    }

    public Card getCardById(int id){
        if(startingCard.getCardId() == id){
            return startingCard;
        }
        for(Card card : getTopList()){
            if(card.getCardId() == id){
                return card;
            }
        }

        for(Card card : getBottomList()){
            if(card.getCardId() == id){
                return card;
            }
        }

        for(Card card : getLeftList()){
            if(card.getCardId() == id){
                return card;
            }
        }

        for(Card card : getRightList()){
            if(card.getCardId() == id){
                return card;
            }
        }

        //card not found:
        return null;
    }

    public LinkedList<Card> getTopList() {
        return topList;
    }

    public LinkedList<Card> getBottomList() {
        return bottomList;
    }

    public LinkedList<Card> getLeftList() {
        return leftList;
    }

    public LinkedList<Card> getRightList() {
        return rightList;
    }

    public Card getStartingCard() {
        return startingCard;
    }

    public int getPlacedCard() {
        return placedCard;
    }

    public void setPlacedCard(int placedCard) {
        this.placedCard = placedCard;
    }
}
