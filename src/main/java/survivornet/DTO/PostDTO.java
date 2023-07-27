package survivornet.DTO;

import survivornet.models.Picture;
import survivornet.models.Post;

import java.time.LocalDateTime;
import java.util.List;

public class PostDTO {
    private long postId;

    private UserDTO user;

    private String title;

    private String caption;

    private long parentId;

    private LocalDateTime createdAt;

    private List<Long> pictures;

    public PostDTO(Post post, long commentCount, long reactionCount, long parentId) {
        this.postId = post.getPostId();
        this.user = new UserDTO(post.getUser(), 0, 0);
        this.title = post.getTitle();
        this.caption = post.getCaption();
        this.parentId = parentId;
        this.createdAt = post.getCreatedAt();
        this.pictures = post.getPictures() == null ? null : post.getPictures().stream().map(Picture::getPictureId).toList();
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

    public List<Long> getPictures() {
        return pictures;
    }
}
