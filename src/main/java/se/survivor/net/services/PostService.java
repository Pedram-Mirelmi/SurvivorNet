package se.survivor.net.services;

import org.springframework.stereotype.Service;
import se.survivor.net.DTO.PostDTO;
import se.survivor.net.DTO.PostReactionDTO;
import se.survivor.net.exceptions.InvalidValueException;
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
        if (chunk < 1) {
            throw new InvalidValueException("Invalid negative chunk value");
        }
        User user = dbService.getUserByUsername(username);
        List<Post> posts = dbService.getUserHomePosts(user.getUserId(), chunk);
        return posts.stream().map(
                p -> new PostDTO(
                        p,
                        dbService.getPostCommentCount(p.getPostId()),
                        dbService.getPostReactionCount(p.getPostId()),
                        null)
        ).toList();
    }

    public PostDTO getPostDTO(long postId) {
        return dbService.getPostDTO(postId);
    }


    public PostDTO addPost(String username, String title, String caption, long parentId) {
        return new PostDTO(dbService.addPost(username, title, caption, parentId), 0, 0, dbService.getPostById(parentId));
    }

    public List<PostDTO>getUserPosts(Long userId, int chunk) {
        return null;
    }

    public void addReaction(String username, long postId, int reactionType) {
        User user = dbService.getUserByUsername(username);
        dbService.addReaction(user.getUserId(), postId, reactionType);
    }

    public List<PostReactionDTO> getReactions(long postId) {
        return dbService.getPostReactions(postId).
                stream().
                map(PostReactionDTO::new).toList();
    }
}
