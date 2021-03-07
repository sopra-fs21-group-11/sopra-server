package ch.uzh.ifi.hase.soprafs21.rest.dto;

import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;


import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UserPutDTO {

    private String name;

    private String username;

    private String password;

    private UserStatus status;

    private LocalDate dateOfBirth;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = (status.equals("ONLINE")?UserStatus.ONLINE:UserStatus.OFFLINE);
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) throws ParseException {
        if(dateOfBirth!=null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-d");
            this.dateOfBirth = LocalDate.parse(dateOfBirth, formatter);
        }
    }
}

