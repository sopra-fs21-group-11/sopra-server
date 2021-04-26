package ch.uzh.ifi.hase.soprafs21.entity;

import ch.uzh.ifi.hase.soprafs21.entity.Cards.Card;
import ch.uzh.ifi.hase.soprafs21.rest.socketDTO.EvaluatedCardDTO;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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

    public Board(Card startingCard){
        this.topList = new LinkedList<>();
        this.bottomList = new LinkedList<>();
        this.leftList = new LinkedList<>();
        this.rightList = new LinkedList<>();
        this.startingCard = startingCard;
    }
    public void placeCard(Card cardToPlace, int index, String axis){
        if(axis.toLowerCase(Locale.ROOT).contains("top")){
            //no upper neighbour
            if(topList.size()== index){
                topList.get(index-1).setHigherNeighbour(cardToPlace);
                cardToPlace.setLowerNeighbour(topList.get(index-1));
                topList.addLast(cardToPlace);
            }
            //lower neighbour is starting card
            else if(index == 0){
                startingCard.setHigherNeighbour(cardToPlace);
                cardToPlace.setLowerNeighbour(startingCard);
                if(!topList.isEmpty()){
                    cardToPlace.setHigherNeighbour(topList.get(index));
                    topList.get(index).setLowerNeighbour(cardToPlace);
                }
                topList.addFirst(cardToPlace);


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
            //no lower neighbour
            if(bottomList.size()== index){
                bottomList.get(index-1).setLowerNeighbour(cardToPlace);
                cardToPlace.setHigherNeighbour(bottomList.get(index-1));
                bottomList.addLast(cardToPlace);
            }
            //higher neighbour is starting card
            else if(index == 0){
                startingCard.setLowerNeighbour(cardToPlace);
                cardToPlace.setHigherNeighbour(startingCard);
                if(!bottomList.isEmpty()){
                    cardToPlace.setLowerNeighbour(bottomList.get(index));
                    bottomList.get(index).setHigherNeighbour(cardToPlace);
                }
                bottomList.addFirst(cardToPlace);
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
            //no left neighbour
            if(leftList.size() == index){
                leftList.get(index-1).setLeftNeighbour(cardToPlace);
                cardToPlace.setRightNeighbour(leftList.get(index-1));
                leftList.addLast(cardToPlace);
            }
            //right neighbour is starting card
            else if(index == 0){
                startingCard.setLeftNeighbour(cardToPlace);
                cardToPlace.setRightNeighbour(startingCard);
                if(!leftList.isEmpty()){
                    cardToPlace.setLeftNeighbour(leftList.get(index));
                    leftList.get(index).setRightNeighbour(cardToPlace);
                }
                leftList.addFirst(cardToPlace);
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
            //no right neighbour
            if(rightList.size()== index){
                rightList.get(index-1).setRightNeighbour(cardToPlace);
                cardToPlace.setLeftNeighbour(rightList.get(index-1));
                rightList.addLast(cardToPlace);
            }
            //left neighbour is starting card
            else if(index == 0){
                startingCard.setRightNeighbour(cardToPlace);
                cardToPlace.setLeftNeighbour(startingCard);
                if(!rightList.isEmpty()){
                    cardToPlace.setRightNeighbour(rightList.get(index));
                    rightList.get(index).setLeftNeighbour(cardToPlace);
                }
                rightList.addFirst(cardToPlace);
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
}
