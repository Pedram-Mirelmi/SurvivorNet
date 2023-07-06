package se.survivor.net.services;

import org.springframework.stereotype.Service;
import se.survivor.net.DTO.PostDTO;
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

    public List<PostDTO> getUserPostsDTO(String username, int chunk) throws InvalidValueException {
        if(chunk < 1) {
            throw new InvalidValueException("Invalid negative chunk value");
        }
        User user = dbService.getUserByUsername(username);
        return dbService.getUserPostsDTO(user.getUserId(), chunk);
    }

    public PostDTO getPostDTO(long postId) {
        return dbService.getPostDTO(postId);
    }


    public boolean addPost(String username, String title, String caption, long parentId) {
        dbService.addPost(username, title, caption, parentId);
        return true;
    }

    public List<PostDTO> getUserPosts(Long userId, int chunk) {
        return null;
    }
}
