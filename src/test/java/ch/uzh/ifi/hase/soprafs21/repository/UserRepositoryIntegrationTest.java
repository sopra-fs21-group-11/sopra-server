package ch.uzh.ifi.hase.soprafs21.repository;

import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void findById_success() {
        // given
        /*User user = new User();
        user.setUsername("firstname@lastname");
        user.setStatus(UserStatus.OFFLINE);
        user.setToken("1");
        user.setPassword("password");

        entityManager.persist(user);
        entityManager.flush();

        // when
        Optional<User> found = userRepository.findById(user.getId());
        assertTrue(found.isPresent());
        assertFalse(found.isEmpty());

        User foundUser = found.get();

        // then
        assertNotNull(foundUser.getId());
        assertEquals(foundUser.getUsername(), user.getUsername());
        assertEquals(foundUser.getPassword(), user.getPassword());
        assertEquals(foundUser.getToken(), user.getToken());
        assertEquals(foundUser.getStatus(), user.getStatus());

        //we test also otherwise (no user is found...)
        found = userRepository.findById(234L);
        assertFalse(found.isPresent());
        assertTrue(found.isEmpty());*/

    }
}
