package survivornet.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import survivornet.models.User;
import survivornet.models.UserFollow;
import survivornet.projections.FolloweeProjection;
import survivornet.projections.FollowerProjection;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<UserFollow, Long> {

    List<FollowerProjection> findAllByFollowee(User followee, Pageable pageable);

    List<FolloweeProjection> findAllByFollower(User follower, Pageable pageable);

    @Query("SELECT uf FROM UserFollow uf WHERE uf.follower.username=:follower AND uf.followee.username=:followee")
    Optional<UserFollow> findByFolloweeAndFollower(String follower, String followee);

    void deleteByFollowerAndFollowee(User follower, User followee);
}
