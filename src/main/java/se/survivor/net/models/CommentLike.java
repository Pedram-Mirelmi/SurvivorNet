package se.survivor.net.models;


import jakarta.persistence.*;

@Entity
@Table(schema = "public")
public class CommentLike {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long likeId;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    private User user;

    @ManyToOne(targetEntity = Comment.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "commentId", referencedColumnName = "commentId")
    private Comment comment;

    @Column(nullable = false)
    private boolean isLike;

    public CommentLike() {
    }

    public CommentLike(User user, Comment comment, boolean isLike) {
        this.user = user;
        this.comment = comment;
        this.isLike = isLike;
    }

    public long getLikeId() {
        return likeId;
    }

    public User getUser() {
        return user;
    }

    public Comment getComment() {
        return comment;
    }

    public boolean isLike() {
        return isLike;
    }
}
