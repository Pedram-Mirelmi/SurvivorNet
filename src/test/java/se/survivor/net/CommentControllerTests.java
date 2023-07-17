package se.survivor.net;


import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import se.survivor.net.DTO.CommentDTO;
import se.survivor.net.exceptions.UnauthorizedException;
import se.survivor.net.models.Post;
import se.survivor.net.models.User;
import se.survivor.net.services.db.CommentDbService;
import se.survivor.net.services.db.PostDbService;
import se.survivor.net.services.db.UserDbService;
import se.survivor.net.services.domain.CommentService;

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

    private User pedramUser;
    private User minaUser;

    private Post pedramPost1;

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

        pedramPost1 = postDbService.addPost(pedramUser.getUsername(),
                "Pedram's first post",
                "Hi! I'm so excited!",
                -1);
    }

    @AfterAll
    void tearDown() {
        userDbService.removeUser("Pedram");
        userDbService.removeUser(minaUser.getUsername());
    }

    @Test
    @Order(1)
    void addAndGetComments() throws UnauthorizedException {
        comment1 = commentService.addComment(pedramUser.getUsername(),
                pedramPost1.getPostId(),
                "Comment on my own post!",
                -1L);
        List<CommentDTO> comments = commentService.getPostComments(pedramUser.getUsername(), pedramPost1.getPostId(), 0);
        assertEquals(1, comments.size());
        CommentDTO comment_ = comments.get(0);
        assertEquals(comment1.getPostId(), comment_.getPostId());
        assertEquals(comment1.getUser().getUsername(), comment_.getUser().getUsername());
        assertEquals(comment1.getText(), comment_.getText());
    }

    @Test
    @Order(1)
    void addAndGetSolutions() throws UnauthorizedException {
        solution1 = commentService.addSolution(pedramUser.getUsername(),
                pedramPost1.getPostId(),
                "Solution on my own post!");
        List<CommentDTO> solutions = commentService.getPostSolutions(pedramUser.getUsername(), pedramPost1.getPostId(), 0);
        assertEquals(1, solutions.size());
        CommentDTO solution_ = solutions.get(0);
        assertEquals(solution1.getPostId(), solution_.getPostId());
        assertEquals(solution1.getUser().getUsername(), solution_.getUser().getUsername());
        assertEquals(solution1.getText(), solution_.getText());
    }

    @Test
    @Order(2)
    void likeComment() {
        assertEquals(0, commentDbService.getCommentLikes(comment1.getCommentId()));
        commentService.likeComment(pedramUser.getUsername(), comment1.getCommentId(), true);
        assertEquals(1, commentDbService.getCommentLikes(comment1.getCommentId()));
    }
}
