package survivornet.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import survivornet.DTO.UserDTO;
import survivornet.exceptions.UnauthorizedException;
import survivornet.models.User;
import survivornet.services.db.UserDbService;
import survivornet.services.domain.UserService;

import java.sql.SQLIntegrityConstraintViolationException;
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
    void setUp() throws ParseException, SQLIntegrityConstraintViolationException {

        integrationTestUser1 = userDbService.addUser(
                "integrationTestUser1",
                "integrationTestUser1FName",
                "integrationTestUser1LName",
                "integrationTestUser1Pass",
                "integrationTestUser1Email@SurvivorNet.com",
                null,
                "This is bio of integrationTestUser1");
        integrationTestUser2 = userDbService.addUser(
                "integrationTestUser2",
                "integrationTestUser2FName",
                "integrationTestUser2LName",
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
        assertEquals(integrationTestUser1.getFirstname(), user.getFirstname());
        assertEquals(integrationTestUser1.getLastname(), user.getLastname());
    }

	@Test
    @Order(1)
    void testGetUsernameByEmail() throws UnauthorizedException {
        UserDTO user = userService.getUserDTOByEmail(integrationTestUser1.getEmail(), integrationTestUser1.getEmail());
        assertEquals(integrationTestUser1.getUsername(), user.getUsername());
        assertEquals(integrationTestUser1.getFirstname(), user.getFirstname());
        assertEquals(integrationTestUser1.getLastname(), user.getLastname());
    }

    @Test
    @Order(1)
    void testAuthenticateWithUserPass() {
        assertTrue(userDbService.authenticateByPassword(integrationTestUser1.getUsername(), integrationTestUser1.getPassword()));
    }

    @Test
    @Order(2)
    void addFollowing() throws UnauthorizedException {
        userService.changeFollow(integrationTestUser1.getUsername(), integrationTestUser2.getUsername(), true);
        assertTrue(userService.getFollowStatus(integrationTestUser1.getUsername(), integrationTestUser1.getUsername(), integrationTestUser2.getUsername()));
    }

    @Test
    @Order(3)
    void testFollowingsAfterFollow() throws UnauthorizedException {
        var followings = userService.getUserFollowingsDTO(integrationTestUser1.getUsername(), integrationTestUser1.getUsername(), 0);
        assertEquals(1, followings.size());
        UserDTO user = followings.get(0);
        assertEquals(integrationTestUser2.getUsername(), user.getUsername());
    }

    @Test
    @Order(3)
    void testFollowersAfterFollow() throws UnauthorizedException {
        var followers = userService.getUserFollowersDTO(integrationTestUser2.getUsername(), integrationTestUser2.getUsername(), 0);
        assertEquals(1, followers.size());
        UserDTO user = followers.get(0);
        assertEquals(integrationTestUser1.getUsername(), user.getUsername());
    }

    @Test
    @Order(4)
    void testRemoveFollow() {
        userService.changeFollow(integrationTestUser1.getUsername(), integrationTestUser2.getUsername(), false);
    }



    @Test
    @Order(5)
    void testFollowingsAfterUnfollow() throws UnauthorizedException {
        var followings = userService.getUserFollowingsDTO(integrationTestUser1.getUsername(), integrationTestUser1.getUsername(), 0);
        assertEquals(0, followings.size());
    }

    @Test
    @Order(5)
    void testFollowersAfterUnfollow() throws UnauthorizedException {
        var followers = userService.getUserFollowersDTO(integrationTestUser2.getUsername(), integrationTestUser2.getUsername(), 0);
        assertEquals(0, followers.size());
    }
}
