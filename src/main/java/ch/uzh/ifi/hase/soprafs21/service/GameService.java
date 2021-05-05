package ch.uzh.ifi.hase.soprafs21.service;


import ch.uzh.ifi.hase.soprafs21.entity.Cards.Card;
import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.GameLobby;
import ch.uzh.ifi.hase.soprafs21.entity.GameSettings;
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
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Couldnt find game with id: "+gameId);
        }
        if(game.getHost().getId() != host.getId()){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the host is allowed to kick players.");
        }
        game = game.removePlayer(userToKick);
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
        gameToEnd.endGame();//remove propertyChangeListeners
        //save earned tokens:
        for(var user : gameToEnd.getPlayers()){
            if(gameToEnd.hasWinner()) {
                //set wins
                if(user.getKey().getId() == gameToEnd.getWinnerId()){
                    userService.saveWins(user.getKey().getId(), 1); //add 1 win
                }
                //set defeats
                if(user.getKey().getId() != gameToEnd.getWinnerId()){
                    userService.saveDefeats(user.getKey().getId(), 1);//add 1 defeat
                }
            }
            //set playtime:
            long diffInMillies = Math.abs((new Date()).getTime() - gameToEnd.getStartTime().getTime());
            long minutesPlayed = TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS);
            //playtime and token are only set if a game lasted longer than 3 min.
            if(minutesPlayed >=3){
                //set tokens
                userService.saveEarnedTokens(user.getKey().getId(), user.getKey().getCurrentToken());
                //set playtime:
                userService.saveGameTime(user.getKey().getId(), minutesPlayed);
            }
        }
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
