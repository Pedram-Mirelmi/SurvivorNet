package se.survivor.net.services.db;


import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.stereotype.Service;
import se.survivor.net.models.Picture;
import se.survivor.net.models.Post;
import se.survivor.net.models.User;

@Service
public class PictureDbService {
    private final UserDbService userDbService;
    private final PostDbService postDbService;
    private final EntityManagerFactory entityManagerFactory;


    public PictureDbService(UserDbService userDbService, PostDbService postDbService) {
        this.userDbService = userDbService;
        this.postDbService = postDbService;
        var registry = new StandardServiceRegistryBuilder().configure().build();
        entityManagerFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }

    public Picture addPictureForProfile(String username) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        User user = userDbService.getUserByUsername(username, entityManager);
        if(user.getProfilePic() != null) {
            entityManager.remove(user.getProfilePic());
        }

        Picture picture = new Picture(user, null);
        user.setProfilePic(picture);

        entityManager.persist(picture);

        entityManager.getTransaction().commit();
        entityManager.close();

        return picture;
    }

    public Picture addBackgroundPictureForProfile(String username) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        User user = userDbService.getUserByUsername(username, entityManager);
        if(user.getBackgroundPic() != null) {
            entityManager.remove(user.getBackgroundPic());
        }

        Picture picture = new Picture(user, null);
        user.setBackgroundPic(picture);

        entityManager.persist(picture);

        entityManager.getTransaction().commit();
        entityManager.close();

        return picture;
    }

    public Picture addPicturePost(long postId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        Post post = postDbService.getPostById(postId, entityManager);
        User user = post.getUser();
        Picture picture = new Picture(user, post);
        post.getPictures().add(picture);
        entityManager.persist(picture);

        entityManager.getTransaction().commit();
        entityManager.close();
        return picture;
    }



}
