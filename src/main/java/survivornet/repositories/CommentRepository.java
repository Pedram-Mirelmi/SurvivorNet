package survivornet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import survivornet.models.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query(value = "SELECT COUNT (c) FROM Comment c WHERE c.post.postId=:postId")
    long countByPostId(long postId);
}
