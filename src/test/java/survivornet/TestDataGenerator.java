package survivornet;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import survivornet.services.db.CommentDbService;
import survivornet.services.db.PostDbService;
import survivornet.services.db.UserDbService;
import survivornet.utils.DataGenerator;

import java.sql.SQLIntegrityConstraintViolationException;

@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
public class TestDataGenerator {


    @Autowired
    private final CommentDbService commentDbService;

    @Autowired
    private final PostDbService postDbService;

    @Autowired
    private final UserDbService userDbService;

    @Autowired
    public TestDataGenerator(CommentDbService commentDbService, PostDbService postDbService, UserDbService userDbService) {
        this.commentDbService = commentDbService;
        this.postDbService = postDbService;
        this.userDbService = userDbService;
    }

    @BeforeAll
    void init() {
        DataGenerator.setPostService(postDbService);
        DataGenerator.setUserDbService(userDbService);
        DataGenerator.setCommentService(commentDbService);
    }

    @Test
    @Disabled
    void test() throws SQLIntegrityConstraintViolationException {
        DataGenerator.generateData(5, 2, 10);
    }
}
