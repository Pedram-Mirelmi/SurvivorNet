package se.survivor.net.models;


import jakarta.persistence.*;

import java.sql.Date;
import java.util.Set;

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

    @OneToMany(targetEntity = Post.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "postId", referencedColumnName = "parentId")
    private Set<Post> children;

    @OneToMany(targetEntity = Picture.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "postId", referencedColumnName = "pictureId")
    private Set<Picture> pictures;


    public Post() {
    }


    public Post(User user, String title, String caption, Post parent) {
        this.user = user;
        this.title = title;
        this.caption = caption;
        this.parent = parent;
    }

    public long getPostId() {
        return postId;
    }

    public User getUser() {
        return user;
    }

    public Date getCreated_at() {
        return created_at;
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

    public Set<Post> getChildren() {
        return children;
    }

    public Set<Picture> getPictures() {
        return pictures;
    }
}
