package survivornet;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
public class DevelopmentTest {

//    @Autowired
//    private final EntityManagerFactory entityManagerFactory;
//
//
//    @Autowired
//    public DevelopmentTest(EntityManagerFactory entityManagerFactory) {
//        this.entityManagerFactory = entityManagerFactory;
//    }
//
//    @Test
//    @Disabled
//    void test() {
//        EntityManager entityManager = entityManagerFactory.createEntityManager();
//        var result = entityManager.createQuery(
//                "SELECT NEW survivornet.DTO.UserDTO(u, SIZE(u.followers), SIZE(u.followings)) " +
//                "FROM survivornet.models.User u " +
//                "WHERE u.userId=:userId", UserDTO.class)
//                .setParameter(USER_ID, 1)
//                .getSingleResult();
//        assertEquals(1, result.getUserId());
//    }
}
