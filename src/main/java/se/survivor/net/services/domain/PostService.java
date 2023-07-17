package se.survivor.net.services.domain;

import org.springframework.stereotype.Service;
import se.survivor.net.DTO.PostDTO;
import se.survivor.net.DTO.PostReactionDTO;
import se.survivor.net.exceptions.InvalidValueException;
import se.survivor.net.exceptions.UnauthorizedException;
import se.survivor.net.models.Post;
import se.survivor.net.services.AuthorizationService;
import se.survivor.net.services.db.PostDbService;

import java.util.List;

@Service
public class PostService {

    private final PostDbService postDbService;
    private final AuthorizationService authorizationService;

    public PostService(PostDbService postDbService, AuthorizationService authorizationService) {
        this.postDbService = postDbService;
        this.authorizationService = authorizationService;
    }

    public List<PostDTO> getHomePosts(String username, int chunk) throws InvalidValueException {
        if (chunk < 0) {
            throw new InvalidValueException("Invalid negative chunk value");
        }
        List<Post> posts = postDbService.getUserHomePosts(username, chunk);
        return posts.stream().map(
                p -> new PostDTO(
                        p,
                        postDbService.getPostCommentCount(p.getPostId()),
                        postDbService.getPostReactionCount(p.getPostId()),
                        p.getParent() == null ? -1 : p.getParent().getPostId())
        ).toList();
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
                post.getParent() == null ? -1 : post.getParent().getPostId());
    }

    public List<PostDTO> getUserPosts(String viewerUsername, String underViewUsername, int chunk) throws InvalidValueException, UnauthorizedException {
        if (chunk < 0) {
            throw new InvalidValueException("Invalid negative chunk value");
        }
        if(!authorizationService.canAccessProfile(viewerUsername, underViewUsername)) {
            throw new UnauthorizedException("User cannot access this user!");
        }
        return postDbService.getUserPosts(underViewUsername, chunk)
                .stream()
                .map(p -> new PostDTO(p,
                        postDbService.getPostCommentCount(p.getPostId()),
                        postDbService.getPostReactionCount(p.getPostId()),
                        p.getParent() == null ? -1 : p.getParent().getPostId()
                )).toList();
    }

    public void addReaction(String username, long postId, int reactionType) throws UnauthorizedException {
        if(!authorizationService.canAddReaction(username, postId)) {
            throw new UnauthorizedException("User cannot add reaction to this post");
        }
        postDbService.addPostReaction(username, postId, reactionType);
    }

    public List<PostReactionDTO> getReactions(String username, long postId) throws UnauthorizedException {
        if(!authorizationService.canViewPostReactions(username, postId)) {
            throw new UnauthorizedException("User cannot view this post's reactions");
        }
        return postDbService.getPostReactions(postId).
                stream().
                map(PostReactionDTO::new).toList();
    }
}
