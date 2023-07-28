package survivornet.models;

import jakarta.persistence.*;

import java.sql.Date;
import java.util.List;

@Entity
@Table(name = "users", schema = "public", indexes = {@Index(columnList = "username" , name = "username_index")})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private Long userId;

    @Column(unique = true, nullable = false)
    private String username;

    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private Date birthdate;

    @Column(name = "joined_at", nullable = false)
    private Date joinedAt;

    @Column(nullable = false)
    private String bio;

    @OneToOne(targetEntity = Picture.class,
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_pic_id",
            referencedColumnName = "picture_id")
    private Picture profilePic;

    @OneToOne(targetEntity = Picture.class,
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    @JoinColumn(name = "background_pic_id",
            referencedColumnName = "picture_id")
    private Picture backgroundPic;

    @OneToMany(targetEntity = Picture.class,
            mappedBy = "owner",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<Picture> pictures;

    @ManyToMany(targetEntity = User.class,
            fetch = FetchType.LAZY)
    @JoinTable(name = "follows",
                joinColumns = {@JoinColumn(referencedColumnName = "user_id",
                        name = "follower_id")},
                inverseJoinColumns = {@JoinColumn(referencedColumnName = "user_id",
                        name = "followee_id")})
    private List<User> followings;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "follows",
            joinColumns = {@JoinColumn( referencedColumnName = "user_id",
                    name = "followee_id")},
            inverseJoinColumns = {@JoinColumn(referencedColumnName = "user_id",
                    name = "follower_id")})
    private List<User> followers;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "blocks",
            joinColumns = {@JoinColumn(referencedColumnName = "user_id",
                    name = "blocker_id")},
            inverseJoinColumns = {@JoinColumn(referencedColumnName = "user_id",
                    name = "blockee_id")})
    private List<User> blockList;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    @JoinColumn(name = "userId", referencedColumnName = "userId")
    private List<Post> posts;

    public User(String username, String password, String name, String email, Date birthdate, Date joinedAt, String bio, Picture profilePic, Picture backgroundPic) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.birthdate = birthdate;
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

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
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

    public List<User> getFollowings() {
        return followings;
    }

    public List<User> getFollowers() {
        return followers;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public List<Picture> getPictures() {
        return pictures;
    }

    public List<User> getBlockList() {
        return blockList;
    }
}
