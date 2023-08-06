package survivornet;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import survivornet.repositories.PictureRepository;
import survivornet.repositories.UserRepository;
import survivornet.services.db.UserDbService;
import survivornet.services.domain.UserService;

@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
public class DevelopmentTest {

    @Autowired
    private final UserDbService userDbService;

    @Autowired
    private final UserService userService;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final PictureRepository pictureRepository;

    @Autowired
    public DevelopmentTest(UserDbService userDbService, UserService userService, UserRepository userRepository, PictureRepository pictureRepository) {
        this.userService = userService;
        this.userDbService = userDbService;
        this.userRepository = userRepository;
        this.pictureRepository = pictureRepository;
    }

    @Test
    void test() {
        userService.changeFollow("integrationTestUser1", "integrationTestUser2", true);
    }


}
