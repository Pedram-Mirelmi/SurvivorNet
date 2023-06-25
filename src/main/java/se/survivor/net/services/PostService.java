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

    public List<PostDTO> getUserPosts(String username, int chunk) throws InvalidValueException {
        if(chunk < 1) {
            throw new InvalidValueException("Invalid negative chunk value");
        }
        User user = dbService.getUserByUsername(username);
        List<Post> posts = dbService.getUserPostsDTO(user.getUserId());
        return posts.stream().map(
                p -> new PostDTO(
                        p,
                        dbService.getPostCommentCount(p.getPostId()),
                        dbService.getPostReactionCount(p.getPostId()),
                        null)
        ).toList();
    }

    public PostDTO getPostDTO(long postId) {
        return new PostDTO(dbService.getPost(postId),
                dbService.getPostCommentCount(postId),
                dbService.getPostReactionCount(postId),
                dbService.getPostComments(postId));
    }


    public PostDTO getPostDTOWithComments(long postId) {
        Post post = dbService.getPost(postId);
        return new PostDTO(post,
                dbService.getPostCommentCount(postId),
                dbService.getPostReactionCount(postId),
                commentService.getPostComments(postId));
    }

    public boolean addPost(String username, String title, String caption, long parentId) {
        dbService.addPost(username, title, caption, parentId);

    }
}
