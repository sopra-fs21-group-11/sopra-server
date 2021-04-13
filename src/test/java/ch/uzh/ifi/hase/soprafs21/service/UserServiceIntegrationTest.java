package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the UserResource REST resource.
 *
 * @see UserService
 */
@WebAppConfiguration
@SpringBootTest
public class UserServiceIntegrationTest {

    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
    }

    @Test
    public void GetUserByTokenAndIDAfterCreation() throws Exception {

        User testUser = new User();
        testUser.setPassword("testName");
        testUser.setUsername("testUsername");

        User createdUser = userService.createUser(testUser);

        //generated values present?
        assertNotNull(createdUser.getToken());
        assertNotNull(createdUser.getId());

        User searchedUser = userService.getUserByToken(createdUser.getToken());
        assertEquals(createdUser.getUsername(), searchedUser.getUsername());
        assertEquals(createdUser.getPassword(), searchedUser.getPassword());

        //same thing with id
        searchedUser = userService.getUser(createdUser.getId());
        assertEquals(createdUser.getUsername(), searchedUser.getUsername());
        assertEquals(createdUser.getPassword(), searchedUser.getPassword());
    }

    @Test
    public void UpdateUser() throws Exception {
        User testUser = new User();
        testUser.setPassword("testName");
        testUser.setUsername("testUsername");

        User createdUser = userService.createUser(testUser);

        User updateUser = new User();
        updateUser.setUsername("newUsername");
        updateUser.setPassword("newPassword");

        User updatedUser = userService.updateUser(createdUser.getId(), updateUser);

        assertEquals(updatedUser.getUsername(), updateUser.getUsername());
        assertEquals(updatedUser.getPassword(), updateUser.getPassword());
        assertNotEquals(testUser.getUsername(), updatedUser.getUsername());

        assertEquals(updatedUser.getId(), createdUser.getId());
    }

    @Test
    public void CheckAuthentication() throws Exception {
        User testUser = new User();
        testUser.setPassword("testName");
        testUser.setUsername("testUsername");

        User createdUser = userService.createUser(testUser);
        User notAllowedUser = new User();
        notAllowedUser.setPassword("qwe");
        notAllowedUser.setUsername("qwwe");

        Exception exception = assertThrows(Exception.class, () -> {
            userService.checkUserAuthentication(notAllowedUser);
        });

        //User authenticatedUser = userService.checkUserAuthentication(createdUser);
    }

}