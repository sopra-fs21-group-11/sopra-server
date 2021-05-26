package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back to the caller.
 */
@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    @Autowired
    public UserService(@Qualifier("userRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsers() {
        return this.userRepository.findAll();
    }

    public User getUser(Long userid) {
        Optional<User> user =this.userRepository.findById(userid);

        String baseErrorMessage="user with %s was not found ";
        if (user.isEmpty() ) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(baseErrorMessage,userid.toString()));
        }
        return user.get() ;
    }

    public User logoutUser(Long userid) {
        Optional<User> user =this.userRepository.findById(userid);
        if (user.isPresent() ) {
            User  userUpdate= user.get();
            userUpdate.setIsOnline(false);
            userUpdate = userRepository.save(userUpdate);
            return userUpdate;
        }
        String baseErrorMessage="user with %s was not found ";
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(baseErrorMessage,userid.toString()));
    }

    public User createUser(User newUser) {
        newUser.setToken(getJWTToken(newUser.getUsername()));
        newUser.setIsOnline(true);
        checkIfUserExists(newUser);

        // saves the given entity but data is only persisted in the database once flush() is called
        newUser = userRepository.save(newUser);
        userRepository.flush();

        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    public User getUserByToken(String token){
        //Could be refactored into user repo. findByToken(). This has not been tested and I had some troubles getting this correct.
        token = token.replace("Bearer ", "");
        List<User> users = this.getUsers();
        for(User user : users){
            if(user.getToken().equals(token)){
                return user;
            }
        }
        return null;
    }

    private String getJWTToken(String username) {
        String secretKey = "mySecretKey";
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils
                .commaSeparatedStringToAuthorityList("ROLE_USER");

        String token = Jwts
                .builder()
                .setId("soprafs21")
                .setSubject(username)
                .claim("authorities",
                        grantedAuthorities.stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 600000000))
                .signWith(SignatureAlgorithm.HS512,
                        secretKey.getBytes()).compact();
        return token;
    }

    public User updateUser(Long userid,User requestUser) {
        Optional<User> user =this.userRepository.findById(userid);

        String baseErrorMessage="user with %s was not found ";
        if (user.isEmpty() ) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(baseErrorMessage,userid.toString()));
        }
        User updateuser=user.get();

        if(requestUser.getUsername()!=null)
            updateuser.setUsername(requestUser.getUsername());

        if(requestUser.getPassword()!=null)
            updateuser.setPassword(requestUser.getPassword());

        // saves the given entity but data is only persisted in the database once flush() is called
        updateuser = userRepository.save(updateuser);

        userRepository.flush();

        log.debug("Updated Information for User: {}", updateuser);
        return updateuser;
    }

    public void saveWins(long userId, long winsToAdd){
        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty()){
            return;
        }
        User updateUser = user.get();
        updateUser.setTotalWins(updateUser.getTotalWins()+winsToAdd);
        userRepository.save(updateUser);
        userRepository.flush();
    }

    public void saveDefeats(long userId, long defeatsToAdd){
        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty()){
            return;
        }
        User updateUser = user.get();
        updateUser.setTotalDefeats(updateUser.getTotalDefeats()+defeatsToAdd);
        userRepository.save(updateUser);
        userRepository.flush();
    }

    public void saveGameTime(long userId, long minutesToAdd){
        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty()){
            return;
        }
        User updateUser = user.get();
        updateUser.setPlayTime(updateUser.getPlayTime()+minutesToAdd);
        userRepository.save(updateUser);
        userRepository.flush();
    }

    public void saveEarnedTokens(long userId, int tokensToAdd){
        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty()){
            return;
        }
        User updateUser = user.get();
        updateUser.setTotalTokens(updateUser.getTotalTokens()+tokensToAdd);
        userRepository.save(updateUser);
        userRepository.flush();
    }

    /**
     * This is a helper method that will check the uniqueness criteria of the username and the name
     * defined in the User entity. The method will do nothing if the input is unique and throw an error otherwise.
     *
     * @param userToBeCreated
     * @throws org.springframework.web.server.ResponseStatusException
     * @see User
     */
    private void checkIfUserExists(User userToBeCreated) {
        User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());

        String baseErrorMessage = "added user  failed because %s already exists";
        if (userByUsername != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(baseErrorMessage,userToBeCreated.getUsername()));
        }
    }

    public User checkUserAuthentication(User userToBeCreated) {
        User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());

        String baseErrorMessage = "Invalid username/ password";
        if (userByUsername != null ) {
            if(userByUsername.getPassword().equals(userToBeCreated.getPassword())) {
                userByUsername.setIsOnline(true);
                userByUsername.setToken(getJWTToken(userByUsername.getUsername()));
                userByUsername = userRepository.save(userByUsername);
                return userByUsername;
            }
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, String.format(baseErrorMessage));
    }
}
