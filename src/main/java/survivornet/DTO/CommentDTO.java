package survivornet.DTO;

import survivornet.models.Comment;

import java.time.LocalDateTime;

public class CommentDTO {
    private long commentId;
    private boolean isSuggestion;
    private long parentId;
    private UserDTO user;
    private long postId;
    private String text;
    private LocalDateTime createdAt;


    public CommentDTO(Comment comment, long numberOfLikes, long numberOfDislikes) {
        this.commentId = comment.getCommentId();
        this.isSuggestion = comment.isSuggestion();
        this.parentId = comment.getParentComment() == null ? -1L : comment.getParentComment().getCommentId();
        this.user =  new UserDTO(comment.getUser(), 0, 0);
        this.postId = comment.getPost().getPostId();
        this.text = comment.getText();
        this.createdAt = comment.getCreatedAt();
    }


    public long getCommentId() {
        return commentId;
    }

    public void setCommentId(long commentId) {
        this.commentId = commentId;
    }

    public boolean isSuggestion() {
        return isSuggestion;
    }

    public void setSuggestion(boolean suggestion) {
        isSuggestion = suggestion;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
