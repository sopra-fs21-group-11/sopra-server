package ch.uzh.ifi.hase.soprafs21.rest.socketDTO;


import ch.uzh.ifi.hase.soprafs21.entity.Cards.Card;

import java.util.ArrayList;
import java.util.List;

public class GameStateDTO {

    private List<CardDTO> left;
    private List<CardDTO> right;
    private List<CardDTO> top;
    private List<CardDTO> bottom;

    private CardDTO startingCard;

    private int playertokens;
    private long playersturn;
    private String gamestate;
    private CardDTO nextCardOnStack;

    public GameStateDTO() {

        left = new ArrayList<>();
        right = new ArrayList<>();
        top = new ArrayList<>();
        bottom = new ArrayList<>();


    }

    public CardDTO getStartingCard() {
        return startingCard;
    }

    public void setStartingCard(CardDTO startingCard) {
        this.startingCard = startingCard;
    }

    public CardDTO getNextCardOnStack() {
        return nextCardOnStack;
    }

    public void setNextCardOnStack(CardDTO nextCardOnStack) {
        this.nextCardOnStack = nextCardOnStack;
    }



    public void addLeftCard(CardDTO card){
        this.left.add(card);
    }
    public void addRightCard(CardDTO card){
        this.right.add(card);
    }
    public void addTopCard(CardDTO card){
        this.top.add(card);
    }
    public void addBottomCard(CardDTO card){
        this.bottom.add(card);
    }

    public CardDTO getStartingCard() {
        return startingCard;
    }

    public void setStartingCard(CardDTO startingCard) {
        this.startingCard = startingCard;
    }

    public int getPlayertokens() {
        return playertokens;
    }

    public void setPlayertokens(int playertokens) {
        this.playertokens = playertokens;
    }

    public long getPlayersturn() {
        return playersturn;
    }

    public void setPlayersturn(long playersturn) {
        this.playersturn = playersturn;
    }

    public String getGamestate() {
        return gamestate;
    }

    public void setGamestate(String gamestate) {
        this.gamestate = gamestate;
    }
}
