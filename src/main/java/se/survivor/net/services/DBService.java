package se.survivor.net.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.stereotype.Service;
import se.survivor.net.exceptions.InvalidIdException;
import se.survivor.net.models.User;

import java.text.ParseException;

@Service
public class DBService implements IDb {


    private final EntityManagerFactory entityManagerFactory;


    public DBService() throws ParseException {
        var registry = new StandardServiceRegistryBuilder().configure().build();
        entityManagerFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }

    @Override
    public User getUserById(Long userId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        User user = entityManager.find(User.class, userId);
        if(user == null) {
            throw new InvalidIdException("Invalid user Id");
        }
        return user;
    }


}
