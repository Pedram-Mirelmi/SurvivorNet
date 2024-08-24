package survivornet.services.domain;

import org.springframework.stereotype.Service;
import survivornet.DTO.CommentDTO;
import survivornet.exceptions.UnauthorizedException;
import survivornet.services.AuthorizationService;
import survivornet.services.db.CommentDbService;

import java.util.List;

@Service
public class CommentService {
    private final CommentDbService commentDbService;
    private final AuthorizationService authorizationService;

    public CommentService(CommentDbService commentDbService, AuthorizationService authorizationService) {
        this.commentDbService = commentDbService;
        this.authorizationService = authorizationService;
    }

    public List<CommentDTO> getPostComments(String username, long postId, int chunk) throws UnauthorizedException {
        if(!authorizationService.canViewPostComments(username, postId)) {
            throw new UnauthorizedException("User cannot get this post comments!");
        }
        return commentDbService.getPostComments(postId, chunk);
    }

    public CommentDTO addComment(String username, long postId, String commentText, long parentId) throws UnauthorizedException {
        if(!authorizationService.canLeaveComment(username, postId)) {
            throw new UnauthorizedException("User cannot leave comment under this post");
        }
        return new CommentDTO(commentDbService.addComment(username, postId, commentText, parentId),
                0,
                0);
    }

    public List<CommentDTO> getPostSuggestion(String username, long postId, int chunk) throws UnauthorizedException {
        if(!authorizationService.canViewPostComments(username, postId)) {
            throw new UnauthorizedException("User cannot view this post solutions");
        }
        return commentDbService.getPostSuggestions(postId, chunk);
    }

    public CommentDTO addSuggestion(String username, long postId, String solutionText) throws UnauthorizedException {
        if(!authorizationService.canAddSuggestion(username, postId)) {
            throw new UnauthorizedException("User cannot add solution to this post");
        }
        return new CommentDTO(commentDbService.addSuggestion(username, postId, solutionText),
                0,
                0);
    }

    public boolean likeComment(String username, long commentId, boolean likes) {
        return commentDbService.addCommentLike(username, commentId, likes);
    }
}
