package se.survivor.net.models;

import jakarta.persistence.*;

@Entity
@Table(name = "pictures", schema = "public")
public class Picture {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long pictureId;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "ownerId", referencedColumnName = "userId")
    private User owner;

    public Picture() {
    }

    public Picture(User owner) {
        this.owner = owner;
    }

    public Long getPictureId() {
        return pictureId;
    }

    public User getOwner() {
        return owner;
    }
}
