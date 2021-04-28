package ch.uzh.ifi.hase.soprafs21.service;


import ch.uzh.ifi.hase.soprafs21.entity.Cards.Card;
import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.GameLobby;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.CardMapper;
import ch.uzh.ifi.hase.soprafs21.rest.socketDTO.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class GameService {

    private List<GameLobby> openGames = new ArrayList<>();
    private List<Game> runningGames = new ArrayList<>();

    private SimpMessagingTemplate template;


    @Autowired
    public GameService(SimpMessagingTemplate template) {
         this.template = template;
    }


    public List<GameLobby> getAllOpenGames(){
        return openGames;
    }
    public List<Game> getAllRunningGames(){
        return runningGames;
    }


    public Game startGame(GameLobby gameToStart){
        Game startedGame = gameToStart.StartGame();
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
        if(game.getHost() != host){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the host is allowed to kick players.");
        }
        game.removePlayer(userToKick);
        return game;
    }

    public GameLobby joinGameLobby(User user, long gameId){
        GameLobby gameToJoin = getOpenGameById(gameId);
        if(gameToJoin.getPlayers().size() >= gameToJoin.getSettings().getPlayersMax()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Unable to join. Game is full");
        }
        if(gameToJoin.getPlayers().contains(user)){
            //TODO: multiple joins are still possible. Needs fix...
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Unable to join. User is already in the game");
        }
        gameToJoin.addPlayer(user);
        return gameToJoin;

    }

    public Game joinRunningGame(User user, String sessionId, long gameId){
        Game gameToJoin = getRunningGameById(gameId);
        gameToJoin.joinGame(user, sessionId);
        return gameToJoin;
    }

    public boolean gameIsFull(long gameId){
        Game FullGame = getRunningGameById(gameId);

        if(FullGame.getJoinedPlayer().size() != 0){
            return false;
        }else{
            //the first time, the game is full, we share the starting tokens:
            for(var user : FullGame.getPlayers()){
                user.getKey().setCurrentToken(FullGame.getCurrentSettings().getNrOfStartingTokens());
            }
            //when we start the game we have to rearrange the player queue because host would take a double turn:
            FullGame.rearrangeGame();
            return true;
        }
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
        if(turningUser.getId().equals(game.getCurrentPlayer().getKey().getId())){// is it turningusers turn?
            game.performTurn(turningUser.getId(), game.getNextCard(), placementIndex, axis);
        }
        sendGameStateToUsers(gameId);
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

    public void sendGameStateToUsers(long id){
        Game gameToSend = this.getRunningGameById(id);
        for(var userToSend : gameToSend.getPlayers()){
            String sessionId = userToSend.getValue();
            GameStateDTO gameStateDTO = gameToSend.convertToDTO();
            gameStateDTO.setPlayertokens(userToSend.getKey().getCurrentToken()); //nr of token is userspecific
            this.template.convertAndSend("/topic/game/queue/specific-game-game"+sessionId,gameStateDTO);

        }
    }
    public void sendDoubtResultDTO(long gameId, Card referenceCard, Card doubtedCard, boolean isDoubtRightous){
        Game gameToSend = this.getRunningGameById(gameId);
        DoubtResultDTO resultDTO = new DoubtResultDTO();
        resultDTO.setReferenceCard(CardMapper.ConvertEntityToCardDTO(referenceCard));
        resultDTO.setDoubtedCard(CardMapper.ConvertEntityToCardDTO(doubtedCard));
        resultDTO.setDoubtRightous(isDoubtRightous);
        for(var userToSend: gameToSend.getPlayers()){
            String sessionId = userToSend.getValue();
            this.template.convertAndSend("/topic/game/queue/specific-game-game"+sessionId, resultDTO);
        }

    }

    public void sendEvaluatedGameStateToUsers(long id){
        Game gameToSend = this.getRunningGameById(id);
        for(var userToSend: gameToSend.getPlayers()){
            String sessionId = userToSend.getValue();
            EvaluatedGameStateDTO gameStateDTO = gameToSend.evaluate();
            gameStateDTO.setPlayertokens(userToSend.getKey().getCurrentToken());
            this.template.convertAndSend("/topic/game/queue/specific-game-game"+sessionId, gameStateDTO);
        }
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
