package se.survivor.net.services;

import org.springframework.stereotype.Service;
import se.survivor.net.DTO.PostDTO;
import se.survivor.net.DTO.PostReactionDTO;
import se.survivor.net.exceptions.InvalidValueException;
import se.survivor.net.models.Picture;
import se.survivor.net.models.Post;
import se.survivor.net.models.User;

import java.util.List;

@Service
public class PostService {

    DbService dbService;
    private CommentService commentService;

    public PostService(DbService dbService) {
        this.dbService = dbService;
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

    public PostDTO getPostDTO(long postId) {
        return dbService.getPostDTO(postId);
    }


    public PostDTO addPost(String username, String title, String caption, long parentId) {
        Post post = dbService.addPost(username, title, caption, parentId);
        return new PostDTO(post,
                0,
                0,
                post.getParent() == null ? -1 : post.getParent().getPostId());
    }

    public List<PostDTO> getUserPosts(String username, int chunk) throws InvalidValueException {
        if (chunk < 0) {
            throw new InvalidValueException("Invalid negative chunk value");
        }
        return dbService.getUserPosts(username, chunk)
                .stream()
                .map(p -> new PostDTO(p,
                        dbService.getPostCommentCount(p.getPostId()),
                        dbService.getPostReactionCount(p.getPostId()),
                        p.getParent() == null ? -1 : p.getParent().getPostId()
                )).toList();
    }

    public void addReaction(String username, long postId, int reactionType) {
        dbService.addReaction(username, postId, reactionType);
    }

    public List<PostReactionDTO> getReactions(long postId) {
        return dbService.getPostReactions(postId).
                stream().
                map(PostReactionDTO::new).toList();
    }

    public Picture addPictureToPost(long postId) {
        return dbService.addPicturePost(postId);
    }
}
