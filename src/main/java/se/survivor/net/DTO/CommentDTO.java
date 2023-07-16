package se.survivor.net.DTO;

import se.survivor.net.models.Comment;

import java.time.LocalDateTime;

public class CommentDTO {
    private long commentId;
    private boolean isSolution;
    private long parentId;
    private UserDTO user;
    private long postId;
    private String text;
    private LocalDateTime createdAt;


    public CommentDTO(Comment comment, long numberOfLikes, long numberOfDislikes) {
        this.commentId = comment.getCommentId();
        this.isSolution = comment.isSolution();
        this.parentId = comment.getParentComment() == null ? -1L : comment.getParentComment().getCommentId();
        this.user =  new UserDTO(comment.getUser());
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

    public boolean isSolution() {
        return isSolution;
    }

    public void setSolution(boolean solution) {
        isSolution = solution;
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
