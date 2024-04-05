package survivornet.services;


import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import survivornet.DTO.PostDTO;
import survivornet.DTO.PostReactionDTO;
import survivornet.DTO.UserDTO;
import survivornet.exceptions.InvalidValueException;
import survivornet.exceptions.UnauthorizedException;
import survivornet.models.User;
import survivornet.services.db.UserDbService;
import survivornet.services.domain.PostService;

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
    
    private User integrationTestUser1;
    private User integrationTestUser2;
    
    private PostDTO integrationUser1Post1;
    private PostDTO integrationUser1Post2;
    
    private PostDTO integrationUser2Post1;

    @Autowired
    public PostControllerTests(PostService postService, UserDbService userDbService) {
        this.postService = postService;
        this.userDbService = userDbService;
    }

    @BeforeAll
    void setUp() {
        integrationTestUser1 = userDbService.addUser(
                "integrationTestUser1",
                "integrationTestUser1Name",
                "integrationTestUser1Pass",
                "integrationTestUser1Email@SurvivorNet.com",
                null,
                "This is bio of integrationTestUser1");
        integrationTestUser2 = userDbService.addUser(
                "integrationTestUser2",
                "integrationTestUser2Name",
                "integrationTestUser2Pass",
                "integrationTestUser2Email@SurvivorNet.com",
                null,
                "This is bio of integrationTestUser2");
        
        userDbService.changeFollow(
                integrationTestUser1.getUsername(),
                integrationTestUser2.getUsername(),
                true);

    }

    @AfterAll
    void tearDown() {
        userDbService.removeUser(integrationTestUser1.getUsername());
        userDbService.removeUser(integrationTestUser2.getUsername());
    }

    @Test
    @Order(1)
    void addSomePosts() {
        integrationUser1Post1 = postService.addPost(integrationTestUser1.getUsername(),
                integrationTestUser1.getUsername() + "'s first post's title",
                integrationTestUser1.getUsername() + "'s first post's caption",
                -1);
        integrationUser1Post2 = postService.addPost(integrationTestUser1.getUsername(),
                integrationTestUser1.getUsername() + "'s second post's title",
                integrationTestUser1.getUsername() + "'s second post's caption",
                integrationUser1Post1.getPostId());
        integrationUser2Post1 = postService.addPost(integrationTestUser2.getUsername(),
                integrationTestUser2.getUsername() + "'s first post's title",
                integrationTestUser2.getUsername() + "'s first post's caption",
                -1);
    }

    @Test
    @Order(2)
    void getHomePosts() throws InvalidValueException {
        List<PostDTO> pedramHomePosts = postService.getHomePosts(integrationTestUser1.getUsername(), 0);
        List<PostDTO> minaHomePosts = postService.getHomePosts(integrationTestUser2.getUsername(), 0);
        assertEquals(1, pedramHomePosts.size());
        assertEquals(0, minaHomePosts.size());
        userDbService.changeFollow(integrationTestUser2.getUsername(), integrationTestUser1.getUsername(), true);
        minaHomePosts = postService.getHomePosts(integrationTestUser2.getUsername(), 0);
        assertEquals(2, minaHomePosts.size());

        assertEquals(integrationUser2Post1.getTitle(), pedramHomePosts.get(0).getTitle());

        // descending!
        assertEquals(integrationUser1Post1.getTitle(), minaHomePosts.get(1).getTitle());
        assertEquals(integrationUser1Post2.getTitle(), minaHomePosts.get(0).getTitle());
        assertEquals(integrationUser1Post1.getPostId(), minaHomePosts.get(0).getParentId());


        userDbService.changeFollow(integrationTestUser2.getUsername(), integrationTestUser1.getUsername(), false);

        minaHomePosts = postService.getHomePosts(integrationTestUser2.getUsername(), 0);
        assertEquals(0, minaHomePosts.size());
    }

    @Test
    @Order(2)
    void getPostDTO () throws UnauthorizedException {
        PostDTO postDTO = postService.getPostDTO(integrationTestUser1.getUsername(), integrationUser1Post2.getPostId());
        assertEquals(integrationUser1Post2.getTitle(), postDTO.getTitle());
    }

    @Test
    @Order(3)
    void addPost() throws InvalidValueException, UnauthorizedException {
        PostDTO post = postService.addPost(integrationTestUser2.getUsername(),
                integrationTestUser2.getUsername() + "'s second post's title",
                integrationTestUser2.getUsername() + "'s second post's caption",
                -1);
        var minaPosts = postService.getUserPosts(integrationTestUser2.getUsername(), integrationTestUser2.getUsername(), 0);
        assertEquals(2, minaPosts.size());
        assertEquals(post.getTitle(), minaPosts.get(1).getTitle());
    }

    @Test
    @Order(4)
    void addReaction() throws UnauthorizedException {
        postService.addReaction(integrationTestUser1.getUsername(), integrationUser2Post1.getPostId(), 2);
        postService.addReaction(integrationTestUser1.getUsername(), integrationUser2Post1.getPostId(), 1);
    }

    @Test
    @Order(5)
    void getPostReactions() throws UnauthorizedException {
        List<PostReactionDTO> reactions = postService.getReactions(integrationTestUser2.getUsername(), integrationUser2Post1.getPostId(), 0);
        assertEquals(1, reactions.size());
        PostReactionDTO reaction = reactions.get(0);
        assertEquals(integrationUser2Post1.getPostId(), reaction.getPostId());
        assertEquals(1, reaction.getReactionType());
        UserDTO user = reaction.getUser();
        assertEquals(integrationTestUser1.getUsername(), user.getUsername());
    }


}
