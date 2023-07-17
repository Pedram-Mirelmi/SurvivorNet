package se.survivor.net.services;

import org.springframework.stereotype.Service;
import se.survivor.net.DTO.CommentDTO;
import se.survivor.net.exceptions.UnauthorizedException;
import se.survivor.net.models.Comment;

import java.util.List;

@Service
public class CommentService {
    private final DbService dbService;
    private final AuthorizationService authorizationService   ;

    public CommentService(DbService dbService, AuthorizationService authorizationService) {
        this.dbService = dbService;
        this.authorizationService = authorizationService;
    }

    public List<CommentDTO> getPostComments(String username, long postId, int chunk) throws UnauthorizedException {
        if(!authorizationService.canViewPostComments(username, postId)) {
            throw new UnauthorizedException("User cannot get this post comments!");
        }
        return dbService.getPostComments(postId, chunk)
                .stream()
                .map(c -> new CommentDTO(c,
                        dbService.getCommentLikes(c.getCommentId()),
                        dbService.getCommentDislikes(c.getCommentId())))
                .toList();
    }

    public CommentDTO addComment(String username, long postId, String commentText, long parentId) throws UnauthorizedException {
        if(!authorizationService.canLeaveComment(username, postId)) {
            throw new UnauthorizedException("User cannot leave comment under this post");
        }
        Comment newComment = dbService.addComment(username, postId, commentText, parentId);
        return new CommentDTO(newComment, 0, 0);
    }

    public List<CommentDTO> getPostSolutions(String username, long postId, int chunk) throws UnauthorizedException {
        if(!authorizationService.canViewPostComments(username, postId)) {
            throw new UnauthorizedException("User cannot view this post solutions");
        }
        return dbService.getPostSolutions(postId, chunk)
                .stream()
                .map(c -> new CommentDTO(c,
                        dbService.getCommentLikes(c.getCommentId()),
                        dbService.getCommentDislikes(c.getCommentId())))
                .toList();
    }

    public CommentDTO addSolution(String username, long postId, String solutionText) throws UnauthorizedException {
        if(!authorizationService.canAddSolution(username, postId)) {
            throw new UnauthorizedException("User cannot add solution to this post");
        }
        Comment newSolution = dbService.addSolution(username, postId, solutionText);
        return new CommentDTO(newSolution, 0, 0);
    }

    public void likeComment(String username, long commentId, boolean likes) {
        dbService.addCommentLike(username, commentId, likes);
    }
}
