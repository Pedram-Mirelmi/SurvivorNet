package survivornet.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.stereotype.Service;
import survivornet.models.Post;
import survivornet.models.User;
import survivornet.services.db.PostDbService;
import survivornet.services.db.UserDbService;

@Service
public class AuthorizationService {

    private final EntityManagerFactory entityManagerFactory;

    private final UserDbService userDbService;
    private final PostDbService postDbService;

    public AuthorizationService(UserDbService userDbService, PostDbService postDbService) {
        this.userDbService = userDbService;
        this.postDbService = postDbService;
        var registry = new StandardServiceRegistryBuilder().configure().build();
        entityManagerFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }

    private boolean canAccessProfile(String viewerUsername, String underViewUsername, EntityManager entityManager) {
        return entityManager.createQuery(
                "SELECT u.userId FROM User u " +
                        "WHERE u.username=:viewerUsername AND u IN " +
                        "(SELECT u1.blockList FROM User u1 " +
                        "WHERE u1.username=:underViewUsername)")
                .setParameter("viewerUsername", viewerUsername)
                .setParameter("underViewUsername", underViewUsername)
                .getResultList()
                .isEmpty();
    }


    public boolean canAccessProfile(String viewerUsername, String underViewUsername) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        boolean access = canAccessProfile(viewerUsername, underViewUsername, entityManager);

        entityManager.getTransaction().commit();
        entityManager.close();
        return access;
    }

    public boolean canFollow(String followerUsername, String followeeUsername) {
        // later we can add different profile privacy modes
        return canAccessProfile(followerUsername, followeeUsername);
    }

    public boolean canViewFollowList(String viewerUsername, String underViewUsername) {
        // later we can add different profile privacy modes
        return canAccessProfile(viewerUsername, underViewUsername);
    }

    public boolean canViewPost(String viewerUsername, long postId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        Post post = postDbService.getPostById(postId, entityManager);
        User targetUser = userDbService.getUserById(post.getUser().getUserId(), entityManager);
        boolean access = canAccessProfile(viewerUsername, targetUser.getUsername(), entityManager);
        entityManager.getTransaction().commit();
        entityManager.close();

        return access;
    }

    public boolean canAddReaction(String doerUser, long postId) {
        // for now it's the same
        return canViewPost(doerUser, postId);
    }

    public boolean canViewPostReactions(String viewerUsername, long postId) {
        // for now it's the same
        return canAddReaction(viewerUsername, postId);
    }

    public boolean canLeaveComment(String doerUsername, long postId) {
        // for now it's the same
        return canViewPost(doerUsername, postId);
    }

    public boolean canAddSolution(String doerUsername, long postId) {
        // for now it's the same
        return canViewPost(doerUsername, postId);
    }

    public boolean canViewPostComments(String viewerUsername, long postId) {
        return canViewPost(viewerUsername, postId);
    }

    public boolean canAddPictureToPost(String username, long postId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        boolean access = postDbService.getPostById(postId, entityManager)
                .getUser().getUsername().equals(username);

        entityManager.getTransaction().commit();
        entityManager.close();

        return access;
    }
}
