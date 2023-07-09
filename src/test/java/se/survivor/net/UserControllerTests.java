package se.survivor.net;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import se.survivor.net.models.User;
import se.survivor.net.services.DbService;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class UserControllerTests {

    DbService db;

    @BeforeEach
    void setUp() throws ParseException {
        db = new DbService();
    }

    @Test
    void contextLoads() {
    }

    @Test
    @Order(1)
    void testAddUser1() {
        db.addUser("Pedram",
                "pedram",
                "123",
                "mirelmipedram@gmail.com",
                null,
                "This is Pedram!");
    }

    @Test
    @Order(1)
    void testAddUser2() {
        db.addUser("Mina",
                "mina",
                "123",
                "mina.ilkhani00@gmail.com",
                null,
                "This is Mina!");
    }

    @Test
    @Order(2)
    void testGetUserById() {
        User user = db.getUserById(1L);
        assertEquals(1, user.getUserId());
        assertEquals("Pedram", user.getUsername());
        assertEquals("pedram", user.getName());
        assertTrue(user.getBio().isEmpty());
    }

    @Test
	@Order(2)
	void testGetUserByUsername() {
    	User user = db.getUserByUsername("Pedram");
        assertEquals(1, user.getUserId());
        assertEquals("Pedram", user.getUsername());
        assertEquals("pedram", user.getName());
        assertTrue(user.getBio().isEmpty());
	}

	@Test
    @Order(2)
    void testGetUsernameByEmail() {
        User user = db.getUserByEmail("mirelmipedram@gmail.com");
        assertEquals(1, user.getUserId());
        assertEquals("Pedram", user.getUsername());
        assertEquals("pedram", user.getName());
        assertTrue(user.getBio().isEmpty());
    }

    @Test
    @Order(2)
    void testAuthenticateWithUserPass() {
        assertTrue(db.authenticate("Pedram", "123"));
    }

    @Test
    @Order(3)
    void addFollowing()
    {
        db.changeFollow(1, 2, true);
    }

}
