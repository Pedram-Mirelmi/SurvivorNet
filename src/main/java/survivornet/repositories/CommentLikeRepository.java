package survivornet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import survivornet.models.CommentLike;
import survivornet.projections.CommentLikesProjection;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    @Query("SELECT COUNT (cl) FROM CommentLike cl WHERE cl.comment.commentId=:commentId")
    long countAllByCommentId(long commentId);

//    @Query(value = "SELECT " +
//            "    COUNT(TRUE) AS likes, " +
//            "    COUNT(FALSE) AS dislikes " +
//            "FROM " +
//            "     ( " +
//            "        SELECT cl.islike " +
//            "         FROM comment_likes cl " +
//            "         WHERE cl.comment_id=:commentId " +
//            "     ) as c",
//        nativeQuery = true)
//    CommentLikesProjection countCommentLikesAndDislikes(long commentId);

    @Query("SELECT  " +
            "    COUNT(CASE WHEN cl.isLike = TRUE THEN 1 END) AS likes, " +
            "    COUNT(CASE WHEN cl.isLike = FALSE THEN 1 END) AS dislikes " +
            "FROM CommentLike cl " +
            "WHERE cl.comment.commentId=:commentId")
    CommentLikesProjection countCommentLikesAndDislikes(long commentId);




    @Query("SELECT cl FROM CommentLike cl WHERE cl.user.username=:username AND cl.comment.commentId=:commentId")
    Optional<CommentLike> findByUsernameAndCommentId(String username, long commentId);
}
