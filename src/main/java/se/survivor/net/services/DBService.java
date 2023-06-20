package se.survivor.net.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import se.survivor.net.exceptions.InvalidIdException;
import se.survivor.net.models.Picture;
import se.survivor.net.models.Post;
import se.survivor.net.models.User;

import java.sql.Date;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;

@Service
public class DBService implements IDb {


    private final EntityManagerFactory entityManagerFactory;


    public DBService() throws ParseException {
        var registry = new StandardServiceRegistryBuilder().configure().build();
        entityManagerFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }

    @Override
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

    @Override
    public User getUserById(Long userId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            User user = getUserById(userId, entityManager);
            entityManager.close();
            return user;
        }
        catch (Exception e) {
            entityManager.close();
            throw e;
        }
    }

    public User getUserById(Long userId, EntityManager entityManager) {
        User user = entityManager.find(User.class, userId);
        if(user == null) {
            throw new InvalidIdException("Invalid user Id");
        }
        return user;
    }



    @Override
    public User getUserByUsername(String username) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            User user = getUserByUsername(username, entityManager);
            entityManager.close();
            return user;
        }
        catch (Exception e) {
            entityManager.close();
            throw e;
        }
    }

    public User getUserByUsername(String username, EntityManager entityManager) {
        var resultList = entityManager.createQuery("SELECT u FROM User u WHERE u.username=:username")
                .setParameter("username", username)
                .getResultList();
        if(resultList.isEmpty()) {
            throw new InvalidIdException("Invalid Username");
        }
        return (User) resultList.get(0);
    }

    @Override
    public User getUserByEmail(String email) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            User user = getUserByEmail(email, entityManager);
            entityManager.close();
            return user;
        }
        catch (Exception e) {
            entityManager.close();
            throw e;
        }
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

    @Override
    public boolean authenticate(String username, String password) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        User user = getUserByUsername(username, entityManager);
        return user.getPassword().equals(password);
    }

    @Override
    public List<Post> getUserPosts(Long userId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        var resultList = entityManager.createQuery("SELECT p FROM Post p WHERE p.user.userId=:userId")
                .setParameter("userId", userId)
                .getResultList();
        entityManager.close();
        return resultList;
    }

    @Override
    public List<User> getFollowers(Long userId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        User user = entityManager.find(User.class, userId);
        if(user == null) {
            throw new InvalidIdException("Invalid user Id");
        }
        var followers = user.getFollowers().stream().toList();
        entityManager.close();
        return followers;
    }

    @Override
    public List<User> getFollowings(Long userId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        User user = entityManager.find(User.class, userId);
        if(user == null) {
            throw new InvalidIdException("Invalid user Id");
        }
        var followings = user.getFollowings().stream().toList();
        entityManager.close();
        return followings;
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

}
