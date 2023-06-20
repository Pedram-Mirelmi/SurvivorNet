package se.survivor.net.services;

import se.survivor.net.DTO.PostDTO;
import se.survivor.net.exceptions.InvalidValueException;
import se.survivor.net.models.Post;

import java.util.List;

public class PostService implements IPostService {

    IDb db;

    public PostService(IDb db) {
        this.db = db;
    }

    @Override
    public List<PostDTO> getUsersHomePosts(long userId, int chunk) throws InvalidValueException {
        if (chunk < 1) {
            throw new InvalidValueException("Invalid negative chunk value");
        }
        List<Post> posts = db.getUserHomePosts(userId, chunk);
        return posts.stream().map(
            p -> new PostDTO(
                p,
                db.getPostCommentCount(p.getPostId()),
                db.getPostReactionCount(p.getPostId()),
                null))
        .toList();

    }

    @Override
    public PostDTO getPost(long postId) {
        return null;
    }
}
