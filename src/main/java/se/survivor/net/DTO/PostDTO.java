package se.survivor.net.DTO;

import se.survivor.net.models.Post;

import java.sql.Date;
import java.time.LocalDateTime;

public class PostDTO {
    private long postId;

    private UserDTO user;

    private String title;

    private String caption;

    private long parentId;

    private LocalDateTime createdAt;

    public PostDTO(Post post, long commentCount, long reactionCount, long parentId) {
        this.postId = post.getPostId();
        this.user = new UserDTO(post.getUser());
        this.title = post.getTitle();
        this.caption = post.getCaption();
        this.parentId = parentId;
        this.createdAt = post.getCreatedAt();
    }

    public long getPostId() {
        return postId;
    }

    public UserDTO getUser() {
        return user;
    }

    public String getTitle() {
        return title;
    }

    public String getCaption() {
        return caption;
    }

    public long getParentId() {
        return parentId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
