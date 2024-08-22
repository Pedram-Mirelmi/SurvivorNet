package survivornet.models;

import jakarta.persistence.*;

import java.sql.Date;
import java.time.LocalDate;
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
    private String firstname;

    @Column(nullable = false)
    private String lastname;

    @Column(nullable = false, unique = true)
    private String email;

    private Date birthdate;

    @Column(name = "joined_at", nullable = false)
    private Date joinedAt;

    @Column(nullable = false)
    private String bio;

    @OneToOne(targetEntity = Picture.class,
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JoinColumn(name = "profile_pic_id",
            referencedColumnName = "picture_id")
    private Picture profilePic;

    @OneToOne(targetEntity = Picture.class,
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JoinColumn(name = "background_pic_id",
            referencedColumnName = "picture_id")
    private Picture backgroundPic;

    @OneToMany(targetEntity = Picture.class,
            mappedBy = "owner",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.ALL},
            orphanRemoval = true)
    private List<Picture> pictures;

    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "user",
            cascade = {CascadeType.ALL},
            orphanRemoval = true)
    private List<Post> posts;

    @OneToMany(fetch = FetchType.LAZY,
                mappedBy = "follower",
                cascade = {CascadeType.ALL},
                orphanRemoval = true)
    private List<UserFollow> followings;

    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "followee",
            cascade = {CascadeType.ALL},
            orphanRemoval = true)
    private List<UserFollow> followers;

    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "blocker",
            cascade = {CascadeType.ALL},
            orphanRemoval = true)
    private List<UserBlock> blockees;


    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "blockee",
            cascade = {CascadeType.ALL},
            orphanRemoval = true)
    private List<UserBlock> blockers;

    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "user",
            cascade = {CascadeType.ALL},
            orphanRemoval = true)
    private List<PostReaction> reactions;


    public User(String username, String password, String firstname, String lastname, String email, Date birthdate, Date joinedAt, String bio, Picture profilePic, Picture backgroundPic) {
        this.username = username;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.birthdate = birthdate;
        this.joinedAt = joinedAt;
        this.profilePic = profilePic;
        this.bio = bio;
        this.backgroundPic = backgroundPic;
    }

    public User() {
    }

    @PrePersist
    public void prePersist() {
        if(joinedAt == null)
            joinedAt = Date.valueOf(LocalDate.now());
    }

    public Long getUserId() {
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

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
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

//    public List<User> getFollowings() {
//        return followings;
//    }
//
//    public List<User> getFollowers() {
//        return followers;
//    }
//
//    public List<Post> getPosts() {
//        return posts;
//    }

    public List<Picture> getPictures() {
        return pictures;
    }

//    public List<User> getBlockList() {
//        return blockList;
//    }
}
