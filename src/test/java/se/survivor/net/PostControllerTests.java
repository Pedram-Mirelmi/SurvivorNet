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
import se.survivor.net.models.Post;
import se.survivor.net.models.User;
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
    private final DbService dbService;

    @Autowired
    private final PostService postService;
    
    private User pedramUser;
    private User minaUser;
    
    private Post pedramPost1;
    private Post pedramPost2;
    
    private Post minaPost1;

    @Autowired
    public PostControllerTests(PostService postService, DbService dbService) {
        this.postService = postService;
        this.dbService = dbService;
    }

    @BeforeAll
    @Order(0)
    void setUp() {
        pedramUser = dbService.addUser("Pedram",
                "pedram",
                "123",
                "mirelmipedram@gmail.com",
                null,
                "This is Pedram");
        minaUser = dbService.addUser("Mina",
                "mina",
                "123",
                "minaIlkhani00@gmail.com",
                null,
                "This is Mina");
        dbService.changeFollow(pedramUser.getUsername(),
                minaUser.getUsername(),
                true);
        pedramPost1 = dbService.addPost(pedramUser.getUsername(),
                "Pedram's first post",
                "Hi! I'm so excited!",
                -1);
        pedramPost2 = dbService.addPost(pedramUser.getUsername(),
                "Pedram's second post",
                "Hi! I'm sooo excited",
                pedramPost1.getPostId());
        minaPost1 = dbService.addPost(minaUser.getUsername(),
                "Mina's first post",
                "Hi! I'm too excited!",
                -1);
    }

    @AfterAll
    @Order(5)
    void tearDown() {
        dbService.removeUser(pedramUser.getUsername());
        dbService.removeUser(minaUser.getUsername());
    }

    @Test
    @Order(1)
    void getHomePosts() throws InvalidValueException {
        List<PostDTO> pedramHomePosts = postService.getHomePosts(pedramUser.getUsername(), 0);
        List<PostDTO> minaHomePosts = postService.getHomePosts(minaUser.getUsername(), 0);
        assertEquals(1, pedramHomePosts.size());
        assertEquals(0, minaHomePosts.size());
        dbService.changeFollow(minaUser.getUsername(), pedramUser.getUsername(), true);
        minaHomePosts = postService.getHomePosts(minaUser.getUsername(), 0);
        assertEquals(2, minaHomePosts.size());

        assertEquals("Mina's first post", pedramHomePosts.get(0).getTitle());

        // descending!
        assertEquals("Pedram's first post", minaHomePosts.get(1).getTitle());
        assertEquals("Pedram's second post", minaHomePosts.get(0).getTitle());
        assertEquals(pedramPost1.getPostId(), minaHomePosts.get(0).getParentId());


        dbService.changeFollow(minaUser.getUsername(), pedramUser.getUsername(), false);

        minaHomePosts = postService.getHomePosts(minaUser.getUsername(), 0);
        assertEquals(0, minaHomePosts.size());
    }

    @Test
    @Order(1)
    void getPostDTO () {
        PostDTO postDTO = postService.getPostDTO(pedramPost2.getPostId());
        assertEquals("Pedram's second post", postDTO.getTitle());
    }

    @Test
    @Order(2)
    void addPost() throws InvalidValueException {
        PostDTO postDTO = postService.addPost(minaUser.getUsername(),
                "Mina's second post",
                "Hiiii!",
                -1);
        var minaPosts = postService.getUserPosts(minaUser.getUsername(), 0);
        assertEquals(2, minaPosts.size());
    }

    @Test
    @Order(3)
    void addReaction()
    {
        postService.addReaction(pedramUser.getUsername(), minaPost1.getPostId(), 2);
        postService.addReaction(pedramUser.getUsername(), minaPost1.getPostId(), 1);
    }

    @Test
    @Order(4)
    void getPostReactions() {
        List<PostReactionDTO> reactions = postService.getReactions(minaPost1.getPostId());
        assertEquals(1, reactions.size());
        PostReactionDTO reaction = reactions.get(0);
        assertEquals(minaPost1.getPostId(), reaction.getPostId());
        assertEquals(1, reaction.getReactionType());
        UserDTO user = reaction.getUser();
        assertEquals(pedramUser.getUsername(), user.getUsername());
    }


}