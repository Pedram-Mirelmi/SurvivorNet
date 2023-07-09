package se.survivor.net.models;


import jakarta.persistence.*;

@Entity
@Table(name = "commentLikes")
public class CommentLike {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long likeId;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(targetEntity = Comment.class, fetch = FetchType.LAZY)
    private Comment comment;

    private boolean likes;


    public CommentLike() {
    }

    public CommentLike(User user, Comment comment, boolean likes) {
        this.user = user;
        this.comment = comment;
        this.likes = likes;
    }

    public User getUser() {
        return user;
    }

    public Comment getComment() {
        return comment;
    }

    public boolean isLikes() {
        return likes;
    }
}
