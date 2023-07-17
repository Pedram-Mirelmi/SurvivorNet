package se.survivor.net.services.db;


import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.Hibernate;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.stereotype.Service;
import se.survivor.net.exceptions.InvalidIdException;
import se.survivor.net.models.Comment;
import se.survivor.net.models.CommentLike;
import se.survivor.net.models.Post;
import se.survivor.net.models.User;

import java.time.LocalDateTime;
import java.util.List;

import static se.survivor.net.utils.Constants.*;

@Service
public class CommentDbService {
    private final UserDbService userDbService;
    private final PostDbService postDbService;
    private final EntityManagerFactory entityManagerFactory;

    public CommentDbService(UserDbService userDbService, PostDbService postDbService) {
        this.userDbService = userDbService;
        this.postDbService = postDbService;
        var registry = new StandardServiceRegistryBuilder().configure().build();
        entityManagerFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }

    public Comment addComment(String username, long postId, String commentText, long parentId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        User user = userDbService.getUserByUsername(username, entityManager);
        Post post = postDbService.getPostById(postId, entityManager);
        Comment comment;
        Comment parent = null;
        if(parentId != -1) {
            parent = getCommentById(parentId, entityManager);
        }
        comment = new Comment(user, post, commentText, LocalDateTime.now(), parent, false);
        entityManager.persist(comment);
        entityManager.getTransaction().commit();
        entityManager.close();
        return comment;
    }

    public Comment addSolution(String username, long postId, String solutionText) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        User user = userDbService.getUserByUsername(username, entityManager);
        Post post = postDbService.getPostById(postId, entityManager);
        Comment comment = new Comment(user, post, solutionText, LocalDateTime.now(), null, true);
        entityManager.persist(comment);
        entityManager.getTransaction().commit();
        entityManager.close();
        return comment;
    }

    public Comment getCommentById(long commentId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        Comment comment = getCommentById(commentId, entityManager);

        entityManager.getTransaction().commit();
        entityManager.close();

        return comment;
    }

    private Comment getCommentById(long commentId, EntityManager entityManager) {
        Comment comment = entityManager.find(Comment.class, commentId);
        if(comment == null) {
            throw new InvalidIdException("Invalid comment id");
        }
        return comment;
    }

    public List<Comment> getPostComments(long postId, int chunk) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        var comments = entityManager.createQuery("SELECT c from Comment c " +
                "WHERE c.post.postId=:postId AND c.isSolution=FALSE", Comment.class)
                .setParameter(POST_ID, postId)
                .setFirstResult(chunk * 10)
                .setMaxResults((chunk+1) * 10)
                .getResultList();

        comments.forEach(c -> Hibernate.initialize(c.getUser()));

        entityManager.getTransaction().commit();
        entityManager.close();
        return comments;
    }


    public List<Comment> getPostSolutions(long postId, int chunk) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        var solutions = entityManager.createQuery("SELECT c from Comment c " +
                "WHERE c.post.postId=:postId AND c.isSolution=TRUE")
                .setParameter(POST_ID, postId)
                .setFirstResult(chunk * 10)
                .setMaxResults((chunk+1) * 10)
                .getResultList();

        entityManager.getTransaction().commit();
        entityManager.close();
        return solutions;
    }

    public void addCommentLike(String username, long commentId, boolean likes) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        Comment comment = getCommentById(commentId, entityManager);

        if(!hasUserLikedComment(username, commentId, entityManager)) {
            User user = userDbService.getUserByUsername(username, entityManager);
            CommentLike commentLike = new CommentLike(user, comment, likes);
            entityManager.persist(commentLike);
        }

        entityManager.getTransaction().commit();
        entityManager.close();
    }


    public long getCommentLikes(Long commentId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        Long likes = (Long) entityManager.createQuery("SELECT COUNT(*) " +
                "FROM CommentLike cl " +
                "WHERE cl.comment.commentId=:commentId AND cl.isLike=TRUE")
                .setParameter("commentId", commentId)
                .getSingleResult();

        entityManager.getTransaction().commit();
        entityManager.close();
        return likes;
    }

    public long getCommentDislikes(Long commentId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        Long dislikes = (Long) entityManager.createQuery("SELECT COUNT(*) " +
                "FROM CommentLike cl " +
                "WHERE cl.comment.commentId=:commentId AND cl.isLike=FALSE")
                .setParameter("commentId", commentId)
                .getSingleResult();

        entityManager.getTransaction().commit();
        entityManager.close();
        return dislikes;
    }

    private boolean hasUserLikedComment(String username, long commentId, EntityManager entityManager) {
        return !entityManager.createQuery(
                "SELECT cl FROM CommentLike cl " +
                        "WHERE cl.user.username=:username AND cl.comment.commentId=:commentId")
                .setParameter(USERNAME, username)
                .setParameter(COMMENT_ID, commentId)
                .getResultList()
                .isEmpty();
    }


}
