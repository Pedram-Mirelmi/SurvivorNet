package se.survivor.net;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import se.survivor.net.DTO.UserDTO;
import se.survivor.net.exceptions.UnauthorizedException;
import se.survivor.net.models.User;
import se.survivor.net.services.db.UserDbService;
import se.survivor.net.services.domain.UserService;

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
    UserDbService userDbService;


    private User integrationTestUser1;
    private User integrationTestUser2;

    @Autowired
    public UserControllerTests(UserService userService) {
        this.userService = userService;
    }

    @AfterAll
    void tearDown() throws ParseException {
        System.out.println("tearing down! =======================================================================================");
        userDbService.removeUser(integrationTestUser1.getUsername());
        userDbService.removeUser(integrationTestUser2.getUsername());
    }

    @BeforeAll
    void setUp() throws ParseException {
        integrationTestUser1 = userDbService.addUser("integrationTestUser1",
                "integrationTestUser1Name",
                "integrationTestUser1Pass",
                "integrationTestUser1Email@SurvivorNet.com",
                null,
                "This is bio of integrationTestUser1");
        integrationTestUser2 = userDbService.addUser("integrationTestUser2",
                "integrationTestUser2Name",
                "integrationTestUser2Pass",
                "integrationTestUser2Email@SurvivorNet.com",
                null,
                "This is bio of integrationTestUser2");
    }

    @Test
	@Order(1)
	void testGetUserByUsername() throws UnauthorizedException {
    	UserDTO user = userService.getUserDTOByUsername(integrationTestUser1.getUsername(), integrationTestUser1.getUsername());
        assertEquals(integrationTestUser1.getUsername(), user.getUsername());
        assertEquals(integrationTestUser1.getName(), user.getName());
        assertTrue(user.getBio().isEmpty());
	}

	@Test
    @Order(1)
    void testGetUsernameByEmail() throws UnauthorizedException {
        UserDTO user = userService.getUserDTOByEmail(integrationTestUser1.getEmail(), integrationTestUser1.getEmail());
        assertEquals(integrationTestUser1.getUsername(), user.getUsername());
        assertEquals(integrationTestUser1.getName(), user.getName());
        assertTrue(user.getBio().isEmpty());
    }

    @Test
    @Order(1)
    void testAuthenticateWithUserPass() {
        assertTrue(userDbService.authenticateByPassword(integrationTestUser1.getUsername(), integrationTestUser1.getPassword()));
    }

    @Test
    @Order(2)
    void addFollowing() {
        userService.addFollow(integrationTestUser1.getUsername(), integrationTestUser2.getUsername());
    }

    @Test
    @Order(3)
    void testFollowingsAfterFollow() throws UnauthorizedException {
        var followings = userService.getUserFollowingsDTO(integrationTestUser1.getUsername(), integrationTestUser1.getUsername() );
        assertEquals(1, followings.size());
        UserDTO user = followings.get(0);
        assertEquals(integrationTestUser2.getUsername(), user.getUsername());
    }

    @Test
    @Order(3)
    void testFollowersAfterFollow() throws UnauthorizedException {
        var followers = userService.getUserFollowersDTO(integrationTestUser2.getUsername(), integrationTestUser2.getUsername());
        assertEquals(1, followers.size());
        UserDTO user = followers.get(0);
        assertEquals(integrationTestUser1.getUsername(), user.getUsername());
    }

    @Test
    @Order(4)
    void testRemoveFollow() {
        userService.removeFollow(integrationTestUser1.getUsername(), integrationTestUser2.getUsername());
    }



    @Test
    @Order(5)
    void testFollowingsAfterUnfollow() throws UnauthorizedException {
        var followings = userService.getUserFollowingsDTO(integrationTestUser1.getUsername(), integrationTestUser1.getUsername() );
        assertEquals(0, followings.size());
    }

    @Test
    @Order(5)
    void testFollowersAfterUnfollow() throws UnauthorizedException {
        var followers = userService.getUserFollowersDTO(integrationTestUser2.getUsername(), integrationTestUser2.getUsername());
        assertEquals(0, followers.size());
    }
}
