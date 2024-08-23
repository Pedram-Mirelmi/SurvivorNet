package survivornet.repositories;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import survivornet.DTO.PostDTO;
import survivornet.models.Post;
import survivornet.models.User;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT NEW survivornet.DTO.PostDTO(p, " +
            "   (SELECT COUNT (c) FROM Comment c WHERE c.post.postId=p.postId)," +
            "   (SELECT COUNT (pr) FROM PostReaction pr WHERE pr.post.postId=p.postId)," +
            "   p.parent.postId," +
            "   p.parent.title) " +
            "FROM Post p " +
            "LEFT JOIN p.parent parent " +
            "WHERE p.user.username=:username")
    List<PostDTO> findAllDtoByUsername(String username, Pageable pageable);

    @Query("SELECT NEW survivornet.DTO.PostDTO(p, " +
            "   (SELECT COUNT (c) FROM Comment c WHERE c.post.postId=:postId), " +
            "   (SELECT COUNT (pr) FROM PostReaction pr WHERE pr.post.postId=:postId), " +
            "   p.parent.postId," +
            "   p.parent.title) " +
            "FROM Post p " +
            "LEFT JOIN p.parent parent " +
            "WHERE p.postId=:postId")
    Optional<PostDTO> getDtoById(long postId);


    @Query("SELECT NEW survivornet.DTO.PostDTO(p, "+
            "   (SELECT COUNT (c) FROM Comment c WHERE c.post.postId=p.postId)," +
            "   (SELECT COUNT (pr) FROM PostReaction pr WHERE pr.post.postId=p.postId)," +
            "   p.parent.postId, " +
            "   p.parent.title) " +
            "FROM Post p " +
            "LEFT JOIN p.parent parent " +
            "WHERE p.user.userId IN " +
            "    (SELECT uf.followee.userId FROM UserFollow uf" +
            "    WHERE uf.follower.username=:username) " +
            "OR p.user.userId IN " +
            "   (SELECT u.userId FROM User u " +
            "   WHERE u.username=:username) " +
            "ORDER BY p.createdAt DESC ")
    List<PostDTO> findAllHomePostDtoByUsername(String username, Pageable pageable);


    @Query("SELECT p FROM Post p WHERE p.user.username=:username")
    List<Post> findAllByUsername(String username, PageRequest of);
}
