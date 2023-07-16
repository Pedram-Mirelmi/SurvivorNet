package se.survivor.net.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "comments", schema = "public")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long commentId;

    @Column(nullable = false)
    private boolean isSolution;

    @ManyToOne(targetEntity = Comment.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "parentId", referencedColumnName = "commentId")
    private Comment parentComment;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    private User user;

    @ManyToOne(targetEntity = Post.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "postId", referencedColumnName = "postId")
    private Post post;

    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(targetEntity = CommentLike.class,
            mappedBy = "comment",
            fetch = FetchType.LAZY)
    private List<CommentLike> likes;

    public Comment(User user, Post post, String text, LocalDateTime createdAt, Comment parentComment, boolean isSolution) {
        this.isSolution = isSolution;
        this.parentComment = parentComment;
        this.user = user;
        this.post = post;
        this.text = text;
        this.createdAt = createdAt;
    }

    public Comment() {

    }

    public Long getCommentId() {
        return commentId;
    }

    public boolean isSolution() {
        return isSolution;
    }

    public Comment getParentComment() {
        return parentComment;
    }

    public User getUser() {
        return user;
    }

    public Post getPost() {
        return post;
    }

    public String getText() {
        return text;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
