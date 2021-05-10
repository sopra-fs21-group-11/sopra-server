package ch.uzh.ifi.hase.soprafs21.entity;

import ch.uzh.ifi.hase.soprafs21.rest.dto.UserGetDTO;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Internal User Representation
 * This class composes the internal representation of the user and defines how the user is stored in the database.
 * Every variable will be mapped into a database field with the @Column annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unique across the database -> composes the primary key
 */
@Entity
@Table(name = "USER")
public class User extends UserGetDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Long totalWins = 0L;

    @Column(nullable = false)
    private Long totalDefeats = 0L;

    @Column(nullable = false)
    private Date creationDate = new Date();

    @Column(nullable = false)
    private Long totalTokens = 0L;

    @Column(nullable = false)
    private Long playTime = 0L;

    @Column(nullable = false)
    private boolean isPlaying = false;

    @Column(nullable = false)
    private boolean isOnline = true;

    @Column int currentToken;

    public int getCurrentToken() {
        return currentToken;
    }

    public void setCurrentToken(int currentToken) {
        this.currentToken = currentToken;
    }

    @Override
    public Long getId() { return id; }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() { return token; }

    public void setToken(String token) { this.token = token; }

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

    public void setIsPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    public boolean getIsOnline() { return isOnline; }

    public void setIsOnline(boolean isOnline) { this.isOnline = isOnline; }
}
