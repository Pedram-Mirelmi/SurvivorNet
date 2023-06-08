package se.survivor.net;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import se.survivor.net.models.User;
import se.survivor.net.services.DBService;
import se.survivor.net.services.IDb;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class UserControllerTests {

    IDb db;

    @BeforeEach
    void setUp() throws ParseException {
        db = new DBService();
    }

    @Test
    void contextLoads() {
    }

    @Test
    @Order(1)
    void testAddUser() {
        db.addUser("Pedram",
                "pedram",
                "123",
                "mirelmipedram@gmail.com",
                null,
                "This is Pedram!");
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


}
