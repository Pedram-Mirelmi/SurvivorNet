package survivornet;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import survivornet.DTO.PostDTO;
import survivornet.DTO.UserDTO;
import survivornet.exceptions.UnauthorizedException;
import survivornet.models.User;
import survivornet.repositories.PictureRepository;
import survivornet.repositories.UserRepository;
import survivornet.services.db.PostDbService;
import survivornet.services.db.UserDbService;
import survivornet.services.domain.PostService;
import survivornet.services.domain.UserService;

import java.sql.SQLIntegrityConstraintViolationException;

@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
public class DevelopmentTest {

    private final UserDbService userDbService;

    private final UserService userService;

    private final PostService postService;

    private final PostDbService postDbService;

    private UserDTO integrationTestUser1;
    private UserDTO integrationTestUser2;

    private PostDTO integrationUser1Post1;
    private PostDTO integrationUser1Post2;

    private PostDTO integrationUser2Post1;

    @Autowired
    public DevelopmentTest(UserDbService userDbService, UserService userService, PostDbService postDbService, PostService postService) {
        this.userService = userService;
        this.userDbService = userDbService;
        this.postService = postService;
        this.postDbService = postDbService;
    }

    @BeforeAll
    void setUp() throws SQLIntegrityConstraintViolationException {
//        integrationTestUser1 = userDbService.addUser(
//                "integrationTestUser1",
//                "integrationTestUser1FName",
//                "integrationTestUser1LName",
//                "integrationTestUser1Pass",
//                "integrationTestUser1Email@SurvivorNet.com",
//                null,
//                "This is bio of integrationTestUser1");
//        integrationTestUser2 = userDbService.addUser(
//                "integrationTestUser2",
//                "integrationTestUser2FName",
//                "integrationTestUser1LName",
//                "integrationTestUser2Pass",
//                "integrationTestUser2Email@SurvivorNet.com",
//                null,
//                "This is bio of integrationTestUser2");
//
//        userDbService.changeFollow(
//                integrationTestUser1.getUsername(),
//                integrationTestUser2.getUsername(),
//                true);

    }

//    @Order(1)
//    @BeforeAll
    void assignVariables() throws UnauthorizedException {
//        this.integrationTestUser1 = userService.getUserDTOByUsername("integrationTestUser1", "integrationTestUser1");
//        this.integrationTestUser2 = userService.getUserDTOByUsername("integrationTestUser2", "integrationTestUser2");
    }


    @Order(2)
    @BeforeAll
    void addSomePosts() throws UnauthorizedException {
        assignVariables();
//        integrationUser1Post1 = postService.addPost(integrationTestUser1.getUsername(),
//                integrationTestUser1.getUsername() + "'s first post's title",
//                integrationTestUser1.getUsername() + "'s first post's caption",
//                -1);
//        integrationUser1Post2 = postService.addPost(integrationTestUser1.getUsername(),
//                integrationTestUser1.getUsername() + "'s second post's title",
//                integrationTestUser1.getUsername() + "'s second post's caption",
//                integrationUser1Post1.getPostId());
//        integrationUser2Post1 = postService.addPost(integrationTestUser2.getUsername(),
//                integrationTestUser2.getUsername() + "'s first post's title",
//                integrationTestUser2.getUsername() + "'s first post's caption",
//                -1);
    }

    @AfterAll
    void tearDown() {
//        userDbService.removeUser(integrationTestUser1.getUsername());
//        userDbService.removeUser(integrationTestUser2.getUsername());
    }


    @Disabled
    @Test
    void test() {
        var x = postDbService.getPostDTO(1);
        var a = 1;
    }


}
