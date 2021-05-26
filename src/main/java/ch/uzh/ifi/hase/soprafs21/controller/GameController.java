package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.GameLobby;
import ch.uzh.ifi.hase.soprafs21.entity.GameSettings;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.*;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.GameMapper;
import ch.uzh.ifi.hase.soprafs21.service.GameService;
import ch.uzh.ifi.hase.soprafs21.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
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

    @PostMapping("/games/{id}/start")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseEntity startGame(@PathVariable long id, @RequestHeader("Authorization") String token) {
        GameLobby gameToStart = gameService.getOpenGameById(id);

        //check if host started the game else -> 403
        User hostUser = userService.getUserByToken(token);
        if(hostUser == null){
            return ResponseEntity.status(404).body(null);
        }
        if(gameToStart.getHostId()!= hostUser.getId())
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only hosts can start games.");
        }

        //check if minimum players is reached.
        if(gameToStart.getSettings().getPlayersMin()>gameToStart.getPlayers().size()){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot start the game. There have to be more players");
        }
        //start actual game
        Game startedGame = gameService.startGame(gameToStart);

        //String url = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(startedGame.getId()).toString();
        Map<String, String> location = new HashMap<String, String>();
        location.put("id", Long.toString(startedGame.getId()) );

        return ResponseEntity.status(200).body(location);
    }

    @GetMapping("/games/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGetDTO getGameById(@PathVariable long id) {
        GameLobby game = null;
        if(gameService.openGameExists(id)) {
            game = gameService.getOpenGameById(id);
        }
        GameGetDTO gameGetDTO = null;
        if(game != null) {
            gameGetDTO = GameMapper.ConvertEntityToGameGetDTO(game);
            gameGetDTO.setHostId(DTOMapper.INSTANCE.convertEntityToUserGetDTO(userService.getUser(game.getHostId())));
            List<UserGetDTO> playingUsers = new ArrayList<>();
            for(var user : game.getPlayers()){
                playingUsers.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
            }
            gameGetDTO.setPlayers(playingUsers);
        } else {
            Game runningGame = gameService.getRunningGameById(id);
            gameGetDTO = GameMapper.ConvertRunningGameToGetDTO(runningGame);
            gameGetDTO.setHostId(DTOMapper.INSTANCE.convertEntityToUserGetDTO(userService.getUser(runningGame.getHostPlayerId())));
            List<UserGetDTO> playingUsers = new ArrayList<>();
            for(var user : runningGame.getPlayers()){
                playingUsers.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user.getKey()));
            }
            gameGetDTO.setPlayers(playingUsers);
        }
        return gameGetDTO;
    }

    @PostMapping("/games")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseEntity createGame(@RequestHeader("Authorization") String token, @RequestBody GamePostDTO gamePostDTO) {
        User hostUser = userService.getUserByToken(token);
        if(hostUser == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Host user not found. Please reformat request.");
        }
        //check if gameSettings are ok, create new gameSettings object
        GameSettings gameSettings = new GameSettings();
        gameSettings.setDeckId(gamePostDTO.getDeckId());
        gameSettings.setPlayersMin(gamePostDTO.getPlayersMin());
        gameSettings.setPlayersMax(gamePostDTO.getPlayersMax());
        gameSettings.setNrOfEvaluations(gamePostDTO.getNrOfEvaluations());
        gameSettings.setDoubtCountdown(gamePostDTO.getDoubtCountdown());
        gameSettings.setVisibleAfterDoubtCountdown(gamePostDTO.getVisibleAfterDoubtCountdown());
        gameSettings.setPlayerTurnCountdown(gamePostDTO.getPlayerTurnCountdown());
        gameSettings.setEvaluationCountdown(gamePostDTO.getEvaluationCountdown());
        gameSettings.setEvaluationCountdownVisible(gamePostDTO.getEvaluationCountdownVisible());
        gameSettings.setTokenGainOnCorrectGuess(gamePostDTO.getTokenGainOnCorrectGuess());
        gameSettings.setTokenGainOnNearestGuess(gamePostDTO.getTokenGainOnNearestGuess());
        gameSettings.setNrOfStartingTokens(gamePostDTO.getNrOfStartingTokens());

        GameLobby newGame = gameService.createNewGameLobby(hostUser, gameSettings);
        if(gamePostDTO.getName().equals("")){
            newGame.setName(Long.toString(newGame.getId()));
        }else {
            newGame.setName(gamePostDTO.getName());
        }

        //String url = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(newGame.getId()).toString();
        Map<String, String> location = new HashMap<String, String>();
        location.put("id", Long.toString(newGame.getId()) );
        // convert internal representation of user back to API
        return ResponseEntity.status(201).body(location);
    }

    @PostMapping("/games/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GamePostDTO joinGame(@PathVariable long id, @RequestHeader("Authorization") String token) {
        User joiningUser = userService.getUserByToken(token);
        if(joiningUser == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Joining user could not be identified by token.");
        }
        GameLobby joinedGame = gameService.joinGameLobby(userService.getUserByToken(token), id);
        return GameMapper.ConvertEntityToGamePostDTO(joinedGame);
    }

    @GetMapping("/games/{id}/leave")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GamePostDTO leaveGame(@PathVariable long id, @RequestHeader("Authorization") String token) {
        User leavingUser = userService.getUserByToken(token);
        if(leavingUser == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Joining user could not be identified by token.");
        }
        GameLobby leavedGame = gameService.leaveGameLobby(leavingUser, id);
        return GameMapper.ConvertEntityToGamePostDTO(leavedGame);
    }

    @PutMapping("/games/{id}/kick")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GamePostDTO kickPlayer(@PathVariable long id, @RequestHeader("Authorization") String token, @RequestBody GameKickPutDTO gameKickPutDTO) {
        User hostUser = userService.getUserByToken(token);
        User userToKick = userService.getUser(gameKickPutDTO.getKickPlayerId());
        if(hostUser == null || userToKick == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Host or user to kick could not be found.");
        }
        GameLobby kickedGame = gameService.kickPlayer(hostUser, userToKick, id);
        return GameMapper.ConvertEntityToGamePostDTO(kickedGame);
    }

    @GetMapping("/games/settings")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameSettingsDTO getDefaultGameSettings() {
        GameSettings defaultGameSettings = new GameSettings();
        return GameMapper.ConvertEntityToGameSettingsDTO(defaultGameSettings);
    }
}
