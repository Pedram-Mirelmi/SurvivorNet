package survivornet.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import survivornet.DTO.PostReactionDTO;
import survivornet.models.PostReaction;

import java.util.List;
import java.util.Optional;

public interface PostReactionRepository extends JpaRepository<PostReaction, Long> {

    @Query("SELECT COUNT (pr) FROM PostReaction pr WHERE pr.post.postId=:postId")
    long countByPostId(long postId);

    @Query("SELECT NEW survivornet.DTO.PostReactionDTO( " +
            "   NEW survivornet.DTO.UserDTO(pr.user, 0, 0), " +
            "   pr.post.postId, " +
            "   pr.reactionType) " +
            "FROM PostReaction pr " +
            "WHERE pr.post.postId=:postId")
    List<PostReactionDTO> findAllDtoByPostId(long postId, Pageable pageable);

    @Query("SELECT pr FROM PostReaction pr WHERE pr.user.username=:username AND pr.post.postId=:postId")
    Optional<PostReaction> findByUsernameAndPostId(String username, long postId);
}
