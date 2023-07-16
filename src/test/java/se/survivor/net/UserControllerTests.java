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


    private User pedramUser;
    private User minaUser;

    @Autowired
    public UserControllerTests(UserService userService) {
        this.userService = userService;
    }

    @AfterAll
    void tearDown() throws ParseException {
        System.out.println("tearing down! =======================================================================================");
        dbService.removeUser(pedramUser.getUsername());
        dbService.removeUser(minaUser.getUsername());
    }

    @BeforeAll
    void setUp() throws ParseException {
        pedramUser = dbService.addUser("Pedram",
                "pedram",
                "123",
                "mirelmipedram@gmail.com",
                null,
                "This is Pedram!");

        minaUser = dbService.addUser("Mina",
                "mina",
                "123",
                "mina.ilkhani00@gmail.com",
                null,
                "This is Mina!");
    }

    @Test
	@Order(1)
	void testGetUserByUsername() {
    	UserDTO user = userService.getUserDTOByUsername(pedramUser.getUsername());
        assertEquals(pedramUser.getUsername(), user.getUsername());
        assertEquals(pedramUser.getName(), user.getName());
        assertTrue(user.getBio().isEmpty());
	}

	@Test
    @Order(1)
    void testGetUsernameByEmail() {
        UserDTO user = userService.getUserDTOByEmail("mirelmipedram@gmail.com");
        assertEquals(pedramUser.getUsername(), user.getUsername());
        assertEquals(pedramUser.getName(), user.getName());
        assertTrue(user.getBio().isEmpty());
    }

    @Test
    @Order(1)
    void testAuthenticateWithUserPass() {
        assertTrue(dbService.authenticate(pedramUser.getUsername(), pedramUser.getPassword()));
    }

    @Test
    @Order(2)
    void addFollowing() {
        userService.addFollow(pedramUser.getUsername(), minaUser.getUsername());
    }

    @Test
    @Order(3)
    void testFollowingsAfterFollow() {
        var followings = userService.getUserFollowingsDTO(pedramUser.getUsername() );
        assertEquals(1, followings.size());
        UserDTO user = followings.get(0);
        assertEquals(minaUser.getUsername(), user.getUsername());
    }

    @Test
    @Order(3)
    void testFollowersAfterFollow() {
        var followers = userService.getUserFollowersDTO(minaUser.getUsername());
        assertEquals(1, followers.size());
        UserDTO user = followers.get(0);
        assertEquals(pedramUser.getUsername(), user.getUsername());
    }

    @Test
    @Order(4)
    void testRemoveFollow() {
        userService.removeFollow(pedramUser.getUsername(), minaUser.getUsername());
    }



    @Test
    @Order(5)
    void testFollowingsAfterUnfollow() {
        var followings = userService.getUserFollowingsDTO(pedramUser.getUsername() );
        assertEquals(0, followings.size());
    }

    @Test
    @Order(5)
    void testFollowersAfterUnfollow() {
        var followers = userService.getUserFollowersDTO(minaUser.getUsername());
        assertEquals(0, followers.size());
    }
}
