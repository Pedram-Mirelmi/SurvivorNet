package se.survivor.net;


import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import se.survivor.net.DTO.PostDTO;
import se.survivor.net.DTO.UserDTO;
import se.survivor.net.exceptions.UnauthorizedException;
import se.survivor.net.models.Picture;
import se.survivor.net.models.Post;
import se.survivor.net.models.User;
import se.survivor.net.services.DbService;
import se.survivor.net.services.PostService;
import se.survivor.net.services.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
public class PictureTests {

    @Autowired
    private final DbService dbService;

    @Autowired
    private final UserService userService;

    @Autowired
    private final PostService postService;
    
    private User pedramUser;
    private Post pedramPost;

    @Autowired
    public PictureTests(DbService dbService, UserService userService, PostService postService) {
        this.dbService = dbService;
        this.userService = userService;
        this.postService = postService;
    }

    @BeforeAll
    void setUp() {
        pedramUser = dbService.addUser("Pedram",
                "pedram",
                "123",
                "mirelmipedram@gmail.com",
                null,
                "This is Pedram");
        pedramPost = dbService.addPost(pedramUser.getUsername(),
                "Pedram's first post",
                "Hi! I'm so excited!",
                -1);
    }

    @AfterAll
    void tearDown() {
        dbService.removeUser(pedramUser.getUsername());
    }

    @Test
    @Order(1)
    void addProfilePicture() throws UnauthorizedException {
        Picture picture = userService.addProfilePicture(pedramUser.getUsername());
        assertEquals(pedramUser.getUsername(), picture.getOwner().getUsername());
        UserDTO user = userService.getUserDTOByUsername(pedramUser.getUsername(), pedramUser.getUsername());
        assertEquals(user.getProfilePicId(), picture.getPictureId());
    }


    @Test
    @Order(1)
    void addBackgroundPicture() throws UnauthorizedException {
        Picture picture = userService.addBackgroundProfilePicture(pedramUser.getUsername());
        assertEquals(pedramUser.getUsername(), picture.getOwner().getUsername());
        UserDTO user = userService.getUserDTOByUsername(pedramUser.getUsername(), pedramUser.getUsername());
        assertEquals(user.getBackgroundPicId(), picture.getPictureId());
    }


    @Test
    @Order(2)
    void addPostPicture() throws UnauthorizedException {
        Picture picture = postService.addPictureToPost(pedramUser.getUsername(), pedramPost.getPostId());
        PostDTO post = postService.getPostDTO(pedramUser.getUsername(), pedramPost.getPostId());
        assertEquals(1, post.getPictures().size());
        assertEquals(post.getPictures().get(0), picture.getPictureId());
    }
}
