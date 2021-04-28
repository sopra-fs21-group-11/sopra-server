package ch.uzh.ifi.hase.soprafs21.entity;

import ch.uzh.ifi.hase.soprafs21.constant.GameState;
import ch.uzh.ifi.hase.soprafs21.entity.Cards.Card;
import ch.uzh.ifi.hase.soprafs21.entity.ValueCategories.ValueCategory;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.CardMapper;
import ch.uzh.ifi.hase.soprafs21.rest.socketDTO.*;
import ch.uzh.ifi.hase.soprafs21.service.GameService;
import ch.uzh.ifi.hase.soprafs21.service.countdown.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

public class Game implements PropertyChangeListener {
    private Queue<Map.Entry<User, String>> players;
    private long id;
    private Board activeBoard;
    private Map.Entry<User, String> currentPlayer;
    private GameState activeState;
    private boolean hasWinner = false;
    private Deck deckStack;
    private long hostPlayerId;
    private List<User> waitingForPlayers;
    private Card nextCard;
    private ValueCategory horizontalValueCategory;
    private ValueCategory verticalValueCategory;

    private final GameSettings currentSettings;

    private SimpMessagingTemplate template;

    private GameService gameService;

    private CountdownHelper doubtCountdown;
    private CountdownHelper visibleCountdown;
    private CountdownHelper turnCountdown;
    private CountdownHelper evaluationCountdown;

    private Evaluation evaluation; //I created a new class because our game-class gets crowded slightly...
    private int nrOfWrongCards;


    public Game(GameLobby lobby){
        //set up the game-object with all details of the lobby:
        this.waitingForPlayers = lobby.getPlayers();
        this.currentSettings = lobby.getSettings();
        this.hostPlayerId = lobby.getHostId();
        this.activeState = GameState.CARDPLACEMENT;
        this.id = lobby.getId();

        this.verticalValueCategory = lobby.getSettings().getVerticalValueCategory();
        this.horizontalValueCategory = lobby.getSettings().getHorizontalValueCategory();


        this.deckStack = new Deck(currentSettings.getCardsBeforeEvaluation()*currentSettings.getNrOfEvaluations());//Initializes the standard testing deck. (30 cards out of csv. All SwissLocationCard)

        //We set the starting-card and the nextCard right away:
        this.activeBoard = new Board(deckStack.pop());
        this.nextCard = deckStack.pop();

        this.players = new LinkedList<>();

    }

    public boolean joinGame(User user, String sessionId){
        boolean waitingFor = false;
        for(User waitingForUser : this.waitingForPlayers){
            if(waitingForUser.getId() == user.getId()){
                waitingFor = true;
                break;
            }
        }
        if(!waitingFor) {
            return false; //already joined or not in lobby
        }

        for(User waitingForUser : this.waitingForPlayers){
            if(waitingForUser.getId() == user.getId()){
                this.waitingForPlayers.remove(waitingForUser);
                break;
            }
        }
        if(this.hostPlayerId == user.getId()){
            this.currentPlayer = new AbstractMap.SimpleEntry<>(user, sessionId); //host starts the game.
        }
        this.players.add(new AbstractMap.SimpleEntry<User, String>(user, sessionId));//add token/user-combo to our players queue.
        return true;
    }

    public void performTurn(long userid, Card cardToPlace, int placementIndex, String axis){
        if(currentPlayer.getKey().getId() != userid){
            return;
        }
        //set next player
        //players.add(currentPlayer);
        //place card
        activeBoard.placeCard(cardToPlace, placementIndex,axis);
        //set next card
        nextCard = deckStack.pop();
        this.turnCountdown.doStop();
        currentPlayer = players.remove();
        players.add(currentPlayer); //the doubtingphase has its one currentPlayer
    }

