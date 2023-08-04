package survivornet.models;

import jakarta.persistence.*;

@Entity
@Table(name = "users_blocks",
        uniqueConstraints = @UniqueConstraint(columnNames = {"blocker_id", "blockee_id"}))
public class UserBlock {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocker_id", referencedColumnName = "user_id")
    private User blocker;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blockee_id", referencedColumnName = "user_id")
    private User blockee;

    public UserBlock(User blocker, User blockee) {
        this.blocker = blocker;
        this.blockee = blockee;
    }

    public UserBlock() {
    }
}
