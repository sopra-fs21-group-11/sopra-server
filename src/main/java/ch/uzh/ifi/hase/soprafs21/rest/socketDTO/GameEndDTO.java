package ch.uzh.ifi.hase.soprafs21.rest.socketDTO;

import ch.uzh.ifi.hase.soprafs21.rest.dto.UserGetDTO;
import java.util.*;

public class GameEndDTO {
    private long gameId;
    private List<UserGetDTO> scoreboard;
    private List<Long> winnerIds;
    private long gameMinutes;
    private Boolean gameTooShort;

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

    public List<Long> getWinnerIds() { return winnerIds; }

    public void setWinnerIds(List<Long> winnerId) { this.winnerIds = winnerId; }

    public long getGameMinutes() {
        return gameMinutes;
    }

    public void setGameMinutes(long gameMinutes) {
        this.gameMinutes = gameMinutes;
    }

    public Boolean getGameTooShort() { return gameTooShort; }

    public void setGameTooShort(Boolean gameTooShort) { this.gameTooShort = gameTooShort; }
}
