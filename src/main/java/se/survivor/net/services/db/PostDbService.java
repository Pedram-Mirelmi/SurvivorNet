package se.survivor.net.services.db;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.Hibernate;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.stereotype.Service;
import se.survivor.net.DTO.PostDTO;
import se.survivor.net.exceptions.InvalidIdException;
import se.survivor.net.models.Post;
import se.survivor.net.models.PostReaction;
import se.survivor.net.models.User;

import java.time.LocalDateTime;
import java.util.List;

import static se.survivor.net.utils.Constants.POST_ID;
import static se.survivor.net.utils.Constants.USERNAME;

@Service
public class PostDbService {
    private final UserDbService userDbService;
    private final EntityManagerFactory entityManagerFactory;

    public PostDbService(UserDbService userDbService) {
        this.userDbService = userDbService;
        var registry = new StandardServiceRegistryBuilder().configure().build();
        entityManagerFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }


    public Post addPost(String username, String title, String caption, long parentId) {
        Post parent = null;
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        if(parentId != -1) {
            parent = getPostById(parentId, entityManager);
        }
        User user = userDbService.getUserByUsername(username, entityManager);
        Post post = new Post(user, title, caption, LocalDateTime.now(), parent);
        entityManager.persist(post);
        entityManager.getTransaction().commit();
        entityManager.close();
        return post;
    }

    public Post getPostById(long postId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        Post post = getPostById(postId, entityManager);
        entityManager.getTransaction().commit();
        entityManager.close();
        return post;
    }

    public Post getPostById(long postId, EntityManager entityManager) {
        Post post = entityManager.find(Post.class, postId);
        if(post == null) {
            if(entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            entityManager.close();
            throw new InvalidIdException("Invalid post Id");
        }
        return post;
    }

    public PostDTO getPostDTO(long postId) {
        Post post = getPostById(postId);
        return new PostDTO(post,
                getPostCommentCount(postId),
                getPostReactionCount(postId),
                post.getParent() == null ? -1 : post.getParent().getPostId());
    }


    public List<Post> getUserPosts(String username, int chunk) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        List<Post> resultList = entityManager.createQuery(
                "SELECT p FROM Post p " +
                        "WHERE p.user.username=:username", Post.class)
                .setParameter(USERNAME, username)
                .getResultList();

        resultList.forEach(Hibernate::initialize);

        entityManager.getTransaction().commit();
        entityManager.close();

        return resultList;
    }


    public List<Post> getUserHomePosts(String username, int chunk) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        List<Post> resultList = entityManager.createQuery(
                "SELECT p FROM Post p " +
                        "WHERE p.user IN " +
                        "       (SELECT u.followings from User u  " +
                        "       WHERE u.username=:username)" +
                        "ORDER BY p.createdAt DESC ", Post.class)
                .setParameter(USERNAME, username)
                .setFirstResult(chunk*10)
                .setMaxResults((chunk+1)*10)
                .getResultList();

        resultList.forEach(p -> Hibernate.initialize(p.getParent()));

        entityManager.getTransaction().commit();
        entityManager.close();

        return resultList;

    }

    public long getPostCommentCount(long postId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        long count = entityManager.createQuery(
                "SELECT COUNT(*) " +
                        "FROM Comment c " +
                        "WHERE c.post.postId = :postId", Long.class)
                .setParameter(POST_ID, postId)
                .getSingleResult();

        entityManager.getTransaction().commit();
        entityManager.close();

        return count;

    }

    public long getPostReactionCount(long postId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        long count = entityManager.createQuery(
                "SELECT COUNT(*) " +
                        "FROM PostReaction pr " +
                        "WHERE pr.post.postId = :postId", Long.class)
                .setParameter(POST_ID, postId)
                .getSingleResult();

        entityManager.getTransaction().commit();
        entityManager.close();

        return count;
    }

    public List<PostReaction> getPostReactions(long postId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        Post post = entityManager.find(Post.class, postId);
        var postReactions = post.getReactions();
        postReactions.forEach(pr -> Hibernate.initialize(pr.getUser()));
        entityManager.getTransaction().commit();
        entityManager.close();
        return postReactions;
    }

    public void addPostReaction(String username, long postId, int reactionType) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        Post post = entityManager.find(Post.class, postId);
        User user = userDbService.getUserByUsername(username, entityManager);
        Hibernate.initialize(post.getReactions());
        var resultList = entityManager.createQuery(
                "SELECT pr FROM PostReaction pr " +
                        " WHERE pr.user.username=:username AND pr.post.postId=:postId")
                .setParameter(USERNAME, username)
                .setParameter(POST_ID, postId)
                .getResultList();
        if(!resultList.isEmpty()) {
            entityManager.remove(resultList.get(0));
        }
        entityManager.persist(new PostReaction(user, reactionType, post));

        entityManager.getTransaction().commit();
        entityManager.close();
    }


}
