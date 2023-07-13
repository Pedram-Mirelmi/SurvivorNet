package se.survivor.net.DTO;

import jakarta.persistence.*;
import se.survivor.net.models.Post;
import se.survivor.net.models.User;

import java.sql.Date;
import java.util.List;

public class PostDTO {
    private long postId;

    private UserDTO user;

    private String title;

    private String caption;

    private PostDTO parent;

    private Date created_at;

    public PostDTO(Post post, long commentCount, long reactionCount, Post parent) {

    }
}
