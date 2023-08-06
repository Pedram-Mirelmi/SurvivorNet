package survivornet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import survivornet.models.UserBlock;

import java.util.Optional;

public interface BlockRepository extends JpaRepository<UserBlock, Long> {

    @Query("SELECT ub FROM UserBlock ub WHERE ub.blocker.username=:blocker AND ub.blockee.username=:blockee")
    Optional<UserBlock> findByBlockerAndBlockee(String blocker, String blockee);

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    @Query("SELECT ub FROM UserBlock ub WHERE ub.blocker.username=:blocker AND ub.blockee.username=:blockee")
    @Modifying
    void deleteByBlockerAndBlockee(String blocker, String blockee);
}