    public void propertyChange(PropertyChangeEvent evt){
        switch(evt.getPropertyName()) {//which property has changed?
            case("DoubtCdEnded")://DoubtCountdown ended whithout any doubt incoming -> next turn
                this.turnCountdown = new PlayersTurnCountdown(currentSettings.getPlayerTurnCountdown(), this, currentPlayer.getKey());
                turnCountdown.addPropertyChangeListener(this);
                turnCountdown.start();
                activeState = GameState.CARDPLACEMENT;
                gameService.sendGameStateToUsers(id);
                break;
            case("DoubtCdStopped")://Doubt incoming. we start visiblecd:
                this.visibleCountdown = new DoubtVisibleCountdown(currentSettings.getVisibleAfterDoubtCountdown(), this);
                visibleCountdown.addPropertyChangeListener(this);
                visibleCountdown.start();
                //doubt dto is sent by doubt methods
                break;
            case("GuessCdEnded")://EvaluationCountdown ended. Evaluate even not all guesses are here & start visiblecd
                this.visibleCountdown = new EvaluationVisibleCountdown(currentSettings.getEvaluationCountdownVisible(), this);
                visibleCountdown.start();
                performEvaluationAfterGuessPresentOrCdEnded();
                activeState = GameState.VISIBLE;
                gameService.sendEvaluatedGameStateToUsers(id);
                break;
            case("GuessCdStopped"):
                this.visibleCountdown = new DoubtVisibleCountdown(currentSettings.getEvaluationCountdownVisible(), this);
                visibleCountdown.start();
                performEvaluationAfterGuessPresentOrCdEnded();
                activeState = GameState.VISIBLE;
                gameService.sendEvaluatedGameStateToUsers(id);
                break;
            case("DoubtVisibleCdEnded")://Doubt Visible Countdown
                //Two cases: Either we start an evaluation if we have enough cards lying or we continue with next turn.
                if(activeBoard.getPlacedCard() == currentSettings.getCardsBeforeEvaluation()){
                    //start evaluation
                    activeState = GameState.EVALUATION;
                    gameService.sendGameStateToUsers(id);
                    this.evaluationCountdown = new WaitForGuessCountdown(currentSettings.getEvaluationCountdown(), this);
                    evaluationCountdown.addPropertyChangeListener(this);
                    evaluationCountdown.start();
                }else {
                    //start next turn
                    this.turnCountdown = new PlayersTurnCountdown(currentSettings.getPlayerTurnCountdown(), this, currentPlayer.getKey());
                    turnCountdown.addPropertyChangeListener(this);
                    turnCountdown.start();
                    activeState = GameState.CARDPLACEMENT;
                    gameService.sendGameStateToUsers(id);
                }
                break;
            case("PlayerTurnCdEnded"):
                //PlayerCountdown has ended -> next players turn.
                currentPlayer = players.remove();
                players.add(currentPlayer);
                //start new playerCountdown:
                gameService.sendGameStateToUsers(id);
                this.turnCountdown = new PlayersTurnCountdown(currentSettings.getPlayerTurnCountdown(), this, currentPlayer.getKey());
                turnCountdown.addPropertyChangeListener(this);
                turnCountdown.start();
                break;
            case("PlayerTurnCdStopped")://A player performed a turn -> goto doubtingPhase
                this.activeState = GameState.DOUBTINGPHASE;
                this.doubtCountdown = new DoubtCountdown(currentSettings.getDoubtCountdown(), this, currentPlayer.getKey());
                doubtCountdown.addPropertyChangeListener(this);
                doubtCountdown.start();
                break;
            case("EvaluationVisibleCdEnded"): //next turn
                this.turnCountdown = new PlayersTurnCountdown(currentSettings.getPlayerTurnCountdown(), this, currentPlayer.getKey());
                turnCountdown.addPropertyChangeListener(this);
                turnCountdown.start();
                break;
        }
    }
    public void performDoubt(String sessionId, int placedCard, int doubtedCard){
        if(!doubtCountdown.isAlive()){//countdown isnt running -> we dont accept.
            return;
        }
        Card referenceCard = activeBoard.getCardById(doubtedCard);
        Card doubtedCardToSend = activeBoard.getCardById(placedCard);
        User doubtingUser = null;
        for(var user : players){
            if(user.getValue() == sessionId){
                doubtingUser = user.getKey();
            }
        }
        User doubtedUser = ((DoubtCountdown)doubtCountdown).getDoubtedUser();
        boolean evaluateResult = true;
        if(!evaluateDoubt(placedCard, doubtedCard)){
            evaluateResult = false;
            //doubt is rightous -> remove and handle tokens
            //first get card obj from id
            Card cardToRemove = activeBoard.getCardById(placedCard);

            //remove card:
            activeBoard.removeCard(cardToRemove);
            doubtedUser.currentToken--;
            doubtingUser.currentToken++;
        } else{//doubt is wrong
            doubtedUser.currentToken++;
            doubtingUser.currentToken--;
        }
        doubtCountdown.doStop();

        gameService.sendDoubtResultDTO(id,referenceCard, doubtedCardToSend, evaluateResult );

    }


    private boolean evaluateDoubt(int placedCardId, int questionableCardId){
        Card placedCard = activeBoard.getCardById(placedCardId);
        Card questionableCard = activeBoard.getCardById(questionableCardId);
        //check if we have to evaluate vertical or horizontal category:
        ValueCategory compareCategory = null;
        if(placedCard.getHigherNeighbour() == questionableCard || placedCard.getLowerNeighbour() == questionableCard){
            compareCategory = verticalValueCategory;
        }
        else if(placedCard.getLeftNeighbour() == questionableCard || placedCard.getRightNeighbour() == questionableCard){
            compareCategory = horizontalValueCategory;
        }
        try {
            return compareCategory.isPlacementCorrect(placedCard, questionableCard);
        } catch (Exception ex){
            return true;
        }
    }

