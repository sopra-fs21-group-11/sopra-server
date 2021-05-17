package ch.uzh.ifi.hase.soprafs21.rest.socketDTO;

import ch.uzh.ifi.hase.soprafs21.rest.dto.UserGetDTO;
import java.util.ArrayList;
import java.util.List;

public class GameDoubtDTO { //Behaves like a gamestate with additional doubtdto in it.
    private List<CardDTO> left;
    private List<CardDTO> right;
    private List<CardDTO> top;
    private List<CardDTO> bottom;

    private CardDTO startingCard;

    private int playertokens;
    private UserGetDTO playersturn;
    private UserGetDTO nextPlayer;
    private String gamestate;
    private CardDTO nextCardOnStack;

    private DoubtResultDTO doubtResultDTO;

    public GameDoubtDTO() {

        left = new ArrayList<>();
        right = new ArrayList<>();
        top = new ArrayList<>();
        bottom = new ArrayList<>();


    }

    public UserGetDTO getNextPlayer() {
        return nextPlayer;
    }

    public void setNextPlayer(UserGetDTO nextPlayer) {
        this.nextPlayer = nextPlayer;
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


    public int getPlayertokens() {
        return playertokens;
    }

    public void setPlayertokens(int playertokens) {
        this.playertokens = playertokens;
    }

    public UserGetDTO getPlayersturn() {
        return playersturn;
    }

    public void setPlayersturn(UserGetDTO playersturn) {
        this.playersturn = playersturn;
    }

    public String getGamestate() {
        return gamestate;
    }

    public void setGamestate(String gamestate) {
        this.gamestate = gamestate;
    }

    public List<CardDTO> getLeft() {
        return left;
    }

    public void setLeft(List<CardDTO> left) {
        this.left = left;
    }

    public List<CardDTO> getRight() {
        return right;
    }

    public void setRight(List<CardDTO> right) {
        this.right = right;
    }

    public List<CardDTO> getTop() {
        return top;
    }

    public void setTop(List<CardDTO> top) {
        this.top = top;
    }

    public List<CardDTO> getBottom() {
        return bottom;
    }

    public void setBottom(List<CardDTO> bottom) {
        this.bottom = bottom;
    }

    public DoubtResultDTO getDoubtResultDTO() {
        return doubtResultDTO;
    }

    public void setDoubtResultDTO(DoubtResultDTO doubtResultDTO) {
        this.doubtResultDTO = doubtResultDTO;
    }
}
