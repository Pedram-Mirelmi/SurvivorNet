package survivornet.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "comments",
        schema = "public",
        indexes = {@Index(columnList = "post_id", name = "comment_post_index")})
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "comment_id")
    private Long commentId;

    @Column(nullable = false, name = "is_solution")
    private boolean isSolution;

    @ManyToOne(targetEntity = Comment.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "parentId", referencedColumnName = "comment_id")
    private Comment parentComment;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @ManyToOne(targetEntity = Post.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", referencedColumnName = "post_id")
    private Post post;

    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "comment",
            cascade = {CascadeType.ALL},
            orphanRemoval = true)
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
