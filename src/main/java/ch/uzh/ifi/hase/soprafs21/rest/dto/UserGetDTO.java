package ch.uzh.ifi.hase.soprafs21.rest.dto;

import ch.uzh.ifi.hase.soprafs21.constant.Boolean;

import java.util.Date;

public class UserGetDTO {

    private Long id;
    private String username;
    private Long totalWins;
    private Long totalDefeats;
    private Date creationDate;
    private Long totalTokens;
    private Long playTime;
    private boolean isPlaying;
    private boolean isOnline;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getTotalWins() {
        return totalWins;
    }

    public void setTotalWins(Long totalWins) {
        this.totalWins = totalWins;
    }

    public Long getTotalDefeats() {
        return totalDefeats;
    }

    public void setTotalDefeats(Long totalDefeats) {
        this.totalDefeats = totalDefeats;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Long getTotalTokens() {
        return totalTokens;
    }

    public void setTotalTokens(Long totalTokens) {
        this.totalTokens = totalTokens;
    }

    public Long getPlayTime() {
        return playTime;
    }

    public void setPlayTime(Long playTime) {
        this.playTime = playTime;
    }

    public boolean getIsPlaying() {
        return isPlaying;
    }

    public void setIsPlaying(boolean playing) {
        isPlaying = playing;
    }

    public boolean getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }
}
