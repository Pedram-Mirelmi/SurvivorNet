package survivornet.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import survivornet.DTO.UserDTO;
import survivornet.models.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByUsername(String username);

    Optional<User> findUserByEmail(String email);

    List<User> findAllByUsernameContainingOrFirstnameContainingOrLastnameContaining(String username, String firstname, String lastname, Pageable pageable);

    @Query(value = "SELECT NEW survivornet.DTO.UserDTO(u, " +
            "   (SELECT COUNT (*) FROM UserFollow uf_1 WHERE uf_1.followee.username=:username ), " +
            "   (SELECT COUNT (*) FROM UserFollow uf_2 WHERE uf_2.follower.username=:username)) " +
            "FROM survivornet.models.User u " +
            "WHERE u.username=:username")

    Optional<UserDTO> getDtoByUsername(String username);

    void deleteByUsername(String username);

    @Query(value = "SELECT NEW survivornet.DTO.UserDTO(u, " +
            "   (SELECT COUNT (*) FROM UserFollow uf_1 WHERE uf_1.followee.email=:email ), " +
            "   (SELECT COUNT (*) FROM UserFollow uf_2 WHERE uf_2.follower.email=:email)) " +
            "FROM survivornet.models.User u " +
            "WHERE u.email=:email")
    Optional<UserDTO> getDtoByEmail(String email);
}
