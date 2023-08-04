package survivornet.models;

import jakarta.persistence.*;

@Entity
@Table(name = "users_follows",
        uniqueConstraints = @UniqueConstraint(columnNames = {"follower_id", "followee_id"}))
public class UserFollow {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", referencedColumnName = "user_id")
    private User follower;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followee_id", referencedColumnName = "user_id")
    private User followee;

    public UserFollow(User follower, User followee) {
        this.follower = follower;
        this.followee = followee;
    }

    public UserFollow() {
    }

    public User getFollower() {
        return follower;
    }

    public User getFollowee() {
        return followee;
    }
}
