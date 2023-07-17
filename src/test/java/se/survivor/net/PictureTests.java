package se.survivor.net;


import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import se.survivor.net.DTO.PostDTO;
import se.survivor.net.models.Picture;
import se.survivor.net.models.Post;
import se.survivor.net.models.User;
import se.survivor.net.services.db.PictureDbService;
import se.survivor.net.services.db.PostDbService;
import se.survivor.net.services.db.UserDbService;

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
    
    private User pedramUser;
    private Post pedramPost;

    @Autowired
    public PictureTests(UserDbService userService, PostDbService postDbService, PictureDbService pictureDbService) {
        this.userDbService = userService;
        this.postDbService = postDbService;
        this.pictureDbService = pictureDbService;
    }

    @BeforeAll
    void setUp() {
        pedramUser = userDbService.addUser("Pedram",
                "pedram",
                "123",
                "mirelmipedram@gmail.com",
                null,
                "This is Pedram");
        pedramPost = postDbService.addPost(pedramUser.getUsername(),
                "Pedram's first post",
                "Hi! I'm so excited!",
                -1);
    }

    @AfterAll
    void tearDown() {
        userDbService.removeUser(pedramUser.getUsername());
    }

    @Test
    @Order(1)
    void addProfilePicture() {
        Picture picture = pictureDbService.addPictureForProfile(pedramUser.getUsername());
        assertEquals(pedramUser.getUsername(), picture.getOwner().getUsername());
        User user = userDbService.getUserByUsername(pedramUser.getUsername());
        assertEquals(user.getProfilePic().getPictureId(), picture.getPictureId());
    }


    @Test
    @Order(1)
    void addBackgroundPicture() {
        Picture picture = pictureDbService.addBackgroundPictureForProfile(pedramUser.getUsername());
        assertEquals(pedramUser.getUsername(), picture.getOwner().getUsername());
        User user = userDbService.getUserByUsername(pedramUser.getUsername());
        assertEquals(user.getBackgroundPic().getPictureId(), picture.getPictureId());
    }


    @Test
    @Order(2)
    void addPostPicture() {
        Picture picture = pictureDbService.addPicturePost(pedramPost.getPostId());
        PostDTO post = postDbService.getPostDTO(pedramPost.getPostId());
        assertEquals(1, post.getPictures().size());
        assertEquals(post.getPictures().get(0), picture.getPictureId());
    }
}
