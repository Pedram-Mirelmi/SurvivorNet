package survivornet.DTO;

import survivornet.models.Picture;
import survivornet.models.Post;

import java.time.LocalDateTime;
import java.util.List;

public class PostDTO {
    private final long postId;

    private final UserDTO user;

    private final String title;

    private final String caption;

    private final Long parentId;

    private final LocalDateTime createdAt;

    private final List<Long> pictures;

    private final Long numberOfComments;

    private final Long numberOfReactions;

    public PostDTO(Post post, long commentCount, long reactionCount, Long parentId) {
        this.postId = post.getPostId();
        this.user = new UserDTO(post.getUser(), 0, 0);
        this.title = post.getTitle();
        this.caption = post.getCaption();
        this.parentId = parentId;
        this.createdAt = post.getCreatedAt();
        this.pictures = post.getPictures() == null ? null : post.getPictures().stream().map(Picture::getPictureId).toList();
        this.numberOfComments = (long)commentCount;
        this.numberOfReactions = (long)reactionCount;
    }

    public Long getNumberOfComments() {
        return numberOfComments;
    }

    public Long getNumberOfReactions() {
        return numberOfReactions;
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