    public GameStateDTO convertToDTO(){
        GameStateDTO gameStateDTO = new GameStateDTO();
        //add starting card first.
        Card startingCard = this.activeBoard.getStartingCard();
        CardDTO startingCardDTO = CardMapper.ConvertEntityToCardDTO(startingCard);
        gameStateDTO.setStartingCard(startingCardDTO);

        gameStateDTO.setStartingCard(startingCardDTO);
        int positionCounter = 1;
        Card loopCard = startingCard;
        while(loopCard.getLeftNeighbour() !=null){ //get left cards
            loopCard = loopCard.getLeftNeighbour();
            CardDTO cardDTO = CardMapper.ConvertEntityToCardDTO(loopCard);
            cardDTO.setPosition(positionCounter);
            gameStateDTO.addLeftCard(cardDTO);
            positionCounter++;
        }
        positionCounter = 1;
        loopCard = startingCard;
        while(loopCard.getRightNeighbour() !=null){ //get right cards
            loopCard = loopCard.getRightNeighbour();
            CardDTO cardDTO = CardMapper.ConvertEntityToCardDTO(loopCard);
            cardDTO.setPosition(positionCounter);
            gameStateDTO.addRightCard(cardDTO);
            positionCounter++;
        }
        positionCounter = 1;
        loopCard = startingCard;
        while(loopCard.getHigherNeighbour() !=null){ //get top cards
            loopCard = loopCard.getHigherNeighbour();
            CardDTO cardDTO = CardMapper.ConvertEntityToCardDTO(loopCard);
            cardDTO.setPosition(positionCounter);
            gameStateDTO.addTopCard(cardDTO);
            positionCounter++;
        }
        positionCounter = 1;
        loopCard = startingCard;
        while(loopCard.getLowerNeighbour() !=null){ //get bottom cards
            loopCard = loopCard.getLowerNeighbour();
            CardDTO cardDTO = CardMapper.ConvertEntityToCardDTO(loopCard);
            cardDTO.setPosition(positionCounter);
            gameStateDTO.addBottomCard(cardDTO);
            positionCounter++;
        }

        CardDTO nextCard = CardMapper.ConvertEntityToCardDTO(this.nextCard);

        gameStateDTO.setGamestate(this.activeState.toString());
        gameStateDTO.setPlayertokens(1);
        gameStateDTO.setNextCardOnStack(nextCard);

        gameStateDTO.setPlayersturn(this.currentPlayer.getKey().getId());

        Object[] obj = players.toArray();
        //debug with only one player. Usually we get in else case.:
        if(players.size()<=1){
            gameStateDTO.setNextPlayer(1);
        }else {
            gameStateDTO.setNextPlayer(((Map.Entry<User, String>) obj[1]).getKey().getId());
        }
        return gameStateDTO;

    }

