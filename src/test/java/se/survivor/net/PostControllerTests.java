package se.survivor.net;


import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import se.survivor.net.DTO.PostDTO;
import se.survivor.net.DTO.PostReactionDTO;
import se.survivor.net.DTO.UserDTO;
import se.survivor.net.exceptions.InvalidValueException;
import se.survivor.net.services.DbService;
import se.survivor.net.services.PostService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
public class PostControllerTests {

    @Autowired
    private DbService dbService;

    @Autowired
    private PostService postService;

    @Autowired
    public PostControllerTests(PostService postService, DbService dbService) {
        this.postService = postService;
        this.dbService = dbService;
    }

    @BeforeAll
    void setUp() {
        dbService.addUser("Pedram",
                "pedram",
                "123",
                "mirelmipedram@gmail.com",
                null,
                "This is Pedram");
        dbService.addUser("Mina",
                "mina",
                "123",
                "minaIlkhani00@gmail.com",
                null,
                "This is Mina");
        dbService.changeFollow("Pedram",
                "Mina",
                true);
        dbService.addPost("Pedram",
                "Pedram's first post",
                "Hi! I'm so excited!",
                -1);
        dbService.addPost("Pedram",
                "Pedram's second post",
                "Hi! I'm sooo excited",
                1);
        dbService.addPost("Mina",
                "Mina's first post",
                "Hi! I'm too excited!",
                -1);
    }

    @AfterAll
    void tearDown() {
        dbService.removeUser("Pedram");
        dbService.removeUser("Mina");
    }

    @Test
    @Order(1)
    void getHomePosts() throws InvalidValueException {
        List<PostDTO> pedramHomePosts = postService.getHomePosts("Pedram", 0);
        List<PostDTO> minaHomePosts = postService.getHomePosts("Mina", 0);
        assertEquals(1, pedramHomePosts.size());
        assertEquals(0, minaHomePosts.size());
        dbService.changeFollow("Mina", "Pedram", true);
        minaHomePosts = postService.getHomePosts("Mina", 0);
        assertEquals(2, minaHomePosts.size());

        assertEquals("Mina's first post", pedramHomePosts.get(0).getTitle());

        // descending!
        assertEquals("Pedram's first post", minaHomePosts.get(1).getTitle());
        assertEquals("Pedram's second post", minaHomePosts.get(0).getTitle());
        assertEquals(1, minaHomePosts.get(0).getParentId());


        dbService.changeFollow("Mina", "Pedram", false);

        minaHomePosts = postService.getHomePosts("Mina", 0);
        assertEquals(0, minaHomePosts.size());
    }

    @Test
    @Order(1)
    void getPostDTO () {
        PostDTO postDTO = postService.getPostDTO(2);
        assertEquals("Pedram's second post", postDTO.getTitle());
    }

    @Test
    @Order(2)
    void addPost() throws InvalidValueException {
        PostDTO postDTO = postService.addPost("Mina",
                "Mina's second post",
                "Hiiii!",
                -1);
        var minaPosts = postService.getUserPosts("Mina", 0).size();
        assertEquals(2, postService.getUserPosts("Mina", 0).size());
    }

    @Test
    @Order(3)
    void addReaction()
    {
        postService.addReaction("Pedram", 3L, 2);
        postService.addReaction("Pedram", 3L, 1);

    }

    @Test
    @Order(4)
    void getPostReactions() {
        List<PostReactionDTO> reactions = postService.getReactions(3);
        assertEquals(1, reactions.size());
        PostReactionDTO reaction = reactions.get(0);
        assertEquals(3, reaction.getPostId());
        assertEquals(1, reaction.getReactionType());
        UserDTO user = reaction.getUser();
        assertEquals("Pedram", user.getUsername());
    }


}
