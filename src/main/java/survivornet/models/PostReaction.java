package survivornet.models;


import jakarta.persistence.*;

@Entity
@Table(name = "post_reactions",
        schema = "public",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "post_id"}),
        indexes = {@Index(columnList = "post_id", name = "reaction_post_index")})
public class PostReaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long reactionId;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @Column(nullable = false)
    private int reactionType;

    @ManyToOne(targetEntity = Post.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", referencedColumnName = "post_id")
    private Post post;

    public PostReaction(User user, Post post, int reactionType) {
        this.user = user;
        this.reactionType = reactionType;
        this.post = post;
    }

    public PostReaction() {

    }

    public Long getReactionId() {
        return reactionId;
    }

    public User getUser() {
        return user;
    }

    public int getReactionType() {
        return reactionType;
    }

    public void setReactionType(int reactionType) {
        this.reactionType = reactionType;
    }

    public Post getPost() {
        return post;
    }
}
