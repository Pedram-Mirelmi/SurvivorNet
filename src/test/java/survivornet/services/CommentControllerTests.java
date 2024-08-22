package survivornet.services;


import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import survivornet.DTO.CommentDTO;
import survivornet.exceptions.UnauthorizedException;
import survivornet.models.Post;
import survivornet.models.User;
import survivornet.projections.CommentLikesProjection;
import survivornet.services.db.CommentDbService;
import survivornet.services.db.PostDbService;
import survivornet.services.db.UserDbService;
import survivornet.services.domain.CommentService;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
public class CommentControllerTests {

    @Autowired
    private final CommentDbService commentDbService;

    @Autowired
    private final PostDbService postDbService;

    @Autowired
    private final UserDbService userDbService;

    @Autowired
    private final CommentService commentService;

    private User integrationTestUser1;
    private User integrationTestUser2;

    private Post integrationUser1Post1;

    private CommentDTO comment1;

    private CommentDTO solution1;

    @Autowired
    public CommentControllerTests(CommentDbService commentDbService, UserDbService userDbService, CommentService commentService, PostDbService postDbService) {
        this.commentDbService = commentDbService;
        this.userDbService = userDbService;
        this.commentService = commentService;
        this.postDbService = postDbService;
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
        integrationTestUser2 = userDbService.addUser(
                "integrationTestUser2",
                "integrationTestUser2FName",
                "integrationTestUser2LName",
                "integrationTestUser2Pass",
                "integrationTestUser2Email@SurvivorNet.com",
                null,
                "This is integrationTestUser2");

        integrationUser1Post1 = postDbService.addPost(integrationTestUser1.getUsername(),
                "integrationTestUser1's first post' title",
                "integrationTestUser1's first post'",
                -1);
    }

    @AfterAll
    void tearDown() {
        userDbService.removeUser(integrationTestUser1.getUsername());
        userDbService.removeUser(integrationTestUser2.getUsername());
    }

    @Test
    @Order(1)
    void addAndGetComments() throws UnauthorizedException {
        comment1 = commentService.addComment(integrationTestUser1.getUsername(),
                integrationUser1Post1.getPostId(),
                "IntegrationTestComment1's text",
                -1L);
        List<CommentDTO> comments = commentService.getPostComments(integrationTestUser1.getUsername(), integrationUser1Post1.getPostId(), 0);
        assertEquals(1, comments.size());
        CommentDTO comment_ = comments.get(0);
        assertEquals(comment1.getPostId(), comment_.getPostId());
        assertEquals(comment1.getUser().getUsername(), comment_.getUser().getUsername());
        assertEquals(comment1.getText(), comment_.getText());
    }

    @Test
    @Order(1)
    void addAndGetSolutions() throws UnauthorizedException {
        solution1 = commentService.addSolution(integrationTestUser1.getUsername(),
                integrationUser1Post1.getPostId(),
                "IntegrationTestSolution1's text");
        List<CommentDTO> solutions = commentService.getPostSolutions(integrationTestUser1.getUsername(), integrationUser1Post1.getPostId(), 0);
        assertEquals(1, solutions.size());
        CommentDTO solution_ = solutions.get(0);
        assertEquals(solution1.getPostId(), solution_.getPostId());
        assertEquals(solution1.getUser().getUsername(), solution_.getUser().getUsername());
        assertEquals(solution1.getText(), solution_.getText());
    }

    @Test
    @Order(2)
    void likeComment() {
        CommentLikesProjection likes = commentDbService.getCommentLikesAndDislikes(comment1.getCommentId());
        assertEquals(0, likes.getLikes());
        assertEquals(0, likes.getDislikes());
        commentService.likeComment(integrationTestUser1.getUsername(), comment1.getCommentId(), true);
        commentService.likeComment(integrationTestUser1.getUsername(), comment1.getCommentId(), true);
        likes = commentDbService.getCommentLikesAndDislikes(comment1.getCommentId());
        assertEquals(1, likes.getLikes());
        assertEquals(0, likes.getDislikes());
        commentService.likeComment(integrationTestUser1.getUsername(), comment1.getCommentId(), false);
        likes = commentDbService.getCommentLikesAndDislikes(comment1.getCommentId());
        assertEquals(0, likes.getLikes());
        assertEquals(1, likes.getDislikes());
    }
}
