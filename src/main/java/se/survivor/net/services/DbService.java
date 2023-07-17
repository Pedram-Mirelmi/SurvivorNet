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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static se.survivor.net.utils.Constants.*;

@Service
public class DbService {


    private final EntityManagerFactory entityManagerFactory;


    public DbService() {
        var registry = new StandardServiceRegistryBuilder().configure().build();
        entityManagerFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }

    public User addUser(@NotNull String username,
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
        return user;
    }


    public User getUserById(Long userId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        User user = getUserById(userId, entityManager);
        entityManager.getTransaction().commit();
        entityManager.close();
        return user;
    }

    protected User getUserById(Long userId, EntityManager entityManager) {
        User user = entityManager.find(User.class, userId);
        if(user == null) {
            if(entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
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
        var resultList = entityManager.createQuery(
                    "SELECT u FROM User u " +
                    "WHERE u.username=:username", User.class)
                .setParameter(USERNAME, username)
                .getResultList();
        if(resultList.isEmpty()) {
            if(entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            entityManager.close();
            throw new InvalidIdException("Invalid Username");
        }
        return resultList.get(0);
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
        List<User> resultList = entityManager.createQuery(
                    "SELECT u FROM User u " +
                    "WHERE u.email=:email", User.class)
                .setParameter(EMAIL, email)
                .getResultList();
        if(resultList.isEmpty()) {
            throw new InvalidIdException("Invalid email");
        }
        return resultList.get(0);
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
    public List<User> getFollowers(String username) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        User user = getUserByUsername(username, entityManager);
        Hibernate.initialize(user.getFollowers());
        List<User> followers = user.getFollowers();
        entityManager.getTransaction().commit();
        entityManager.close();
        return followers;
    }

    public List<User> getFollowings(String username) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        User user = getUserByUsername(username, entityManager);
        Hibernate.initialize(user.getFollowings());
        List<User> followings = user.getFollowings();
        entityManager.getTransaction().commit();
        entityManager.close();
        return followings;
    }

    public List<User> searchUsers(String query) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        List<User> resultList = entityManager.createQuery(
                "SELECT u FROM User u " +
                        "WHERE u.username LIKE '%:query%' " +
                        "OR u.name LIKE '%:query%' " +
                        "OR u.email LIKE '%:query%'", User.class)
                .setParameter(QUERY, query)
                .getResultList();
        entityManager.getTransaction().commit();
        entityManager.close();
        return resultList;
    }

    public void changeFollow(String follower, String followee, boolean follow) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        User followerUser = getUserByUsername(follower, entityManager);
        User followeeUser = getUserByUsername(followee, entityManager);

        try {
            // one way is enough to store both ways!
            if(follow) {
                followerUser.getFollowings().add(followeeUser);
            }
            else {
                followerUser.getFollowings().remove(followeeUser);
            }
            entityManager.getTransaction().commit();
            entityManager.close();
        }
        catch (Exception e) {
            entityManager.getTransaction().rollback();
            entityManager.close();
        }
    }

    public void changeBlock(String blocker, String blockee, boolean block) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        User blockerUser = getUserByUsername(blocker, entityManager);
        User blockeeUser = getUserByUsername(blockee, entityManager);

        try {
            // one way is enough to store both ways!
            if(block) {
                blockerUser.getBlockList().add(blockeeUser);
            }
            else {
                blockerUser.getBlockList().remove(blockeeUser);
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
    public Picture addPictureForProfile(String username) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        User user = getUserByUsername(username, entityManager);
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

        User user = getUserByUsername(username, entityManager);
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

        Post post = getPostById(postId);
        User user = post.getUser();
        Picture picture = new Picture(user, post);
        post.getPictures().add(picture);
        entityManager.persist(picture);

        entityManager.getTransaction().commit();
        entityManager.close();
        return picture;
    }

    //////////////////////////////////////////////////////////////////////////////////////////

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



    public Post getPostById(long postId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        Post post = getPostById(postId, entityManager);
        entityManager.getTransaction().commit();
        entityManager.close();
        return post;
    }

    protected Post getPostById(long postId, EntityManager entityManager) {
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

    public Post addPost(String username, String title, String caption, long parentId) {
        Post parent = null;
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        if(parentId != -1) {
            parent = getPostById(parentId, entityManager);
        }
        User user = getUserByUsername(username, entityManager);
        Post post = new Post(user, title, caption, LocalDateTime.now(), parent);
        entityManager.persist(post);
        entityManager.getTransaction().commit();
        entityManager.close();
        return post;
    }

    public void addReaction(String username, long postId, int reactionType) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        Post post = entityManager.find(Post.class, postId);
        User user = getUserByUsername(username, entityManager);
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

    public Comment addComment(String username, long postId, String commentText, long parentId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        User user = getUserByUsername(username, entityManager);
        Post post = getPostById(postId, entityManager);
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
                .setParameter(POST_ID, postId)
                .setFirstResult(chunk * 10)
                .setMaxResults((chunk+1) * 10)
                .getResultList();

        entityManager.getTransaction().commit();
        entityManager.close();
        return solutions;
    }

    public Comment addSolution(String username, long postId, String solutionText) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        User user = getUserByUsername(username, entityManager);
        Post post = getPostById(postId, entityManager);
        Comment comment = new Comment(user, post, solutionText, LocalDateTime.now(), null, true);
        entityManager.persist(comment);
        entityManager.getTransaction().commit();
        entityManager.close();
        return comment;
    }

    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////for tests///////////////////////////////////////


    public void removeUser(String username) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        User user = getUserByUsername(username, entityManager);
        user.getPictures().clear();
        user.setProfilePic(null);
        user.setBackgroundPic(null);

        entityManager.createQuery("DELETE FROM Picture p WHERE p.owner.userId=:userId")
                .setParameter(USER_ID, user.getUserId())
                .executeUpdate();
        
        entityManager.createQuery("DELETE FROM CommentLike cl WHERE cl.user.userId=:userId")
                .setParameter(USER_ID, user.getUserId())
                .executeUpdate();

        entityManager.createQuery("DELETE FROM Comment c WHERE c.user.userId=:userId")
                .setParameter(USER_ID, user.getUserId())
                .executeUpdate();

        entityManager.createQuery("DELETE FROM PostReaction pr WHERE pr.user.userId=:userId")
                .setParameter(USER_ID, user.getUserId())
                .executeUpdate();

        entityManager.createQuery("DELETE FROM Post p WHERE p.user.userId=:userId")
                .setParameter(USER_ID, user.getUserId())
                .executeUpdate();


        entityManager.remove(user);

        entityManager.getTransaction().commit();
        entityManager.close();
    }


    public void addCommentLike(String username, long commentId, boolean likes) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        Comment comment = getCommentById(commentId, entityManager);

        if(!hasUserLikedComment(username, commentId, entityManager)) {
            User user = getUserByUsername(username, entityManager);
            CommentLike commentLike = new CommentLike(user, comment, likes);
            entityManager.persist(commentLike);
        }

        entityManager.getTransaction().commit();
        entityManager.close();
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
