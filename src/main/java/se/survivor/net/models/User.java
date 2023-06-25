package se.survivor.net.models;

import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

import java.sql.Date;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true, nullable = false)
    private String username;

    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private Date birthDate;

    @Column(nullable = false)
    private Date joinedAt;

    @Column(nullable = false)
    private String bio;

    @OneToOne
    @JoinColumn(name = "profilePicId", referencedColumnName = "pictureId")
    private Picture profilePic;

    @OneToOne
    @JoinColumn(name = "backgroundPicId")
    private Picture backgroundPic;

//    @OneToMany(fetch = FetchType.LAZY)
//    @JoinColumn(name = "postId")
//    private Set<Post> posts;

    @ManyToMany(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinTable(name = "follows",
                joinColumns = {@JoinColumn(referencedColumnName = "userId", name = "followerId")},
                inverseJoinColumns = {@JoinColumn(referencedColumnName = "userId", name = "followeeId")})
    private Set<User> followings;


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "follows",
            joinColumns = {@JoinColumn( referencedColumnName = "userId", name = "followeeId")},
            inverseJoinColumns = {@JoinColumn(referencedColumnName = "userId", name = "followerId")})
    private Set<User> followers;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "blocks",
            joinColumns = {@JoinColumn(referencedColumnName = "userId", name = "blockerId")},
            inverseJoinColumns = {@JoinColumn(referencedColumnName = "userId", name = "blockeeId")})
    private Set<User> blockList;


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "blocks",
            joinColumns = {@JoinColumn( referencedColumnName = "userId", name = "blockeeId")},
            inverseJoinColumns = {@JoinColumn(referencedColumnName = "userId", name = "blockerId")})
    private Set<User> blockedList;

    public User(String username, String password, String name, String email, Date birthDate, Date joinedAt, String bio, Picture profilePic, Picture backgroundPic) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.birthDate = birthDate;
        this.joinedAt = joinedAt;
        this.profilePic = profilePic;
        this.bio = bio;
        this.backgroundPic = backgroundPic;
    }

    public User() {
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Date getJoinedAt() {
        return joinedAt;
    }

    public Picture getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(Picture profilePic) {
        this.profilePic = profilePic;
    }

    public Picture getBackgroundPic() {
        return backgroundPic;
    }

    public void setBackgroundPic(Picture backgroundPic) {
        this.backgroundPic = backgroundPic;
    }

//    public Set<Post> getPosts() {
//        return posts;
//    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<User> getFollowings() {
        return followings;
    }

    public Set<User> getFollowers() {
        return followers;
    }

    public Set<User> getBlockList() {
        return blockList;
    }

    public Set<User> getBlockedList() {
        return blockedList;
    }
}
