package ch.uzh.ifi.hase.soprafs21.service;


import ch.uzh.ifi.hase.soprafs21.entity.Cards.Card;
import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.GameLobby;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.CardMapper;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.GameMapper;
import ch.uzh.ifi.hase.soprafs21.rest.socketDTO.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class GameService {

    private List<GameLobby> openGames = new ArrayList<>();
    private List<Game> runningGames = new ArrayList<>();

    private SimpMessagingTemplate template;
    private UserService userService;
    private DeckService deckService;

    @Autowired
    public GameService(SimpMessagingTemplate template, UserService userService, DeckService deckService) {
         this.template = template;
         this.userService = userService;
         this.deckService = deckService;
    }

    public List<GameLobby> getAllOpenGames(){
        return openGames;
    }

    public List<Game> getAllRunningGames(){
        return runningGames;
    }

    public Game startGame(GameLobby gameToStart){
        Game startedGame = gameToStart.StartGame(deckService.makeDeckReadyToPlay(gameToStart.getSettings().getDeckId()));
        startedGame.setGameService(this);
        runningGames.add(startedGame);
        openGames.remove(gameToStart);
        return startedGame;
    }

    public GameLobby createNewGameLobby(User host){
        GameLobby newGame = new GameLobby(host);
        newGame.setId(getNextFreeId());
        openGames.add(newGame);
        return newGame;
    }

    public Game doubtAction(long gameId, int placedCard, int doubtedCard, String sessionId){
        Game doubtGame = getRunningGameById(gameId);
        doubtGame.performDoubt(sessionId, placedCard, doubtedCard);
        return doubtGame;
    }

    public GameLobby kickPlayer(User host, User userToKick, long gameId){
        GameLobby game = this.getOpenGameById(gameId);
        if(game == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find game with id: "+gameId);
        }
        if(game.getHost().getId() != host.getId()){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the host is allowed to kick players.");
        }
        game = game.removePlayer(userToKick);
        return game;
    }

    //TODO: tb under construction
    public GameLobby changeSettings(User host, long gameId, GamePostDTO gamePostDTO){
        GameLobby game = this.getOpenGameById(gameId);
        if(game == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find game with id: "+gameId);
        }
        if(game.getHost().getId() != host.getId()){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the host is allowed to change settings.");
        }
        //check if deck is ready to play
        if(!deckService.getDeck(gamePostDTO.getDeckId()).isReadyToPlay()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Deck is not ready to play.");
        }
        if(1 <= gamePostDTO.getNrOfEvaluations() && gamePostDTO.getNrOfEvaluations() <= 4){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Number of evaluation is invalid.");
        }
        //check if minimum cards per evaluation is ok
        int numberOfCardsBeforeEvaluation = deckService.getDeck(gamePostDTO.getDeckId()).getSize() / gamePostDTO.getNrOfEvaluations();
        if(numberOfCardsBeforeEvaluation<5) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Deck is to small to play with this settings");
        }
        if(!(1 <= gamePostDTO.getNrOfEvaluations() && gamePostDTO.getNrOfEvaluations() <= 4)){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Number of evaluation is invalid.");
        }
        if(!(1 <= gamePostDTO.getDoubtCountdown())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "DoubtCountdown is too short.");
        }
        if(!(1 <= gamePostDTO.getVisibleAfterDoubtCountdown())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "VisibleAfterDoubtCountdown is too short.");
        }
        if(!(1 <= gamePostDTO.getEvaluationCountdown())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "EvaluationCountdown is too short.");
        }
        if(!(1 <= gamePostDTO.getPlayerTurnCountdown())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "PlayerTurnCountdown is too short.");
        }

        if(!(2 <= gamePostDTO.getPlayersMax() && gamePostDTO.getPlayersMax() <= 6)){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "PlayersMax is invalid.");
        }
        if(!(2 <= gamePostDTO.getPlayersMax() && gamePostDTO.getPlayersMin() <= 6)){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "PlayersMin is invalid.");
        }

        if(!(1 <= gamePostDTO.getNrOfStartingTokens())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "NrOfStartingTokens is invalid.");
        }
        if(!(0 <= gamePostDTO.getTokenGainOnCorrectGuess())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "TokenGainOnCorrectGuess is invalid.");
        }
        if(!(0 <= gamePostDTO.getTokenGainOnNearestGuess())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "TokenGainOnNearestGuess is invalid.");
        }

        game.getSettings().setDeckId(gamePostDTO.getDeckId());
        game.getSettings().setDoubtCountdown(gamePostDTO.getDoubtCountdown());
        game.getSettings().setVisibleAfterDoubtCountdown(gamePostDTO.getVisibleAfterDoubtCountdown());
        game.getSettings().setEvaluationCountdown(gamePostDTO.getEvaluationCountdown());
        game.getSettings().setEvaluationCountdownVisible(gamePostDTO.getEvaluationCountdownVisible());
        game.getSettings().setPlayerTurnCountdown(gamePostDTO.getPlayerTurnCountdown());
        game.getSettings().setNrOfEvaluations(gamePostDTO.getNrOfEvaluations());
        //game.getSettings().setHorizontalValueCategory(gamePostDTO.getHorizontalValueCategoryId());
        //game.getSettings().setVerticalValueCategory(gamePostDTO.getVerticalValueCategoryId());
        game.getSettings().setPlayersMax(gamePostDTO.getPlayersMax());
        game.getSettings().setPlayersMin(gamePostDTO.getPlayersMin());
        game.getSettings().setNrOfStartingTokens(gamePostDTO.getNrOfStartingTokens());
        game.getSettings().setTokenGainOnCorrectGuess(gamePostDTO.getTokenGainOnCorrectGuess());
        game.getSettings().setTokenGainOnNearestGuess(gamePostDTO.getTokenGainOnNearestGuess());

        return game;
    }

    public GameLobby joinGameLobby(User user, long gameId){
        GameLobby gameToJoin = getOpenGameById(gameId);
        if(gameToJoin.getPlayers().size() >= gameToJoin.getSettings().getPlayersMax()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Unable to join. Game is full");
        }
        if(gameToJoin.getPlayers().contains(user)){
            for(var player : gameToJoin.getPlayers()){
                if(player.getId() == user.getId()){
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Unable to join. User is already in the game");
                }
            }
        }
        gameToJoin.addPlayer(user);
        return gameToJoin;

    }

    public Game joinRunningGame(User user, String sessionId, long gameId){
        Game gameToJoin = getRunningGameById(gameId);
        gameToJoin.joinGame(user, sessionId);
        return gameToJoin;
    }

    public boolean gameIsFull(long gameId) {
        Game FullGame = getRunningGameById(gameId);

        for (var player : FullGame.getPlayers()) {
            if (player.getValue().equals("")) {
                return false;
            }
        }
        //the first time, the game is full, we share the starting tokens:
        for(var user : FullGame.getPlayers()){
            user.getKey().setCurrentToken(FullGame.getCurrentSettings().getNrOfStartingTokens());
        }
        //when we start the game we have to rearrange the player queue because host would take a double turn:
        FullGame.initializeGameWhenFull();
        return true;
    }

    public void gameEnded(long gameId){
        Game gameToEnd = getRunningGameById(gameId);
        gameToEnd.removeAllPropertyListener();//remove propertyChangeListeners

        GameEndDTO gameEndDTO = gameToEnd.createGameEndDTO();

        if(gameEndDTO.getGameTooShort()){
            //Game does not count towards statistic
            for(var user : gameToEnd.getPlayers()) {
                // SendGameEndDTO (every user gets the same) but game does not count was too short!
                this.template.convertAndSend("/topic/game/queue/specific-game-game" + user.getValue(), gameEndDTO);
            }
        }
        else {
            //iterate over each player who played the game
            for(var user : gameToEnd.getPlayers()){
                //set wins
                if(gameToEnd.getWinnerId().contains(user.getKey().getId())) {
                    userService.saveWins(user.getKey().getId(), 1); //add 1 win
                }
                //set defeats
                else{
                    userService.saveDefeats(user.getKey().getId(), 1);//add 1 defeat
                }
                //set tokens
                userService.saveEarnedTokens(user.getKey().getId(), user.getKey().getCurrentToken());
                //set playtime:
                userService.saveGameTime(user.getKey().getId(), gameEndDTO.getGameMinutes());

                //sendGameEndDTO (every user gets the same)
                this.template.convertAndSend("/topic/game/queue/specific-game-game"+user.getValue(),gameEndDTO);
            }
        }
        gameToEnd.clearSessionIds();
        //after saving values we have to remove the game from the list.
        runningGames.remove(gameToEnd);
    }

    public void incomingTurn(long gameId, String sessionId, int placementIndex, String axis){
        Game game = getRunningGameById(gameId);
        User turningUser = new User(); //need to assign. Else the condition turninguser.getid... doesnt work.
        boolean userInGame = false;
        for(var user : game.getPlayers()){
            if(user.getValue().equals(sessionId)){
                turningUser = user.getKey();
                userInGame = true;
            }
        }

        if(!userInGame){return;}
        if(turningUser.getId().equals(game.getCurrentPlayer().getKey().getId())){// is it turning users turn?
            game.performTurn(turningUser.getId(), game.getNextCard(), placementIndex, axis);
        }
    }

    public GameLobby getOpenGameById(long id){
        for(GameLobby game : this.getAllOpenGames()){
            if(game.getId() == id){
                return game;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No game with id "+id+" found.");
    }

    public boolean openGameExists(long id){
        for(GameLobby game : this.getAllOpenGames()){
            if(game.getId() == id){
                return true;
            }
        }
        return false;
    }

    public Game getRunningGameById(long id){
        for(Game game: this.runningGames){
            if(game.getId()==id){
                return game;
            }
        }
        return null;
    }

    public void parseEvaluationGuess(long id, String sessionId, GameGuessDTO guess){
        Game gameToParseEvaluationGuess = this.getRunningGameById(id);
        gameToParseEvaluationGuess.parseEvaluationGuess(sessionId, guess);
    }

    public void sendSeparateGameState(long gameId, long userId){
        Game gameToSend = this.getRunningGameById(gameId);
        for(var userToSend : gameToSend.getPlayers()) {
            if(userToSend.getKey().getId() == userId){
                GameStateDTO gameStateDTO = gameToSend.convertToDTO();
                gameStateDTO.setPlayersturn(userService.getUser(gameStateDTO.getPlayersturn().getId()));
                gameStateDTO.setNextPlayer(userService.getUser(gameStateDTO.getNextPlayer().getId()));
                gameStateDTO.setPlayertokens(userToSend.getKey().getCurrentToken()); //nr of token is userspecific
                this.template.convertAndSend("/topic/game/queue/specific-game-game"+userToSend.getValue(),gameStateDTO);
            }
        }
    }

    public void sendGameStateToUsers(long id){
        Game gameToSend = this.getRunningGameById(id);
        for(var userToSend : gameToSend.getPlayers()){
            String sessionId = userToSend.getValue();
            GameStateDTO gameStateDTO = gameToSend.convertToDTO();
            gameStateDTO.setPlayersturn(userService.getUser(gameStateDTO.getPlayersturn().getId()));
            gameStateDTO.setNextPlayer(userService.getUser(gameStateDTO.getNextPlayer().getId()));
            gameStateDTO.setPlayertokens(userToSend.getKey().getCurrentToken()); //nr of token is userspecific
            this.template.convertAndSend("/topic/game/queue/specific-game-game"+sessionId,gameStateDTO);
        }
    }

    public void sendDoubtResultDTO(long gameId, Card referenceCard, Card doubtedCard, boolean isDoubtRightous){
        Game gameToSend = this.getRunningGameById(gameId);
        GameStateDTO gameStateDTO = gameToSend.convertToDTO();//get gamestate and convert it to gameDoubtDTO
        GameDoubtDTO gameDoubtDTO = GameMapper.ConvertGameStateDTOToGameDoubtDTO(gameStateDTO);
        DoubtResultDTO resultDTO = new DoubtResultDTO();

        resultDTO.setReferenceCard(CardMapper.ConvertEntityToCardDTO(referenceCard));
        resultDTO.setDoubtedCard(CardMapper.ConvertEntityToCardDTO(doubtedCard));
        resultDTO.setDoubtRightous(isDoubtRightous);
        List<Long> neighbours = new ArrayList<>();
        if(doubtedCard.getRightNeighbour()!=null){
            neighbours.add(doubtedCard.getRightNeighbour().getCardId());
        }
        if(doubtedCard.getLeftNeighbour()!=null){
            neighbours.add((doubtedCard.getLeftNeighbour().getCardId()));
        }
        if(doubtedCard.getHigherNeighbour()!=null){
            neighbours.add(doubtedCard.getHigherNeighbour().getCardId());
        }
        if(doubtedCard.getLowerNeighbour()!=null){
            neighbours.add(doubtedCard.getLowerNeighbour().getCardId());
        }
        resultDTO.setDoubtedCardNeighbours(neighbours);

        gameDoubtDTO.setDoubtResultDTO(resultDTO);
        gameDoubtDTO.setPlayersturn(userService.getUser(gameStateDTO.getPlayersturn().getId()));
        gameDoubtDTO.setNextPlayer(userService.getUser(gameStateDTO.getNextPlayer().getId()));

        for(var userToSend: gameToSend.getPlayers()){
            String sessionId = userToSend.getValue();
            gameDoubtDTO.setPlayertokens(userToSend.getKey().getCurrentToken());

            this.template.convertAndSend("/topic/game/queue/specific-game-game"+sessionId, gameDoubtDTO);
        }
    }

    public void sendEvaluatedGameStateToUsers(long id){
        Game gameToSend = this.getRunningGameById(id);
        for(var userToSend: gameToSend.getPlayers()){
            String sessionId = userToSend.getValue();
            EvaluatedGameStateDTO gameStateDTO = gameToSend.evaluate();
            gameStateDTO.setNextPlayer(userService.getUser(gameStateDTO.getNextPlayer().getId()));
            gameStateDTO.setPlayertokens(userToSend.getKey().getCurrentToken());
            this.template.convertAndSend("/topic/game/queue/specific-game-game"+sessionId, gameStateDTO);
        }
    }

    /**
     * check if game-end-request is validated
     * @param gameId id of game
     * @param sessionId sessionId of host
     * @return validationResult
     */
    public boolean endingAllowed(long gameId, String sessionId){
        Game gameToEnd = getRunningGameById(gameId);
        for(var user : gameToEnd.getPlayers()){
            if(user.getValue().equals(sessionId) && user.getKey().getId() == gameToEnd.getHostPlayerId()){
                return true;
            }
        }
        return false;
    }

    /**
     * Helper method for assigning the gameId
     * @return the next free gameid.
     */
    private long getNextFreeId(){

        List<Long> gameIdList = new ArrayList<>();
        long retId = 1;
        for(GameLobby game : openGames){
            gameIdList.add(game.getId());
        }
        for(Game game : runningGames){
           gameIdList.add(game.getId());
        }
        while(gameIdList.contains(retId))
        {
            retId++;
        }
        return retId;
    }
}
