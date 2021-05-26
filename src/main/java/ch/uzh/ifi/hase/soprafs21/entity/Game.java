package ch.uzh.ifi.hase.soprafs21.entity;

import ch.uzh.ifi.hase.soprafs21.Application;
import ch.uzh.ifi.hase.soprafs21.constant.GameState;
import ch.uzh.ifi.hase.soprafs21.entity.Cards.Card;
import ch.uzh.ifi.hase.soprafs21.entity.ValueCategories.ValueCategory;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.CardMapper;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs21.rest.socketDTO.*;
import ch.uzh.ifi.hase.soprafs21.service.GameService;
import ch.uzh.ifi.hase.soprafs21.service.countdown.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Game implements PropertyChangeListener {
    private Queue<Map.Entry<User, String>> players;
    private long id;
    private Board activeBoard;
    private Map.Entry<User, String> currentPlayer;
    private GameState activeState;
    private Deck deckStack;
    private long hostPlayerId;
    private List<User> waitingForPlayers;
    private Card nextCard;
    private ValueCategory horizontalValueCategory;
    private ValueCategory verticalValueCategory;
    private boolean gameStarted;

    private final GameSettings currentSettings;

    private GameService gameService;

    private Date startTime;
    private CountdownHelper doubtCountdown;
    private CountdownHelper visibleCountdown;
    private CountdownHelper turnCountdown;
    private CountdownHelper evaluationCountdown;
    private CountdownHelper evaluationVisibleCountdown;

    private Evaluation evaluation;
    private int nrOfWrongCards;

    private int breakDownCounter = 0;

    private List<Long> winnerIds = new ArrayList<>();

    private int cardsBeforeEvaluation;

    public Game(GameLobby lobby, Deck deckToPlay){
        //set up the game-object with all details of the lobby:
        this.waitingForPlayers = lobby.getPlayers();
        this.currentSettings = lobby.getSettings();
        this.hostPlayerId = lobby.getHostId();
        this.activeState = GameState.CARDPLACEMENT;
        this.id = lobby.getId();
        this.verticalValueCategory = lobby.getSettings().getVerticalValueCategory();
        this.horizontalValueCategory = lobby.getSettings().getHorizontalValueCategory();
        this.gameStarted = false;
        this.deckStack = deckToPlay;
        //We set the starting-card and the nextCard right away:
        this.activeBoard = new Board(deckStack.pop());
        this.nextCard = deckStack.pop();
        this.players = new LinkedList<>();
        //player List generation:
        for(var player : lobby.getPlayers()){
            var playerToAdd = new AbstractMap.SimpleEntry<>(player, "");
            players.add(playerToAdd);
        }
        cardsBeforeEvaluation = deckStack.size()/ currentSettings.getNrOfEvaluations();
        deckStack.shuffleDeck();
    }

    public boolean joinGame(User user, String sessionId){
        //we check if the player was already in the game with a different sessionId:
        for(var player : players){
            if(player.getKey().getId() == user.getId()){
                //change sessionId such that the player gets the next gameState and return.
                if(player.getValue() != ""){ //player has played before and lost connection -> we send an extra gamestate
                    gameService.sendSeparateGameState(id, user.getId());
                }
                player.setValue(sessionId);
                return true;
            }
        }
        if(this.hostPlayerId == user.getId()){
            this.currentPlayer = new AbstractMap.SimpleEntry<>(user, sessionId); //host starts the game.
        }
        return true;
    }

    public void performTurn(long userid, Card cardToPlace, int placementIndex, String axis) {
        if (currentPlayer.getKey().getId() != userid) {
            return;
        }
        if(!turnCountdown.isAlive()){
            Application.logger.info("Perform turn rejected: Userid: "+Long.toString(userid));
            return;
        }
        Application.logger.info("Perform turn with nextCard: "+nextCard.getLocationName());
        activeBoard.placeCard(cardToPlace, placementIndex, axis);
        this.turnCountdown.doStop();
    }

    public void propertyChange(PropertyChangeEvent evt){
        String senderProperty = evt.getPropertyName();
        if(!senderProperty.endsWith(Long.toString(id))){ //if the id isnt ours, we skip.
            return;
        }
        if(this.activeState == GameState.GAMEEND){
            return;
        }
        senderProperty = senderProperty.replaceAll("[0-9]", "");//remove all digits
        //DoubtCountdown ended whithout any doubt incoming -> next turn
        //Doubt Visible Countdown
        //either the doubt is finished or noone has doubted.
        if(senderProperty.equals("DoubtCdEnded")||senderProperty.equals("DoubtVisibleCdEnded"))//which property has changed?
        {
            Application.logger.info(Long.toString(id)+":\t"+senderProperty);

            currentPlayer = players.remove(); //switch currentUser
            players.add(currentPlayer);

            if(deckStack.isEmpty()) {
                //start evaluation:

                activeState = GameState.EVALUATION;
                gameService.sendGameStateToUsers(id);
                this.evaluationCountdown = new WaitForGuessCountdown(currentSettings.getEvaluationCountdown(), this);
                evaluationCountdown.addPropertyChangeListener(this);
                evaluationCountdown.start();
                //initialize new evaluation
                evaluation = new Evaluation(players, currentSettings.getTokenGainOnCorrectGuess(), currentSettings.getTokenGainOnNearestGuess());
                return;
            }
            else{
                Application.logger.info("old nextCard: "+nextCard.getLocationName());
                nextCard = deckStack.pop();
                Application.logger.info("new nextCard: "+nextCard.getLocationName());

            }

            //Two cases: Either we start an evaluation if we have enough cards lying or we continue with next turn.
            //check if we need to go in evaluation:
            if (activeBoard.getPlacedCard() == cardsBeforeEvaluation) {
                //start evaluation
                activeState = GameState.EVALUATION;
                gameService.sendGameStateToUsers(id);
                this.evaluationCountdown = new WaitForGuessCountdown(currentSettings.getEvaluationCountdown(), this);
                evaluationCountdown.addPropertyChangeListener(this);
                evaluationCountdown.start();
                //initialize new evaluation
                evaluation = new Evaluation(players, currentSettings.getTokenGainOnCorrectGuess(), currentSettings.getTokenGainOnNearestGuess());
            }
            else {
                this.turnCountdown = new PlayersTurnCountdown(currentSettings.getPlayerTurnCountdown(), this, currentPlayer.getKey());
                turnCountdown.addPropertyChangeListener(this);
                turnCountdown.start();
                activeState = GameState.CARDPLACEMENT;
                gameService.sendGameStateToUsers(id);
            }
        }
        if(senderProperty.equals("DoubtCdStopped")) {//Doubt incoming. we start visiblecd:
            Application.logger.info(Long.toString(id)+":\tDoubtCdStopped");

            activeState=GameState.DOUBTVISIBLE;
            this.visibleCountdown = new DoubtVisibleCountdown(currentSettings.getVisibleAfterDoubtCountdown(), this);
            visibleCountdown.addPropertyChangeListener(this);
            visibleCountdown.start();
            //doubt dto is sent by doubt methods
        }
        //EvaluationCountdown ended. Evaluate even not all guesses are here & start visiblecd
        //the same goes for when the guesscd has stopped.
        if(senderProperty.equals("GuessCdEnded")|| senderProperty.equals("GuessCdStopped")) {
            Application.logger.info(Long.toString(id)+":\tGuessCdStopped");
            performEvaluationAfterGuessPresentOrCdEnded();
            activeState = GameState.EVALUATIONVISIBLE;
            gameService.sendEvaluatedGameStateToUsers(id);
            this.evaluationVisibleCountdown = new EvaluationVisibleCountdown(currentSettings.getEvaluationCountdownVisible(), this);
            evaluationVisibleCountdown.addPropertyChangeListener(this);
            evaluationVisibleCountdown.start();
        }
        //start next turn
        if(senderProperty.equals("PlayerTurnCdEnded")) {
            Application.logger.info(Long.toString(id)+":\tPlayerTurnCdEnded");
            if(breakDownCounter>5){
                  gameService.gameEnded(id);
                  return;
            }
            breakDownCounter++;
            //PlayerCountdown has ended -> next players turn.
            currentPlayer = players.remove();
            players.add(currentPlayer);
            //start new playerCountdown:
            gameService.sendGameStateToUsers(id);
            this.turnCountdown = new PlayersTurnCountdown(currentSettings.getPlayerTurnCountdown(), this, currentPlayer.getKey());
            turnCountdown.addPropertyChangeListener(this);
            turnCountdown.start();
        }
        if(senderProperty.equals("PlayerTurnCdStopped")) {//A player performed a turn -> goto doubtingPhase
            Application.logger.info(Long.toString(id)+":\tPlayerTurnCdStopped");
            breakDownCounter=0; //setback BreakdownCounter
            this.activeState = GameState.DOUBTINGPHASE;
            gameService.sendGameStateToUsers(id);
            this.doubtCountdown = new DoubtCountdown(currentSettings.getDoubtCountdown(), this, currentPlayer.getKey());
            doubtCountdown.addPropertyChangeListener(this);
            doubtCountdown.start();
        }
        if(senderProperty.equals("EvaluationVisibleCdEnded")) {
            Application.logger.info(Long.toString(id)+":\tEvaluationVisibleCdEnded");
            //check if deck is empty. if so, game is finished.
            if(deckStack.isEmpty()){
                //game ended and we have a regular winner:
                int winnerTokens = 0;
                for(var player:players){
                    if(player.getKey().currentToken>=winnerTokens){
                        this.winnerIds.add(player.getKey().getId());
                        winnerTokens = player.getKey().currentToken;
                    }
                }
                gameService.gameEnded(id);
                return;
            }
            //cleanup board and set new startingcard:
            activeBoard.clearBoard(deckStack.pop());
            nextCard = deckStack.pop();//maybe we need to assign the next player here... idk.

            activeState=GameState.CARDPLACEMENT;
            this.turnCountdown = new PlayersTurnCountdown(currentSettings.getPlayerTurnCountdown(), this, currentPlayer.getKey());
            turnCountdown.addPropertyChangeListener(this);
            turnCountdown.start();

            //next turn and stuff:
            currentPlayer = players.remove();
            players.add(currentPlayer);
            nextCard = deckStack.pop();
            gameService.sendGameStateToUsers(id);
        }
    }

    /**
     * Call this method to remove any propertyListener
     */
    public void removeAllPropertyListener(){
        try {
            activeState = GameState.GAMEEND;
            //gameService.sendGameStateToUsers(id);
            //first we remove any countdown eventListener. Not that we run into strange happenings...
            doubtCountdown.removePropertyChangeListener(this);
            visibleCountdown.removePropertyChangeListener(this);
            turnCountdown.removePropertyChangeListener(this);
            evaluationCountdown.removePropertyChangeListener(this);
            evaluationVisibleCountdown.removePropertyChangeListener(this);
        } catch(Exception ex){ }
    }

    /**
     * Call this method to clear all session IDs
     */
    public void clearSessionIds(){
        try{
            for(var user : players){
                user.setValue("");
            }
        } catch(Exception ex){ }
    }

    public GameEndDTO createGameEndDTO(){
        List<UserGetDTO> scoreboardList = new ArrayList<>();
        for(var player : players){
            scoreboardList.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(player.getKey()));
        }
        // sort the scoreboardList
        Comparator<UserGetDTO> descCurrentTokenComp = (UserGetDTO u1, UserGetDTO u2) -> (int) (u2.getCurrentToken() - u1.getCurrentToken());
        Collections.sort(scoreboardList, descCurrentTokenComp);

        GameEndDTO gameEndDTO = new GameEndDTO();
        gameEndDTO.setScoreboard(scoreboardList);
        gameEndDTO.setGameId(id);

        if(winnerIds == null){
            gameEndDTO.setWinnerIds(winnerIds);
        }

        //set playtime:
        long diffInMillis = Math.abs((new Date()).getTime() - startTime.getTime());
        long minutesPlayed = TimeUnit.MINUTES.convert(diffInMillis, TimeUnit.MILLISECONDS);
        gameEndDTO.setGameMinutes(minutesPlayed);
        if(minutesPlayed<2){
            gameEndDTO.setGameTooShort(true);
        }
        else{
            gameEndDTO.setGameTooShort(false);
        }
        return gameEndDTO;
    }

    public void performDoubt(String sessionId, int placedCard, int doubtedCard){
        if(!doubtCountdown.isAlive()){//countdown is not running -> we dont accept.
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
        if(doubtingUser == null){//user not in game
            return;
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
            if(doubtedUser.currentToken!=0) {
                doubtedUser.currentToken--;
            }
            doubtingUser.currentToken++;
        } else{//doubt is wrong
            doubtedUser.currentToken++;
            if(doubtingUser.currentToken!=0) {
                doubtingUser.currentToken--;
            }
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

        gameStateDTO.setPlayersturn(DTOMapper.INSTANCE.convertEntityToUserGetDTO(this.currentPlayer.getKey()));

        Object[] obj = players.toArray();
        gameStateDTO.setNextPlayer(DTOMapper.INSTANCE.convertEntityToUserGetDTO(((Map.Entry<User, String>)obj[0]).getKey()));

        return gameStateDTO;
    }

    public void parseEvaluationGuess(String sessionId, GameGuessDTO guess){
        if(!evaluationCountdown.isAlive()) {
            //if cd is not alive anymore, we do nothing
            return;
        }
        User guessingUser = null;
        for(Map.Entry<User, String> user : players){
            if(user.getValue().equals(sessionId)){
                guessingUser = user.getKey();
            }
        }
        boolean allGuessesCameIn = evaluation.addGuess(guessingUser,Integer.parseInt(guess.getNrOfWrongPlacedCards()));
        if(allGuessesCameIn){
            evaluationCountdown.doStop();//we stop the cd as soon as all guesses came in. -> onPropertyChange() handles the rest.
        }
    }

    public void performEvaluationAfterGuessPresentOrCdEnded(){
        //if this returns true, all guesses have came in. -> evaluate
        //evaluationCountdown.doStop();//no need to stop cd since this method only gets called when the cd has stopped anyway.
        //evaluation.shareTokens(nrOfWrongCards);
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

        evaluationState.setGamestate(this.activeState.toString());

        evaluationState.setPlayersturn(DTOMapper.INSTANCE.convertEntityToUserGetDTO(this.currentPlayer.getKey()));
        Object[] obj = players.toArray();
        evaluationState.setNextPlayer(DTOMapper.INSTANCE.convertEntityToUserGetDTO(((Map.Entry<User, String>)obj[0]).getKey()));

        evaluationState.setNextCardOnStack(CardMapper.ConvertEntityToCardDTO(this.nextCard));

        this.evaluation.shareTokens(nrOfWrongCards);
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

    public long getHostPlayerId() {
        return hostPlayerId;
    }

    public List<Long> getWinnerId() {
        return winnerIds;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    /**
     * Can be accessed only once (At the start of the game).
     * Needs to be called because Host would take his turn twice because the currentPlayer has been initialized and the queue has host at the top.
     * This is basically a turn without any action.
     */
    public void initializeGameWhenFull(){
        //first cd handling:
        this.gameStarted = true;
        currentPlayer = players.remove();
        players.add(currentPlayer);
        this.turnCountdown = new PlayersTurnCountdown(currentSettings.getPlayerTurnCountdown(), this, currentPlayer.getKey());
        turnCountdown.addPropertyChangeListener(this);
        turnCountdown.start();

        //set gameStartTime:
        startTime = new Date();
    }
}
