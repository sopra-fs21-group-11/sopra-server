package ch.uzh.ifi.hase.soprafs21.rest.socketDTO;


import ch.uzh.ifi.hase.soprafs21.entity.Cards.Card;

import java.util.ArrayList;
import java.util.List;

public class GameStateDTO {
    private List<CardDTO> cards;
    private int playertokens;
    private long playersturn;
    private String gamestate;
    private CardDTO nextCardOnStack;

    public GameStateDTO() {
        cards = new ArrayList<>();
    }

    public CardDTO getNextCardOnStack() {
        return nextCardOnStack;
    }

    public void setNextCardOnStack(CardDTO nextCardOnStack) {
        this.nextCardOnStack = nextCardOnStack;
    }

    public List<CardDTO> getCards() {
        return cards;
    }

    public void setCards(List<CardDTO> cards) {
        this.cards = cards;
    }

    public void addCard(CardDTO card){
        this.cards.add(card);
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
