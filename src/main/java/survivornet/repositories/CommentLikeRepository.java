package survivornet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import survivornet.models.CommentLike;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

}
