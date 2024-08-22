package survivornet.services.domain;

import org.springframework.stereotype.Service;
import survivornet.DTO.PostDTO;
import survivornet.DTO.PostReactionDTO;
import survivornet.exceptions.UnauthorizedException;
import survivornet.models.Post;
import survivornet.services.AuthorizationService;
import survivornet.services.db.PostDbService;

import java.util.List;

@Service
public class PostService {

    private final PostDbService postDbService;
    private final AuthorizationService authorizationService;

    public PostService(PostDbService postDbService, AuthorizationService authorizationService) {
        this.postDbService = postDbService;
        this.authorizationService = authorizationService;
    }

    public List<PostDTO> getHomePosts(String username, int chunk) {
        return postDbService.getUserHomePostDTOs(username, chunk);
    }

    public PostDTO getPostDTO(String username, long postId) throws UnauthorizedException {
        if(!authorizationService.canViewPost(username, postId)) {
            throw new UnauthorizedException("User cannot view this post");
        }
        return postDbService.getPostDTO(postId);
    }


    public PostDTO addPost(String username, String title, String caption, long parentId) {
        Post post = postDbService.addPost(username, title, caption, parentId);
        return new PostDTO(post,
                0,
                0,
                post.getParent() == null ? -1 : post.getParent().getPostId(),
                post.getParent() == null ? null : post.getParent().getTitle());
    }

    public List<PostDTO> getUserPosts(String viewerUsername, String underViewUsername, int chunk) throws UnauthorizedException {
        if(!authorizationService.canAccessProfile(viewerUsername, underViewUsername)) {
            throw new UnauthorizedException("User cannot access this user!");
        }
        return postDbService.getUserPostDTOs(underViewUsername, chunk);
    }

    public boolean addReaction(String username, long postId, int reactionType) throws UnauthorizedException {
        if(!authorizationService.canAddReaction(username, postId)) {
            throw new UnauthorizedException("User cannot add reaction to this post");
        }
        return postDbService.addPostReaction(username, postId, reactionType);
    }

    public List<PostReactionDTO> getReactions(String username, long postId, int chunk) throws UnauthorizedException {
        if(!authorizationService.canViewPostReactions(username, postId)) {
            throw new UnauthorizedException("User cannot view this post's reactions");
        }
        return postDbService.getPostReactionDTOs(postId, chunk);
    }
}
