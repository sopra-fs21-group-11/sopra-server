package ch.uzh.ifi.hase.soprafs21.service;


import ch.uzh.ifi.hase.soprafs21.entity.GameLobby;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class GameService {

    private List<GameLobby> openGames;

    @Autowired
    public GameService() {
        openGames = new ArrayList<GameLobby>();
    }


    public List<GameLobby> getAllOpenGames(){
        return openGames;
    }

    public GameLobby createNewGameLobby(User host){
        GameLobby newGame = new GameLobby(host);
        newGame.setId(getNextFreeId());
        openGames.add(newGame);
        return newGame;
    }

    public GameLobby joinGame(User user, long gameId){
        GameLobby gameToJoin = getGameById(gameId);
        if(gameToJoin.getPlayers().size() >= gameToJoin.getSettings().getPlayersMax()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Unable to join. Game is full");
        }
        if(gameToJoin.getPlayers().contains(user)){
            //TODO: multiple joinings are still possible. Needs fix...
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Unable to join. User is already in the game");
        }
        gameToJoin.addPlayer(user);
        return gameToJoin;

    }

    public GameLobby getGameById(long id){
        for(GameLobby game : this.getAllOpenGames()){
            if(game.getId() == id){
                return game;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No game with id "+id+" found.");
    }

    /**
     * Helper method for assigning the gameId
     * @return the next free gameid.
     */
    private long getNextFreeId(){
        long retId = 1;
        for(GameLobby game : openGames){
            if(retId <= game.getId()){
                retId++;
            }
        }
        return retId;
    }
}
