package se.survivor.net.services;

import org.springframework.stereotype.Service;
import se.survivor.net.DTO.PostDTO;
import se.survivor.net.DTO.PostReactionDTO;
import se.survivor.net.exceptions.InvalidValueException;
import se.survivor.net.exceptions.UnauthorizedException;
import se.survivor.net.models.Picture;
import se.survivor.net.models.Post;

import java.util.List;

@Service
public class PostService {

    private final DbService dbService;
    private final AuthorizationService authorizationService;

    public PostService(DbService dbService, AuthorizationService authorizationService) {
        this.dbService = dbService;
        this.authorizationService = authorizationService;
    }

    public List<PostDTO> getHomePosts(String username, int chunk) throws InvalidValueException {
        if (chunk < 0) {
            throw new InvalidValueException("Invalid negative chunk value");
        }
        List<Post> posts = dbService.getUserHomePosts(username, chunk);
        return posts.stream().map(
                p -> new PostDTO(
                        p,
                        dbService.getPostCommentCount(p.getPostId()),
                        dbService.getPostReactionCount(p.getPostId()),
                        p.getParent() == null ? -1 : p.getParent().getPostId())
        ).toList();
    }

    public PostDTO getPostDTO(String username, long postId) throws UnauthorizedException {
        if(!authorizationService.canViewPost(username, postId)) {
            throw new UnauthorizedException("User cannot view this post");
        }
        return dbService.getPostDTO(postId);
    }


    public PostDTO addPost(String username, String title, String caption, long parentId) {
        Post post = dbService.addPost(username, title, caption, parentId);
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
        return dbService.getUserPosts(underViewUsername, chunk)
                .stream()
                .map(p -> new PostDTO(p,
                        dbService.getPostCommentCount(p.getPostId()),
                        dbService.getPostReactionCount(p.getPostId()),
                        p.getParent() == null ? -1 : p.getParent().getPostId()
                )).toList();
    }

    public void addReaction(String username, long postId, int reactionType) throws UnauthorizedException {
        if(!authorizationService.canAddReaction(username, postId)) {
            throw new UnauthorizedException("User cannot add reaction to this post");
        }
        dbService.addReaction(username, postId, reactionType);
    }

    public List<PostReactionDTO> getReactions(String username, long postId) throws UnauthorizedException {
        if(!authorizationService.canViewPostReactions(username, postId)) {
            throw new UnauthorizedException("User cannot view this post's reactions");
        }
        return dbService.getPostReactions(postId).
                stream().
                map(PostReactionDTO::new).toList();
    }

    public Picture addPictureToPost(String username, long postId) throws UnauthorizedException {
        if(!authorizationService.canAddPictureToPost(username, postId)) {
            throw new UnauthorizedException("User cannot add picture to this post");
        }
        return dbService.addPicturePost(postId);
    }
}
