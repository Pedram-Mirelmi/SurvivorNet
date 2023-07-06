package se.survivor.net.models;

import com.google.gson.internal.LazilyParsedNumber;
import jakarta.persistence.*;

@Entity
@Table(name = "PostReactions")
public class PostReaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long reactionId;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    /**
     * 1: like
     * 2: dislike
     * 3: sad
     * 4: fire
     * 5: hand clap
     */
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
