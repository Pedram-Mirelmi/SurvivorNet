package survivornet.services.db;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import survivornet.DTO.UserDTO;
import survivornet.exceptions.InvalidIdException;
import survivornet.models.User;
import survivornet.models.UserBlock;
import survivornet.models.UserFollow;
import survivornet.projections.FolloweeProjection;
import survivornet.projections.FollowerProjection;
import survivornet.repositories.BlockRepository;
import survivornet.repositories.FollowRepository;
import survivornet.repositories.UserRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static survivornet.utils.Constants.CHUNK_SIZE;

@Service
public class UserDbService {
    private final EntityManagerFactory entityManagerFactory;
    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final BlockRepository blockRepository;

    @Autowired
    public UserDbService(UserRepository userRepository, FollowRepository followRepository, BlockRepository blockRepository) {
        this.userRepository = userRepository;
        this.followRepository = followRepository;
        this.blockRepository = blockRepository;
        var registry = new StandardServiceRegistryBuilder().configure().build();
        entityManagerFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public User addUser(
            String username,
            String name,
            String password,
            String email,
            Date birthDate,
            String bio) {
        return persistUser(new User(username,
                password,
                name,
                email,
                birthDate,
                Date.valueOf(LocalDate.now()),
                bio,
                null,
                null));
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public User persistUser(User user) {
        return this.userRepository.save(user);
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public User getUserById(long userId) {
        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty()) {
            throw new InvalidIdException("Invalid user id");
        }
        return user.get();
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public User getUserByUsername(String username) {
        Optional<User> user = userRepository.findUserByUsername(username);
        if(user.isEmpty()) {
            throw new InvalidIdException("Invalid username");
        }
        return user.get();
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public User getUserByEmail(String email) {
        Optional<User> user = userRepository.findUserByEmail(email);
        if(user.isEmpty()) {
            throw new InvalidIdException("Invalid username");
        }
        return user.get();
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public boolean authenticateByPassword(String username, String password) {
        Optional<User> user = userRepository.findUserByUsername(username);
        return user.isPresent() && password.equals(user.get().getPassword());
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<User> getFollowers(String username, int chunk) {
        return followRepository.findAllByFollowee(getUserByUsername(username), PageRequest.of(chunk, CHUNK_SIZE))
                .stream().map(FollowerProjection::getFollower).toList();
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<User> getFollowings(String username, int chunk) {
        return followRepository.findAllByFollower(getUserByUsername(username), PageRequest.of(chunk, CHUNK_SIZE))
                .stream().map(FolloweeProjection::getFollowee).toList();
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<User> searchUsers(String query, int chunk) {
        return userRepository.findAllByUsernameContainingOrNameContaining(query, query, PageRequest.of(chunk, CHUNK_SIZE));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean changeFollow(String follower, String followee, boolean follow) {
        if(follow) {
            UserFollow userFollow = new UserFollow(getUserByUsername(follower), getUserByUsername(followee));
            followRepository.save(userFollow);
        }
        else {
            followRepository.deleteByFollowerAndFollowee(
                    getUserByUsername(follower),
                    getUserByUsername(followee));
        }
        return true;
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public boolean changeBlock(EntityManager entityManager, String blocker, String blockee, boolean block) {
        if(block) {
            UserBlock userFollow = new UserBlock(getUserByUsername(blocker), getUserByUsername(blockee));
            blockRepository.save(userFollow);
        }
        else {
            blockRepository.deleteByBlockerAndBlockee(blocker, blockee);
        }
        return true;
    }


    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public boolean removeUser(String username) {
        userRepository.deleteByUsername(username);
        return true;
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public UserDTO getUserDtoByUsername(String username) {
        Optional<UserDTO> user = userRepository.getDtoByUsername(username);
        if(user.isEmpty()) {
            throw new InvalidIdException("Invalid username");
        }
        return user.get();
    }
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public UserDTO getUserDtoByEmail(String email) {
        Optional<UserDTO> user = userRepository.getDtoByEmail(email);
        if(user.isEmpty()) {
            throw new InvalidIdException("Invalid email");
        }
        return user.get();
    }
}
