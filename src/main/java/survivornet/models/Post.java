package survivornet.models;


import jakarta.persistence.*;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "posts",
        schema = "public",
        indexes = {@Index(columnList = "user_id", name = "post_user_index")})
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "post_id")
    private Long postId;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String caption;

    @ManyToOne(targetEntity = Post.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "parentId", referencedColumnName = "post_id")
    private Post parent;

    @OneToMany(fetch = FetchType.EAGER,
            mappedBy = "post",
            cascade = {CascadeType.ALL},
            orphanRemoval = true)
    private List<Picture> pictures;

    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "post",
            cascade = {CascadeType.ALL},
            orphanRemoval = true)
    private List<Comment> comments;

    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "post",
            cascade = {CascadeType.ALL},
            orphanRemoval = true)
    private List<PostReaction> reactions;


    public Post() {
    }


    public Post(User user, String title, String caption, LocalDateTime createdAt, Post parent) {
        this.user = user;
        this.title = title;
        this.caption = caption;
        this.createdAt = createdAt;
        this.parent = parent;
    }

    @PrePersist
    public void prePersist() {
        if(createdAt == null)
            createdAt = LocalDateTime.now();
    }


    public Long getPostId() {
        return postId;
    }

    public User getUser() {
        return user;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getTitle() {
        return title;
    }

    public String getCaption() {
        return caption;
    }

    public Post getParent() {
        return parent;
    }

//    public List<Post> getChildren() {
//        return children;
//    }

    public List<Picture> getPictures() {
        return pictures;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public List<PostReaction> getReactions() {
        return reactions;
    }
}
