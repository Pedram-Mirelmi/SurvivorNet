package se.survivor.net.services;

import org.springframework.stereotype.Service;
import se.survivor.net.DTO.CommentDTO;
import se.survivor.net.models.Comment;

import java.util.List;

@Service
public class CommentService {
    private DbService dbService;
    private UserService userService;

    public CommentService(DbService dbService) {
        this.dbService = dbService;
    }

    public List<CommentDTO> getPostComments(long postId, int chunk) {
        return dbService.getPostComments(postId, chunk)
                .stream()
                .map(c -> new CommentDTO(c,
                        dbService.getCommentLikes(c.getCommentId()),
                        dbService.getCommentDislikes(c.getCommentId())))
                .toList();
    }

    public CommentDTO addComment(String username, long postId, String commentText, long parentId) {
        Comment newComment = dbService.addComment(username, postId, commentText, parentId);
        return new CommentDTO(newComment, 0, 0);
    }

    public List<CommentDTO> getPostSolutions(long postId, int chunk) {
        return dbService.getPostSolutions(postId, chunk)
                .stream()
                .map(c -> new CommentDTO(c,
                        dbService.getCommentLikes(c.getCommentId()),
                        dbService.getCommentDislikes(c.getCommentId())))
                .toList();
    }

    public CommentDTO addSolution(String username, long postId, String solutionText) {
        Comment newSolution = dbService.addSolution(username, postId, solutionText);
        return new CommentDTO(newSolution, 0, 0);
    }

    public void likeComment(String username, long commentId, boolean likes) {
        dbService.addCommentLike(username, commentId, likes);
    }
}
