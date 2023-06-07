package se.survivor.net.models;


import jakarta.persistence.*;

import java.sql.Date;

@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long postId;

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    private User user;

    @Column(nullable = false)
    private Date created_at;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String caption;

    @ManyToOne(targetEntity = Post.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "parentId", referencedColumnName = "postId")
    private Post parent;

    @OneToOne
    @JoinColumn(name = "pictureId", referencedColumnName = "pictureId")
    private Picture picture;


    public Post() {
    }
}
