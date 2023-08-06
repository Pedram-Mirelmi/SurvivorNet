package survivornet.services.db;


import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import survivornet.DTO.CommentDTO;
import survivornet.exceptions.InvalidIdException;
import survivornet.models.Comment;
import survivornet.models.CommentLike;
import survivornet.models.Post;
import survivornet.models.User;
import survivornet.projections.CommentLikesProjection;
import survivornet.repositories.CommentLikeRepository;
import survivornet.repositories.CommentRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static survivornet.utils.Constants.CHUNK_SIZE;

@Service
public class CommentDbService {
    private final UserDbService userDbService;
    private final PostDbService postDbService;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;

    public CommentDbService(UserDbService userDbService, PostDbService postDbService, CommentRepository commentRepository, CommentLikeRepository commentLikeRepository) {
        this.userDbService = userDbService;
        this.postDbService = postDbService;
        this.commentRepository = commentRepository;
        this.commentLikeRepository = commentLikeRepository;
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public Comment addComment(String username, long postId, String commentText, long parentId) {
        User user = userDbService.getUserByUsername(username);
        Post post = postDbService.getPostById(postId);
        Optional<Comment> parent = commentRepository.findById(parentId);
        return commentRepository.save(new Comment(user, post, commentText, LocalDateTime.now(), parent.orElse(null), false));
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public Comment addSolution(String username, long postId, String solutionText) {
        User user = userDbService.getUserByUsername(username);
        Post post = postDbService.getPostById(postId);
        return commentRepository.save(new Comment(user, post, solutionText, LocalDateTime.now(),null, true));
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Comment getCommentById(long commentId) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        if(comment.isPresent()) {
            return comment.get();
        }
        throw new InvalidIdException("Invalid comment Id");
    }


    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<CommentDTO> getPostComments(long postId, int chunk) {
        return commentRepository.findAllCommentByPostId(postId, PageRequest.of(chunk, CHUNK_SIZE));
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<CommentDTO> getPostSolutions(long postId, int chunk) {
        return commentRepository.findAllSolutionByPostId(postId, PageRequest.of(chunk, CHUNK_SIZE));
    }

    public boolean addCommentLike(String username, long commentId, boolean isLike) {
        Optional<CommentLike> like = commentLikeRepository.findByUsernameAndCommentId(username, commentId);
        CommentLike commentLike;
        if(like.isEmpty()) {
            User user = userDbService.getUserByUsername(username);
            Comment comment = getCommentById(commentId);
            commentLike = new CommentLike(user, comment, isLike);
        }
        else {
            commentLike = like.get();
            commentLike.setLike(isLike);
        }
        commentLikeRepository.save(commentLike);
        return true;
    }

    public CommentLikesProjection getCommentLikesAndDislikes(long commentId) {
        return commentLikeRepository.countCommentLikesAndDislikes(commentId);
    }

    private boolean hasUserLikedComment(String username, long commentId) {
        return commentLikeRepository.findByUsernameAndCommentId(username, commentId).isPresent();
    }


}
