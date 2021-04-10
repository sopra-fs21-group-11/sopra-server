package ch.uzh.ifi.hase.soprafs21.service;


import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.GameLobby;
import ch.uzh.ifi.hase.soprafs21.entity.User;
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

    public void test(long id){
        try {
            Game game = getRunningGameById(id);
            for(var thing : game.getPlayers()){
                Map<String, String> location = new HashMap<String, String>();
                location.put("location", "test" );
                template.convertAndSendToUser(thing.getValue(),"/topic/countdown", location);
            }

            String asd = "qwer";
        } catch (Exception ex){
            String exept = ex.getMessage();
        }

    }

    public Game joinRunningGame(User user, String sessionId, long gameId){
        Game gameToJoin = getRunningGameById(gameId);
        gameToJoin.joinGame(user, sessionId);
        return gameToJoin;
    }

    public boolean gameIsFull(long gameId){
        Game FullGame = getRunningGameById(gameId);
        if(FullGame.getJoinedPlayer().size() != FullGame.getPlayers().size()){//TODO: lacks check if too many players joined
            return false;
        }else{
            return true;
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

    public Game getRunningGameById(long id){
        for(Game game: this.runningGames){
            if(game.getId()==id){
                return game;
            }
        }
        return null;
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
