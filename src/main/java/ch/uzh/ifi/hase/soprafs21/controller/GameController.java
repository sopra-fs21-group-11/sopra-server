package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.entity.GameLobby;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.GameKickPutDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.GameMapper;
import ch.uzh.ifi.hase.soprafs21.service.GameService;
import ch.uzh.ifi.hase.soprafs21.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class GameController {

    private final UserService userService;
    private final GameService gameService;

    GameController(UserService userService, GameService gameService) {
        this.userService = userService;
        this.gameService = gameService;
    }

    @GetMapping("/games")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<GameGetDTO> getAllGames() {
        List<GameLobby> allGames = gameService.getAllOpenGames();
        List<GameGetDTO> allGamesDTO = new ArrayList<GameGetDTO>();
        for(GameLobby lobby : allGames) {
            allGamesDTO.add(GameMapper.ConvertEntityToGameGetDTO(lobby));
        }
        return allGamesDTO;
    }

    @GetMapping("/games/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGetDTO getGameById(@PathVariable long id) {
        GameLobby game = gameService.getGameById(id);

        return GameMapper.ConvertEntityToGameGetDTO(game);
    }

    @PostMapping("/games")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseEntity createGame(@RequestHeader("Authorization") String token) {
        User hostUser = userService.getUserByToken(token);
        if(hostUser == null){
            return ResponseEntity.status(404).body(null);
        }
        GameLobby newGame = gameService.createNewGameLobby(hostUser);

        String url = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(newGame.getId()).toString();
        Map<String, String> location = new HashMap<String, String>();
        location.put("location", url );
        // convert internal representation of user back to API
        return ResponseEntity.status(201).body(location);

    }

    @PostMapping("/games/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GamePostDTO joinGame(@PathVariable long id, @RequestHeader("Authorization") String token) {
        User joiningUser = userService.getUserByToken(token);
        if(joiningUser == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Joining user couldnt be identified by token.");
        }
        GameLobby joinedGame = gameService.joinGame(userService.getUserByToken(token), id);
        return GameMapper.ConvertEntityToGamePostDTO(joinedGame);

    }

    @PutMapping("/games/{id}/kick")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GamePostDTO kickPlayer(@PathVariable long id, @RequestHeader("Authorization") String token, @RequestBody GameKickPutDTO gameKickPutDTO) {
        User hostUser = userService.getUserByToken(token);
        User userToKick = userService.getUser(gameKickPutDTO.getKickPlayerId());
        if(hostUser == null || userToKick == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Host or user to kick couldn't be found.");
        }
        GameLobby kickedGame = gameService.kickPlayer(hostUser, userToKick, id);
        return GameMapper.ConvertEntityToGamePostDTO(kickedGame);

    }
}
