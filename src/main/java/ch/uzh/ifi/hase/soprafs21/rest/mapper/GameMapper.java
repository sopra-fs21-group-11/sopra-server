package ch.uzh.ifi.hase.soprafs21.rest.mapper;

import ch.uzh.ifi.hase.soprafs21.entity.GameLobby;
import ch.uzh.ifi.hase.soprafs21.entity.GameSettings;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.GamePostDTO;

import java.util.ArrayList;
import java.util.List;

public class GameMapper {
    public static GameGetDTO ConvertEntityToGameGetDTO(GameLobby gameLobby){

        GameSettings settings = gameLobby.getSettings();
        GameGetDTO gameGetDTO = new GameGetDTO();
        gameGetDTO.setId(gameLobby.getId());
        gameGetDTO.setName(gameLobby.getName());
        gameGetDTO.setDoubtCountdown(settings.getDoubtCountdown());
        gameGetDTO.setHostId(gameLobby.getHostId());
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

        List<Long> playerList = new ArrayList<>();
        for(User user : gameLobby.getPlayers()){
            playerList.add(user.getId());
        }
        gameGetDTO.setPlayers(playerList);

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

        List<Long> playerList = new ArrayList<>();
        for(User user : gameLobby.getPlayers()){
            playerList.add(user.getId());
        }
        gamePostDTO.setPlayers(playerList);

        return gamePostDTO;
    }
}
