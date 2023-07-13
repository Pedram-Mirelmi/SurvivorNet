package se.survivor.net.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.Hibernate;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import se.survivor.net.DTO.PostDTO;
import se.survivor.net.exceptions.InvalidIdException;
import se.survivor.net.models.*;

import java.sql.Date;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;

@Service
public class DbService {


    private final EntityManagerFactory entityManagerFactory;


    public DbService() throws ParseException {
        var registry = new StandardServiceRegistryBuilder().configure().build();
        entityManagerFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }

    public void addUser(@NotNull String username,
                        @NotNull String name,
                        @NotNull String password,
                        @NotNull String email,
                        Date birthDate,
                        @NotNull String bio) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        User user = new User(username, password, name, email, birthDate, Date.valueOf(LocalDate.now()), "", null, null);
        entityManager.persist(user);
        entityManager.getTransaction().commit();
        entityManager.close();
    }


    public User getUserById(Long userId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        User user = getUserById(userId, entityManager);
        entityManager.getTransaction().commit();
        entityManager.close();
        return user;
    }

    private User getUserById(Long userId, EntityManager entityManager) {
        User user = entityManager.find(User.class, userId);
        if(user == null) {
            entityManager.getTransaction().rollback();
            entityManager.close();
            throw new InvalidIdException("Invalid user Id");
        }
        return user;
    }



    public User getUserByUsername(String username) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        User user = getUserByUsername(username, entityManager);
        entityManager.getTransaction().commit();
        entityManager.close();
        return user;
    }

    public User getUserByUsername(String username, EntityManager entityManager) {
        var resultList = entityManager.createQuery("SELECT u FROM User u WHERE u.username=:username")
                .setParameter("username", username)
                .getResultList();
        if(resultList.isEmpty()) {
            entityManager.getTransaction().rollback();
            entityManager.close();
            throw new InvalidIdException("Invalid Username");
        }
        return (User) resultList.get(0);
    }

    public User getUserByEmail(String email) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        User user = getUserByEmail(email, entityManager);
        entityManager.getTransaction().commit();
        entityManager.close();
        return user;
    }

    public User getUserByEmail(String email, EntityManager entityManager) {
        var resultList = entityManager.createQuery("SELECT u FROM User u WHERE u.email=:email")
                .setParameter("email", email)
                .getResultList();
        if(resultList.isEmpty()) {
            throw new InvalidIdException("Invalid email");
        }
        return (User) resultList.get(0);
    }

    public boolean authenticate(String username, String password) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        User user = getUserByUsername(username, entityManager);
        boolean success = user.getPassword().equals(password);
        entityManager.getTransaction().commit();
        entityManager.close();
        return success;
    }
    public List<User> getFollowers(Long userId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        User user = getUserById(userId, entityManager);
        Hibernate.initialize(user.getFollowers());
        List<User> followers = user.getFollowers();
        entityManager.getTransaction().commit();
        entityManager.close();
        return followers;
    }

    public List<User> getFollowings(Long userId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        User user = getUserById(userId, entityManager);
        Hibernate.initialize(user.getFollowings());
        List<User> followings = user.getFollowings();
        entityManager.getTransaction().commit();
        entityManager.close();
        return followings;
    }

    public List<User> searchUsers(String query) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        var resultList = entityManager.createQuery(
                "SELECT u FROM User u " +
                        "WHERE u.username LIKE '%:query%' " +
                        "OR u.name LIKE '%:query%' " +
                        "OR u.email LIKE '%:query%'")
                .setParameter("query", query)
                .getResultList();
        entityManager.getTransaction().commit();
        entityManager.close();
        return resultList;
    }

    public void changeFollow(long followerId, long followeeId, boolean follow) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        User follower = entityManager.find(User.class, followerId);
        User followee = entityManager.find(User.class, followeeId);

        try {
            // one way is enough to store both ways!
            if(follow) {
                follower.getFollowings().add(followee);
            }
            else {
                follower.getFollowings().remove(followee);
            }
            entityManager.getTransaction().commit();
            entityManager.close();
        }
        catch (Exception e) {
            entityManager.getTransaction().rollback();
            entityManager.close();
        }
    }

    public void changeBlock(long blockerId, Long blockeeId, boolean block) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        User blocker = entityManager.find(User.class, blockerId);
        User blockee = entityManager.find(User.class, blockeeId);

        try {
            // one way is enough to store both ways!
            if(block) {
                blocker.getBlockeeList().add(blockee);
            }
            else {
                blocker.getBlockeeList().remove(blockee);
            }
            entityManager.getTransaction().commit();
            entityManager.close();
        }
        catch (Exception e) {
            entityManager.getTransaction().rollback();
            entityManager.close();
            throw e;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void addPicture(long userId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        User user = entityManager.find(User.class, userId);
        if(user == null) {
            throw new InvalidIdException("Invalid user id");
        }

        Picture picture = new Picture(user);

        entityManager.persist(picture);

        entityManager.getTransaction().commit();
        entityManager.close();
    }

    //////////////////////////////////////////////////////////////////////////////////////////

    public List<PostDTO> getUserPostsDTO(Long userId, int chunk) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        List<Post> resultList = entityManager.createQuery("SELECT p FROM Post p WHERE p.user.userId=:userId")
                .setParameter("userId", userId)
                .getResultList();
        entityManager.close();

        return resultList.stream().map(
                p -> new PostDTO(p, p.getComments().size(), p.getReactions().size(), p.getParent()
                )
        ).toList();
    }





    public List<Post> getUserHomePosts(long userId, int chunk) {
        return null;
    }

    public long getPostCommentCount(long postId) {
        return 0;
    }

    public long getPostReactionCount(long postId) {
        return 0;
    }



    public Post getPostById(long postId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        Post post = getPostById(postId, entityManager);
        entityManager.getTransaction().commit();
        entityManager.close();
        return post;
    }

    private Post getPostById(long postId, EntityManager entityManager) {
        Post post = entityManager.find(Post.class, postId);
        if(post == null) {
            entityManager.getTransaction().rollback();
            entityManager.close();
            throw new InvalidIdException("Invalid post Id");
        }
        return post;
    }

    public PostDTO getPostDTO(long postId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Post post = getPostById(postId);
        return new PostDTO(post, getPostCommentCount(postId), getPostReactionCount(postId), post.getParent());
    }

    public List<Comment> getPostComments(long postId, int chunk) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        var comments = entityManager.createQuery("SELECT c from Comment c " +
                "WHERE c.post.postId=:postId AND c.isSolution=FALSE")
                .setParameter("postId", postId)
                .setFirstResult((chunk-1) * 10)
                .setMaxResults((chunk) * 10)
                .getResultList();

        entityManager.getTransaction().commit();
        entityManager.close();
        return comments;
    }

    public Post addPost(String username, String title, String caption, long parentId) {
        Post parent = null;
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        if(parentId != -1) {
            parent = getPostById(parentId, entityManager);
        }
        User user = getUserByUsername(username, entityManager);
        Post post = new Post(user, title, caption, parent);
        entityManager.persist(post);
        entityManager.getTransaction().commit();
        entityManager.close();
        return post;
    }

    public void addReaction(long userId, long postId, int reactionType) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        Post post = entityManager.find(Post.class, postId);
        User user = entityManager.find(User.class, userId);
        Hibernate.initialize(post.getReactions());
        var resultList = entityManager.createQuery(
                "SELECT pr FROM PostReaction pr " +
                    " WHERE pr.user.userId=:userId AND pr.post.postId=:postId AND pr.reactionType=:reactoinType")
                .setParameter("userId", userId)
                .setParameter("postId", postId)
                .setParameter("reactionType", reactionType)
                .getResultList();
        if(resultList.isEmpty()) {
            post.getReactions().add(new PostReaction(user, reactionType, post));
        }
        else {
            entityManager.remove(resultList.get(0));
        }

        entityManager.getTransaction().commit();
        entityManager.close();
    }

    public List<PostReaction> getPostReactions(long postId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        Post post = entityManager.find(Post.class, postId);
        var postReactions = post.getReactions();
        entityManager.getTransaction().commit();
        entityManager.close();
        return postReactions;
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

    public Comment addComment(long userId, long postId, String commentText, Long parentId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        User user = getUserById(userId, entityManager);
        Post post = getPostById(postId, entityManager);
        Comment comment;
        Comment parent = null;
        if(parentId != null) {
            parent = getCommentById(parentId, entityManager);
        }
        comment = new Comment(user, post, commentText, Date.valueOf(LocalDate.now()), parent, false);
        entityManager.persist(comment);
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

    public List<Comment> getPostSolutions(long postId, int chunk) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        var solutions = entityManager.createQuery("SELECT c from Comment c " +
                "WHERE c.post.postId=:postId AND c.isSolution=TRUE")
                .setParameter("postId", postId)
                .setFirstResult((chunk-1) * 10)
                .setMaxResults((chunk) * 10)
                .getResultList();

        entityManager.getTransaction().commit();
        entityManager.close();
        return solutions;
    }

    public Comment addSolution(long userId, long postId, String solutionText) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        User user = getUserById(userId, entityManager);
        Post post = getPostById(postId, entityManager);
        Comment comment = new Comment(user, post, solutionText, Date.valueOf(LocalDate.now()), null, true);
        entityManager.persist(comment);
        entityManager.getTransaction().commit();
        entityManager.close();
        return comment;
    }

    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////for tests///////////////////////////////////////


    public void removeUser(long userId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        User user = entityManager.find(User.class, userId);
        if(user != null) {
            entityManager.remove(user);
        }

        entityManager.getTransaction().commit();
        entityManager.close();
    }
}
