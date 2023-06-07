package se.survivor.net.models;

import jakarta.persistence.*;

import java.sql.Date;
import java.util.Set;

@Entity
@Table(name = "solutions")
public class Solution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long solutionId;

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

    @OneToMany(targetEntity = SolutionVote.class)
    @JoinColumn(name = "solutionId", referencedColumnName = "solutionId")
    private Set<SolutionVote> votes;


    public Solution() {}

    public Solution(User user, Post post, String text, Date created_at) {
        this.user = user;
        this.post = post;
        this.text = text;
        this.created_at = created_at;
    }

    public Long getSolutionId() {
        return solutionId;
    }

    public User getUser() {
        return user;
    }

    public Post getPost() {
        return post;
    }

    public String getText() {
        return text;
    }


    public Date getCreated_at() {
        return created_at;
    }

}
