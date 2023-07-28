package survivornet.models;

import jakarta.persistence.*;

@Entity
@Table(name = "pictures", schema = "public")
public class Picture {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "picture_id")
    private long pictureId;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", referencedColumnName = "user_id")
    private User owner;

    @ManyToOne(targetEntity = Post.class, fetch = FetchType.LAZY)
    @JoinColumn(nullable = true, name = "post_id", referencedColumnName = "post_id")
    private Post post;

    public Picture() {
    }

    public Picture(User owner, Post post) {
        this.owner = owner;
        this.post = post;
    }

    public Long getPictureId() {
        return pictureId;
    }

    public User getOwner() {
        return owner;
    }
}
