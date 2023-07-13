package se.survivor.net;

import org.junit.jupiter.api.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import se.survivor.net.DTO.UserDTO;
import se.survivor.net.models.User;
import se.survivor.net.services.DbService;
import se.survivor.net.services.UserService;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
class UserControllerTests {

    @Autowired
    UserService userService;
    @Autowired
    DbService dbService;

    @Autowired
    public UserControllerTests(UserService userService) {
        this.userService = userService;
    }

    @AfterAll
    void tearDown() throws ParseException {
        System.out.println("tearing down! =======================================================================================");
        dbService.removeUser(1);
        dbService.removeUser(2);
    }

    @BeforeAll
    void setUp() throws ParseException {
        dbService.addUser("Pedram",
                "pedram",
                "123",
                "mirelmipedram@gmail.com",
                null,
                "This is Pedram!");

        dbService.addUser("Mina",
                "mina",
                "123",
                "mina.ilkhani00@gmail.com",
                null,
                "This is Mina!");
    }

    @Test
    @Order(1)
    void testGetUserById() {
        UserDTO user = userService.getUserById(1L);
        assertEquals(1, user.getUserId());
        assertEquals("Pedram", user.getUsername());
        assertEquals("pedram", user.getName());
        assertTrue(user.getBio().isEmpty());
    }

    @Test
	@Order(1)
	void testGetUserByUsername() {
    	UserDTO user = userService.getUserDTOByUsername("Pedram");
        assertEquals(1, user.getUserId());
        assertEquals("Pedram", user.getUsername());
        assertEquals("pedram", user.getName());
        assertTrue(user.getBio().isEmpty());
	}

	@Test
    @Order(1)
    void testGetUsernameByEmail() {
        UserDTO user = userService.getUserDTOByEmail("mirelmipedram@gmail.com");
        assertEquals(1, user.getUserId());
        assertEquals("Pedram", user.getUsername());
        assertEquals("pedram", user.getName());
        assertTrue(user.getBio().isEmpty());
    }

    @Test
    @Order(1)
    void testAuthenticateWithUserPass() {
        assertTrue(dbService.authenticate("Pedram", "123"));
    }

    @Test
    @Order(2)
    void addFollowing() {
        userService.addFollow("Pedram", 2L);
    }

    @Test
    @Order(3)
    void testFollowingsAfterFollow() {
        var followings = userService.getUserFollowingsDTO(1L );
        assertEquals(1, followings.size());
        UserDTO user = followings.get(0);
        assertEquals("Mina", user.getUsername());
        assertEquals(2, user.getUserId());
    }

    @Test
    @Order(3)
    void testFollowersAfterFollow() {
        var followers = userService.getUserFollowersDTO(2L);
        assertEquals(1, followers.size());
        UserDTO user = followers.get(0);
        assertEquals("Pedram", user.getUsername());
        assertEquals(1, user.getUserId());
    }

    @Test
    @Order(4)
    void testRemoveFollow() {
        userService.removeFollow("Pedram", 2L);
    }



    @Test
    @Order(5)
    void testFollowingsAfterUnfollow() {
        var followings = userService.getUserFollowingsDTO(1L );
        assertEquals(0, followings.size());
    }

    @Test
    @Order(5)
    void testFollowersAfterUnfollow() {
        var followers = userService.getUserFollowersDTO(2L);
        assertEquals(0, followers.size());
    }
}
