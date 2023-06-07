package se.survivor.net.models;

import jakarta.persistence.*;

import java.sql.Date;

@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    private User user;

    @ManyToOne(targetEntity = Post.class)
    @JoinColumn(name = "postId", referencedColumnName = "postId")
    private Post post;

    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private Date created_at;

}
