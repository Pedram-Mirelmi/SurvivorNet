package survivornet.services;


import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import survivornet.DTO.PostDTO;
import survivornet.models.Picture;
import survivornet.models.Post;
import survivornet.models.User;
import survivornet.services.db.PictureDbService;
import survivornet.services.db.PostDbService;
import survivornet.services.db.UserDbService;

import java.sql.SQLIntegrityConstraintViolationException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
public class PictureTests {


    @Autowired
    private final UserDbService userDbService;

    @Autowired
    private final PostDbService postDbService;

    @Autowired
    private final PictureDbService pictureDbService;
    
    private User integrationTestUser1;
    private Post integrationUser1Post1;

    @Autowired
    public PictureTests(UserDbService userService, PostDbService postDbService, PictureDbService pictureDbService) {
        this.userDbService = userService;
        this.postDbService = postDbService;
        this.pictureDbService = pictureDbService;
    }

    @BeforeAll
    void setUp() throws SQLIntegrityConstraintViolationException {
        integrationTestUser1 = userDbService.addUser(
                "integrationTestUser1",
                "integrationTestUser1FName",
                "integrationTestUser1LName",
                "integrationTestUser1Pass",
                "integrationTestUser1Email@SurvivorNet.com",
                null,
                "This is integrationTestUser1");
        integrationUser1Post1 = postDbService.addPost(integrationTestUser1.getUsername(),
                "integrationTestUser1's first post' title",
                "integrationTestUser1's first post'",
                -1);
    }

    @AfterAll
    void tearDown() {
        userDbService.removeUser(integrationTestUser1.getUsername());
    }

    @Test
    @Order(1)
    void addProfilePicture() throws SQLIntegrityConstraintViolationException {
        Picture picture = pictureDbService.addPictureForProfile(integrationTestUser1.getUsername());
        assertEquals(integrationTestUser1.getUsername(), picture.getOwner().getUsername());
        User user = userDbService.getUserByUsername(integrationTestUser1.getUsername());
        assertEquals(user.getProfilePic().getPictureId(), picture.getPictureId());
    }


    @Test
    @Order(1)
    void addBackgroundPicture() throws SQLIntegrityConstraintViolationException {
        Picture picture = pictureDbService.addBackgroundPictureForProfile(integrationTestUser1.getUsername());
        assertEquals(integrationTestUser1.getUsername(), picture.getOwner().getUsername());
        User user = userDbService.getUserByUsername(integrationTestUser1.getUsername());
        assertEquals(user.getBackgroundPic().getPictureId(), picture.getPictureId());
    }


    @Test
    @Order(2)
    void addPostPicture() {
        Picture picture = pictureDbService.addPicturePost(integrationUser1Post1.getPostId());
        PostDTO post = postDbService.getPostDTO(integrationUser1Post1.getPostId());
        assertEquals(1, post.getPictures().size());
        assertEquals(post.getPictures().get(0), picture.getPictureId());
    }
}
