package se.survivor.net.models;

import jakarta.persistence.*;

@Entity
@Table(name = "pictures")
public class Picture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long pictureId;

    public Long getPictureId() {
        return pictureId;
    }
}
