package survivornet.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import survivornet.DTO.CommentDTO;
import survivornet.models.Comment;
import survivornet.projections.CommentLikesProjection;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT COUNT (c) FROM Comment c WHERE c.post.postId=:postId")
    long countByPostId(long postId);

    @Query("SELECT NEW survivornet.DTO.CommentDTO( " +
            "   c, " +
            "   ( SELECT COUNT (*) FROM CommentLike cl WHERE cl.comment.commentId=c.commentId AND cl.isLike=TRUE), " +
            "   ( SELECT COUNT (*) FROM CommentLike cl WHERE cl.comment.commentId=c.commentId AND cl.isLike=FALSE)) " +
            "FROM Comment c " +
            "WHERE c.post.postId=:postId AND c.isSolution=FALSE")
    List<CommentDTO> findAllCommentByPostId(long postId, Pageable of);


    @Query("SELECT NEW survivornet.DTO.CommentDTO(" +
            "   c, " +
            "   ( SELECT COUNT (*) FROM CommentLike cl WHERE cl.comment.commentId=c.commentId AND cl.isLike=TRUE ), " +
            "   ( SELECT COUNT (*) FROM CommentLike cl WHERE cl.comment.commentId=c.commentId AND cl.isLike=FALSE)) " +
            "FROM Comment c " +
            "WHERE c.post.postId=:postId AND c.isSolution=TRUE")
    List<CommentDTO> findAllSolutionByPostId(long postId, Pageable of);



}
