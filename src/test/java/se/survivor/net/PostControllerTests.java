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
import se.survivor.net.exceptions.UnauthorizedException;
import se.survivor.net.models.User;
import se.survivor.net.services.db.UserDbService;
import se.survivor.net.services.domain.PostService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
public class PostControllerTests {

    @Autowired
    private final UserDbService userDbService;

    @Autowired
    private final PostService postService;
    
    private User pedramUser;
    private User minaUser;
    
    private PostDTO pedramPost1;
    private PostDTO pedramPost2;
    
    private PostDTO minaPost1;

    @Autowired
    public PostControllerTests(PostService postService, UserDbService userDbService) {
        this.postService = postService;
        this.userDbService = userDbService;
    }

    @BeforeAll
    void setUp() {
        pedramUser = userDbService.addUser("Pedram",
                "pedram",
                "123",
                "mirelmipedram@gmail.com",
                null,
                "This is Pedram");
        minaUser = userDbService.addUser("Mina",
                "mina",
                "123",
                "minaIlkhani00@gmail.com",
                null,
                "This is Mina");
        userDbService.changeFollow(pedramUser.getUsername(),
                minaUser.getUsername(),
                true);

    }

    @AfterAll
    void tearDown() {
        userDbService.removeUser(pedramUser.getUsername());
        userDbService.removeUser(minaUser.getUsername());
    }

    @Test
    @Order(1)
    void addSomePosts() {
        pedramPost1 = postService.addPost(pedramUser.getUsername(),
                "Pedram's first post",
                "Hi! I'm so excited!",
                -1);
        pedramPost2 = postService.addPost(pedramUser.getUsername(),
                "Pedram's second post",
                "Hi! I'm sooo excited",
                pedramPost1.getPostId());
        minaPost1 = postService.addPost(minaUser.getUsername(),
                "Mina's first post",
                "Hi! I'm too excited!",
                -1);
    }

    @Test
    @Order(2)
    void getHomePosts() throws InvalidValueException {
        List<PostDTO> pedramHomePosts = postService.getHomePosts(pedramUser.getUsername(), 0);
        List<PostDTO> minaHomePosts = postService.getHomePosts(minaUser.getUsername(), 0);
        assertEquals(1, pedramHomePosts.size());
        assertEquals(0, minaHomePosts.size());
        userDbService.changeFollow(minaUser.getUsername(), pedramUser.getUsername(), true);
        minaHomePosts = postService.getHomePosts(minaUser.getUsername(), 0);
        assertEquals(2, minaHomePosts.size());

        assertEquals("Mina's first post", pedramHomePosts.get(0).getTitle());

        // descending!
        assertEquals("Pedram's first post", minaHomePosts.get(1).getTitle());
        assertEquals("Pedram's second post", minaHomePosts.get(0).getTitle());
        assertEquals(pedramPost1.getPostId(), minaHomePosts.get(0).getParentId());


        userDbService.changeFollow(minaUser.getUsername(), pedramUser.getUsername(), false);

        minaHomePosts = postService.getHomePosts(minaUser.getUsername(), 0);
        assertEquals(0, minaHomePosts.size());
    }

    @Test
    @Order(2)
    void getPostDTO () throws UnauthorizedException {
        PostDTO postDTO = postService.getPostDTO(pedramUser.getUsername(), pedramPost2.getPostId());
        assertEquals("Pedram's second post", postDTO.getTitle());
    }

    @Test
    @Order(3)
    void addPost() throws InvalidValueException, UnauthorizedException {
        PostDTO postDTO = postService.addPost(minaUser.getUsername(),
                "Mina's second post",
                "Hiiii!",
                -1);
        var minaPosts = postService.getUserPosts(minaUser.getUsername(), minaUser.getUsername(), 0);
        assertEquals(2, minaPosts.size());
    }

    @Test
    @Order(4)
    void addReaction() throws UnauthorizedException {
        postService.addReaction(pedramUser.getUsername(), minaPost1.getPostId(), 2);
        postService.addReaction(pedramUser.getUsername(), minaPost1.getPostId(), 1);
    }

    @Test
    @Order(5)
    void getPostReactions() throws UnauthorizedException {
        List<PostReactionDTO> reactions = postService.getReactions(minaUser.getUsername(), minaPost1.getPostId());
        assertEquals(1, reactions.size());
        PostReactionDTO reaction = reactions.get(0);
        assertEquals(minaPost1.getPostId(), reaction.getPostId());
        assertEquals(1, reaction.getReactionType());
        UserDTO user = reaction.getUser();
        assertEquals(pedramUser.getUsername(), user.getUsername());
    }


}
