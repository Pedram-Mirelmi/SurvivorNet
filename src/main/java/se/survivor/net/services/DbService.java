package se.survivor.net.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.Hibernate;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import se.survivor.net.DTO.CommentDTO;
import se.survivor.net.DTO.PostDTO;
import se.survivor.net.DTO.PostReactionDTO;
import se.survivor.net.DTO.UserDTO;
import se.survivor.net.exceptions.InvalidIdException;
import se.survivor.net.models.Picture;
import se.survivor.net.models.Post;
import se.survivor.net.models.PostReaction;
import se.survivor.net.models.User;

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
        entityManager.persist(new User(username, password, name, email, birthDate, Date.valueOf(LocalDate.now()), "", null, null));
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
    public List<UserDTO> getFollowers(Long userId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        User user = getUserById(userId, entityManager);
        List<UserDTO> followers = user.getFollowers().stream().map(UserDTO::new).toList();
        entityManager.getTransaction().commit();
        entityManager.close();
        return followers;
    }

    public List<UserDTO> getFollowings(Long userId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        User user = getUserById(userId, entityManager);
        List<UserDTO> followings = user.getFollowings().stream().map(UserDTO::new).toList();
        entityManager.getTransaction().commit();
        entityManager.close();
        return followings;
    }

    public List<User> searchUsers(String query) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        var resultList = entityManager.createQuery(
                "SELECT u FROM User u " +
                        "WHERE u.username LIKE '%:query%' " +
                        "OR u.name LIKE '%:query%' " +
                        "OR u.email LIKE '%:query%'")
                .setParameter("query", query)
                .getResultList();
        entityManager.close();
        return resultList;
    }


    public boolean follow(String followerUsername, Long followeeId) {
        try {
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            User follower = getUserByUsername(followerUsername, entityManager);
            User followee = entityManager.find(User.class, followeeId);
            if(followee == null) {
                throw new InvalidIdException("Invalid User Id");
            }
            follower.getFollowings().add(followee);
            followee.getFollowers().add(follower);
            entityManager.getTransaction().commit();
            entityManager.close();
            return true;
        } catch (InvalidIdException e) {
            throw e;
        }
        catch (Exception e) {
            return false;
        }
    }

    public boolean unfollow(String unfollowerUsername, Long unfolloweeId) {
        try {
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            User follower = getUserByUsername(unfollowerUsername, entityManager);
            User followee = entityManager.find(User.class, unfolloweeId);
            if(followee == null) {
                throw new InvalidIdException("Invalid User Id");
            }
            follower.getFollowings().remove(followee);
            followee.getFollowers().remove(follower);
            entityManager.getTransaction().commit();
            entityManager.close();
            return true;
        }
        catch (InvalidIdException e) {
            throw e;
        }
        catch (Exception e) {
            return false;
        }
    }

    public boolean unblock(String unblockerUsername, Long unblockeeId) {
        try {
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            User blocker = getUserByUsername(unblockerUsername, entityManager);
            User blockee = entityManager.find(User.class, unblockeeId);
            if(blockee == null) {
                throw new InvalidIdException("Invalid User Id");
            }
            blocker.getBlockList().remove(blockee);
            blockee.getBlockedList().remove(blocker);
            entityManager.getTransaction().commit();
            entityManager.close();
            return true;
        }
        catch (InvalidIdException e) {
            throw e;
        }
        catch (Exception e) {
            return false;
        }
    }

    public boolean block(String blockerUsername, Long blockeeId) {
        try {
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            User blocker = getUserByUsername(blockerUsername, entityManager);
            User blockee = entityManager.find(User.class, blockeeId);
            if(blockee == null) {
                throw new InvalidIdException("Invalid User Id");
            }
            blocker.getBlockList().add(blockee);
            blockee.getBlockedList().add(blocker);
            entityManager.getTransaction().commit();
            entityManager.close();
            return true;
        }
        catch (InvalidIdException e) {
            throw e;
        }
        catch (Exception e) {
            return false;
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

    public List<Post> getUserPosts(Long userId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        var resultList = entityManager.createQuery("SELECT p FROM Post p WHERE p.user.userId=:userId")
                .setParameter("userId", userId)
                .getResultList();
        entityManager.close();

        return resultList.stream().map( (Post p) -> new PostDTO(p, p.getComments().size(), p.getReactions().size(), p.getParent()));
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

    public void addFollow(long followerId, long followeeId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        User follower = entityManager.find(User.class, followerId);
        User followee = entityManager.find(User.class, followeeId);

        // one way is enough to store both ways!
        follower.getFollowings().add(followee);

        entityManager.getTransaction().commit();
        entityManager.close();
    }

    public Post getPost(long postId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        Post post = getPost(postId, entityManager);
        entityManager.getTransaction().commit();
        entityManager.close();
        return post;
    }

    private Post getPost(long postId, EntityManager entityManager) {
        Post post = entityManager.find(Post.class, postId);
        if(post == null) {
            entityManager.getTransaction().rollback();
            entityManager.close();
            throw new InvalidIdException("Invalid post Id");
        }
        return post;
    }

    public List<CommentDTO> getPostComments(long postId) {

    }

    public boolean addPost(String username, String title, String caption, long parentId) {
        Post parent = null;
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        if(parentId != -1) {
            parent = getPost(parentId, entityManager);
        }
        User user = getUserByUsername(username, entityManager)
        Post post = new Post()


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
}