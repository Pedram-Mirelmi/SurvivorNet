package se.survivor.net.services.db;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.Hibernate;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import se.survivor.net.exceptions.InvalidIdException;
import se.survivor.net.models.User;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static se.survivor.net.utils.Constants.*;

@Service
public class UserDbService {
    private final EntityManagerFactory entityManagerFactory;


    public UserDbService() {
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

    public User getUserById(Long userId, EntityManager entityManager) {
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


    public boolean authenticateByPassword(String username, String password) {
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
            if(follow && !followeeUser.getBlockList().contains(followerUser)) {
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
                blockerUser.getFollowings().remove(blockeeUser);
                blockeeUser.getFollowings().remove(blockerUser);
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
}