    public void parseEvaluationGuess(String sessionId, GameGuessDTO guess){
        if(!evaluationCountdown.isAlive()) {
            //if cd isnt alive anymore, we do nothing
            return;
        }
        User guessingUser = null;
        for(Map.Entry<User, String> user : players){
            if(user.getValue().equals(sessionId)){
                guessingUser = user.getKey();
            }
        }
        boolean allGuessesCameIn = evaluation.addGuess(guessingUser,guess.getNrOfWrongPlacedCards());
        if(allGuessesCameIn){

        }
    }
    public void performEvaluationAfterGuessPresentOrCdEnded(){
        //if this returns true, all guesses have came in. -> evaluate
        evaluationCountdown.doStop();//stop cd
        evaluation.shareTokens(nrOfWrongCards);
        gameService.sendEvaluatedGameStateToUsers(id);
        activeBoard.setPlacedCard(0);
    }
    public EvaluatedGameStateDTO evaluate(){
        EvaluatedGameStateDTO evaluationState = new EvaluatedGameStateDTO();
        List<EvaluatedCardDTO> evaluatedTop = new ArrayList<>();
        List<EvaluatedCardDTO> evaluatedBottom = new ArrayList<>();
        List<EvaluatedCardDTO> evaluatedLeft = new ArrayList<>();
        List<EvaluatedCardDTO> evaluatedRight = new ArrayList<>();
        evaluationState.setStartingCard(CardMapper.ConvertEntityToCardDTO(activeBoard.getStartingCard()));


        ValueCategory verticalCategory = this.getCurrentSettings().getVerticalValueCategory();
        ValueCategory horizontalCategory = this.getCurrentSettings().getHorizontalValueCategory();

        Card loopCard = activeBoard.getStartingCard();//start with startingcard
        int positionCounter = 1;
        //go up
        while(loopCard.getHigherNeighbour()!= null){
            try {
                boolean correct = verticalCategory.isPlacementCorrect(loopCard, loopCard.getHigherNeighbour());
                EvaluatedCardDTO evaluatedCardDTO = CardMapper.ConvertEntityToEvaluatedCardDTO(loopCard.getHigherNeighbour(), correct);
                evaluatedCardDTO.setPosition(positionCounter);
                evaluatedTop.add(evaluatedCardDTO);//add neighbour
                if(!correct){//card wrong -> add one to counter
                    nrOfWrongCards++;
                }
            } catch (Exception e){}
            loopCard= loopCard.getHigherNeighbour();
            positionCounter++;
        }

        positionCounter = 1;
        loopCard = activeBoard.getStartingCard();//start with startingcard
        //go down
        while(loopCard.getLowerNeighbour()!= null){
            try {
                boolean correct = verticalCategory.isPlacementCorrect(loopCard, loopCard.getLowerNeighbour());
                EvaluatedCardDTO evaluatedCardDTO = CardMapper.ConvertEntityToEvaluatedCardDTO(loopCard.getLowerNeighbour(), correct);
                evaluatedCardDTO.setPosition(positionCounter);
                evaluatedBottom.add(evaluatedCardDTO);//add neighbour
                if(!correct){//card wrong -> add one to counter
                    nrOfWrongCards++;
                }
            }
            catch (Exception e) {           }

            loopCard= loopCard.getLowerNeighbour();
        }
        loopCard = activeBoard.getStartingCard();//start with startingcard


        positionCounter = 1;
        //go left
        while(loopCard.getLeftNeighbour()!= null){
            try {
                boolean correct = horizontalCategory.isPlacementCorrect(loopCard, loopCard.getLeftNeighbour());
                EvaluatedCardDTO evaluatedCardDTO = CardMapper.ConvertEntityToEvaluatedCardDTO(loopCard.getLeftNeighbour(), correct);
                evaluatedCardDTO.setPosition(positionCounter);
                evaluatedLeft.add(evaluatedCardDTO);//add neighbour
                if(!correct){//card wrong -> add one to counter
                    nrOfWrongCards++;
                }
            }
            catch (Exception e) {           }
            loopCard= loopCard.getLeftNeighbour();
        }
        loopCard = activeBoard.getStartingCard();//start with startingcard

        positionCounter = 1;
        //go right
        while(loopCard.getRightNeighbour()!= null){
            try {
                boolean correct = horizontalCategory.isPlacementCorrect(loopCard, loopCard.getRightNeighbour());
                EvaluatedCardDTO evaluatedCardDTO = CardMapper.ConvertEntityToEvaluatedCardDTO(loopCard.getRightNeighbour(), correct);
                evaluatedCardDTO.setPosition(positionCounter);
                evaluatedRight.add(evaluatedCardDTO);//add neighbour
                if(!correct){//card wrong -> add one to counter
                    nrOfWrongCards++;
                }
            }
            catch (Exception e) {           }
            loopCard= loopCard.getRightNeighbour();
        }
        evaluationState.setTop(evaluatedTop);
        evaluationState.setBottom(evaluatedBottom);
        evaluationState.setLeft(evaluatedLeft);
        evaluationState.setRight(evaluatedRight);

        //evaluationState.setCards(evaluatedCards);
        evaluationState.setGamestate(this.activeState.toString());
        evaluationState.setPlayersturn(this.currentPlayer.getKey().getId());
        evaluationState.setNextCardOnStack(CardMapper.ConvertEntityToCardDTO(this.nextCard));
        return evaluationState;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Card getNextCard() {
        return nextCard;
    }

    public List<User> getJoinedPlayer() {
        return waitingForPlayers;
    }

    public Queue<Map.Entry<User, String>> getPlayers() {
        return players;
    }

    public Map.Entry<User, String> getCurrentPlayer() {
        return currentPlayer;
    }

    public GameSettings getCurrentSettings() {
        return currentSettings;
    }

    public void setGameService(GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * Can be accessed only once (At the start of the game).
     * Needs to be called because Host would take his turn twice because the currentPlayer has been initialized and the queue has host at the top.
     * This is basically a turn without any action.
     */
    public void initializeGameWhenFull(){
        //rearrangement of player queue
        players.add(currentPlayer);
        players.remove();
        //first cd handling:
        this.turnCountdown = new PlayersTurnCountdown(currentSettings.getPlayerTurnCountdown(), this, currentPlayer.getKey());
        turnCountdown.addPropertyChangeListener(this);
        turnCountdown.start();
        //send first gamestate:
        activeState = GameState.CARDPLACEMENT; //might not be necessary.
        gameService.sendGameStateToUsers(id);

    }

}
