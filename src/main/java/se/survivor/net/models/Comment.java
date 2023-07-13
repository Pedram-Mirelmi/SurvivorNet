package se.survivor.net.models;

import jakarta.persistence.*;

import java.sql.Date;

@Entity
@Table(name = "comments", schema = "public")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long commentId;

    @Column(nullable = false)
    private boolean isSolution;

    @ManyToOne(targetEntity = Comment.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "parentId", referencedColumnName = "commentId")
    private Comment parentComment;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    private User user;

    @ManyToOne(targetEntity = Post.class)
    @JoinColumn(name = "postId", referencedColumnName = "postId")
    private Post post;

    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private Date createdAt;

    public Comment(User user, Post post, String text, Date createdAt, Comment parentComment, boolean isSolution) {
        this.isSolution = isSolution;
        this.parentComment = parentComment;
        this.user = user;
        this.post = post;
        this.text = text;
        this.createdAt = createdAt;
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

    public Date getCreatedAt() {
        return createdAt;
    }
}
