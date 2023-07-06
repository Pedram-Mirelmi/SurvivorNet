package se.survivor.net.models;


import jakarta.persistence.*;
import se.survivor.net.services.CommentService;

import java.sql.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long postId;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
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
    private List<Post> children;

    @OneToMany(targetEntity = Picture.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "postId", referencedColumnName = "pictureId")
    private List<Picture> pictures;

    @OneToMany(targetEntity = Comment.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "postId", referencedColumnName = "postId")
    private List<Comment> comments;

    @OneToMany(targetEntity = PostReaction.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "postId", referencedColumnName = "postId")
    private List<PostReaction> reactions;


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

    public List<Post> getChildren() {
        return children;
    }

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
