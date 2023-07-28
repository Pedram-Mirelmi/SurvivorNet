package survivornet.models;


import jakarta.persistence.*;

@Entity
@Table(name = "PostReactions", schema = "public")
public class PostReaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long reactionId;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @Column(nullable = false)
    private int reactionType;

    @ManyToOne(targetEntity = Post.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", referencedColumnName = "post_id")
    private Post post;

    public PostReaction(User user, int reactionType, Post post) {
        this.user = user;
        this.reactionType = reactionType;
        this.post = post;
    }

    public PostReaction() {

    }

    public long getReactionId() {
        return reactionId;
    }

    public User getUser() {
        return user;
    }

    public int getReactionType() {
        return reactionType;
    }

    public Post getPost() {
        return post;
    }
}
