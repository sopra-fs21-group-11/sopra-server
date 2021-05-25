package ch.uzh.ifi.hase.soprafs21.rest.mapper;

import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.GameLobby;
import ch.uzh.ifi.hase.soprafs21.entity.GameSettings;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.GameSettingsDTO;
import ch.uzh.ifi.hase.soprafs21.rest.socketDTO.GameDoubtDTO;
import ch.uzh.ifi.hase.soprafs21.rest.socketDTO.GameStateDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameMapper {
    public static GameGetDTO ConvertEntityToGameGetDTO(GameLobby gameLobby){

        GameSettings settings = gameLobby.getSettings();
        GameGetDTO gameGetDTO = new GameGetDTO();
        gameGetDTO.setId(gameLobby.getId());
        gameGetDTO.setName(gameLobby.getName());
        gameGetDTO.setDoubtCountdown(settings.getDoubtCountdown());
        //gameGetDTO.setHostId(gameLobby.getHostId());
        gameGetDTO.setPlayersMax(settings.getPlayersMax());
        gameGetDTO.setPlayersMin(settings.getPlayersMin());
        gameGetDTO.setHorizontalValueCategoryId(settings.getHorizontalValueCategory().getId());
        gameGetDTO.setVerticalValueCategoryId(settings.getVerticalValueCategory().getId());
        gameGetDTO.setNrOfEvaluations(settings.getNrOfEvaluations());
        gameGetDTO.setPlayerTurnCountdown(settings.getPlayerTurnCountdown());
        gameGetDTO.setTokenGainOnCorrectGuess(settings.getTokenGainOnCorrectGuess());
        gameGetDTO.setTokenGainOnNearestGuess(settings.getTokenGainOnNearestGuess());
        gameGetDTO.setVisibleAfterDoubtCountdown(settings.getVisibleAfterDoubtCountdown());
        gameGetDTO.setNrOfStartingTokens(settings.getNrOfStartingTokens());
        gameGetDTO.setGameStarted(false);
        gameGetDTO.setEvaluationCountdown(settings.getEvaluationCountdown());
        gameGetDTO.setEvaluationCountdownVisible(settings.getEvaluationCountdownVisible());
        gameGetDTO.setDeckId(settings.getDeckId());

        List<Long> playerList = new ArrayList<>();
        for(User user : gameLobby.getPlayers()){
            playerList.add(user.getId());
        }
        //gameGetDTO.setPlayers(playerList);

        return gameGetDTO;
    }

    public static GameSettingsDTO ConvertEntityToGameSettingsDTO(GameSettings gameSettings){
        GameSettingsDTO gameSettingsDTO = new GameSettingsDTO();
        gameSettingsDTO.setPlayersMin(gameSettings.getPlayersMin());
        gameSettingsDTO.setPlayersMax(gameSettings.getPlayersMax());
        gameSettingsDTO.setNrOfEvaluations(gameSettings.getNrOfEvaluations());
        gameSettingsDTO.setDoubtCountdown(gameSettings.getDoubtCountdown());
        gameSettingsDTO.setVisibleAfterDoubtCountdown(gameSettings.getVisibleAfterDoubtCountdown());
        gameSettingsDTO.setPlayerTurnCountdown(gameSettings.getPlayerTurnCountdown());
        gameSettingsDTO.setEvaluationCountdown(gameSettings.getEvaluationCountdown());
        gameSettingsDTO.setEvaluationCountdownVisible(gameSettings.getEvaluationCountdownVisible());
        gameSettingsDTO.setTokenGainOnCorrectGuess(gameSettings.getTokenGainOnCorrectGuess());
        gameSettingsDTO.setTokenGainOnNearestGuess(gameSettings.getTokenGainOnNearestGuess());
        gameSettingsDTO.setHorizontalValueCategoryId(gameSettings.getHorizontalValueCategory());
        gameSettingsDTO.setVerticalValueCategoryId(gameSettings.getVerticalValueCategory());
        gameSettingsDTO.setNrOfStartingTokens(gameSettings.getNrOfStartingTokens());
        gameSettingsDTO.setDeckId(gameSettings.getDeckId());

        return gameSettingsDTO;
    }

    public static GameSettings ConvertGameSettingsDTOToEntity(GameSettingsDTO gameSettingsDTO){
        GameSettings gameSettings = new GameSettings();
        gameSettings.setPlayersMin(gameSettingsDTO.getPlayersMin());
        gameSettings.setPlayersMax(gameSettingsDTO.getPlayersMax());
        gameSettings.setNrOfEvaluations(gameSettingsDTO.getNrOfEvaluations());
        gameSettings.setDoubtCountdown(gameSettingsDTO.getDoubtCountdown());
        gameSettings.setVisibleAfterDoubtCountdown(gameSettingsDTO.getVisibleAfterDoubtCountdown());
        gameSettings.setPlayerTurnCountdown(gameSettingsDTO.getPlayerTurnCountdown());
        gameSettings.setEvaluationCountdown(gameSettingsDTO.getEvaluationCountdown());
        gameSettings.setEvaluationCountdownVisible(gameSettingsDTO.getEvaluationCountdownVisible());
        gameSettings.setTokenGainOnCorrectGuess(gameSettingsDTO.getTokenGainOnCorrectGuess());
        gameSettings.setTokenGainOnNearestGuess(gameSettingsDTO.getTokenGainOnNearestGuess());
        gameSettings.setNrOfStartingTokens(gameSettingsDTO.getNrOfStartingTokens());
        gameSettings.setDeckId(gameSettingsDTO.getDeckId());
        return gameSettings;
    }

    public static GameGetDTO ConvertRunningGameToGetDTO(Game game){
        GameSettings settings = game.getCurrentSettings();

        GameGetDTO gameGetDTO = new GameGetDTO();
        gameGetDTO.setId(game.getId());
        gameGetDTO.setName("Running Game - No name");
        gameGetDTO.setDoubtCountdown(settings.getDoubtCountdown());
        //gameGetDTO.setHostId(0);
        gameGetDTO.setPlayersMax(settings.getPlayersMax());
        gameGetDTO.setPlayersMin(settings.getPlayersMin());
        gameGetDTO.setHorizontalValueCategoryId(settings.getHorizontalValueCategory().getId());
        gameGetDTO.setVerticalValueCategoryId(settings.getVerticalValueCategory().getId());
        gameGetDTO.setNrOfEvaluations(settings.getNrOfEvaluations());
        gameGetDTO.setPlayerTurnCountdown(settings.getPlayerTurnCountdown());
        gameGetDTO.setTokenGainOnCorrectGuess(settings.getTokenGainOnCorrectGuess());
        gameGetDTO.setTokenGainOnNearestGuess(settings.getTokenGainOnNearestGuess());
        gameGetDTO.setVisibleAfterDoubtCountdown(settings.getVisibleAfterDoubtCountdown());
        gameGetDTO.setNrOfStartingTokens(settings.getNrOfStartingTokens());
        gameGetDTO.setGameStarted(false);
        gameGetDTO.setEvaluationCountdown(settings.getEvaluationCountdown());
        gameGetDTO.setEvaluationCountdownVisible(settings.getEvaluationCountdownVisible());
        gameGetDTO.setDeckId(settings.getDeckId());

        List<Long> playerList = new ArrayList<>();
        for(Map.Entry<User, String>  user : game.getPlayers()){
            playerList.add(user.getKey().getId());
        }
        //gameGetDTO.setPlayers(playerList);
        gameGetDTO.setGameStarted(true);
        return gameGetDTO;
    }

    public static GamePostDTO ConvertEntityToGamePostDTO(GameLobby gameLobby){

        GameSettings settings = gameLobby.getSettings();
        GamePostDTO gamePostDTO = new GamePostDTO();
        gamePostDTO.setId(gameLobby.getId());
        gamePostDTO.setName(gameLobby.getName());
        gamePostDTO.setDoubtCountdown(settings.getDoubtCountdown());
        gamePostDTO.setHostId(gameLobby.getHostId());
        gamePostDTO.setPlayersMax(settings.getPlayersMax());
        gamePostDTO.setPlayersMin(settings.getPlayersMin());
        gamePostDTO.setHorizontalValueCategoryId(settings.getHorizontalValueCategory().getId());
        gamePostDTO.setVerticalValueCategoryId(settings.getVerticalValueCategory().getId());
        gamePostDTO.setNrOfEvaluations(settings.getNrOfEvaluations());
        gamePostDTO.setPlayerTurnCountdown(settings.getPlayerTurnCountdown());
        gamePostDTO.setTokenGainOnCorrectGuess(settings.getTokenGainOnCorrectGuess());
        gamePostDTO.setTokenGainOnNearestGuess(settings.getTokenGainOnNearestGuess());
        gamePostDTO.setVisibleAfterDoubtCountdown(settings.getVisibleAfterDoubtCountdown());
        gamePostDTO.setNrOfStartingTokens(settings.getNrOfStartingTokens());
        gamePostDTO.setEvaluationCountdown(settings.getEvaluationCountdown());
        gamePostDTO.setEvaluationCountdownVisible(settings.getEvaluationCountdownVisible());
        gamePostDTO.setDeckId(settings.getDeckId());


        List<Long> playerList = new ArrayList<>();
        for(User user : gameLobby.getPlayers()){
            playerList.add(user.getId());
        }
        gamePostDTO.setPlayers(playerList);

        return gamePostDTO;
    }

    public static GameDoubtDTO ConvertGameStateDTOToGameDoubtDTO(GameStateDTO gameStateDTO){
        GameDoubtDTO gameDoubtDTO = new GameDoubtDTO();
        gameDoubtDTO.setLeft(gameStateDTO.getLeft());
        gameDoubtDTO.setRight(gameStateDTO.getRight());
        gameDoubtDTO.setTop(gameStateDTO.getTop());
        gameDoubtDTO.setBottom(gameStateDTO.getBottom());

        gameDoubtDTO.setStartingCard(gameStateDTO.getStartingCard());

        gameDoubtDTO.setPlayersturn(gameStateDTO.getPlayersturn());
        gameDoubtDTO.setNextPlayer(gameStateDTO.getNextPlayer());
        gameDoubtDTO.setGamestate(gameStateDTO.getGamestate());
        gameDoubtDTO.setNextCardOnStack(gameStateDTO.getNextCardOnStack());

        return gameDoubtDTO;
    }
}
