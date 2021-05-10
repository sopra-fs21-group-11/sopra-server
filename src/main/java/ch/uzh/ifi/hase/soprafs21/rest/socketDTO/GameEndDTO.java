package ch.uzh.ifi.hase.soprafs21.rest.socketDTO;

import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserGetDTO;

import java.util.*;

public class GameEndDTO {
    private long gameId;
    private List<UserGetDTO> scoreboard;
    private long gameMinutes;

    public long getGameId() {
        return gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    public List<UserGetDTO> getScoreboard() {
        return scoreboard;
    }

    public void setScoreboard(List<UserGetDTO> scoreboard) {
        this.scoreboard = scoreboard;
    }

    public long getGameMinutes() {
        return gameMinutes;
    }

    public void setGameMinutes(long gameMinutes) {
        this.gameMinutes = gameMinutes;
    }
}
