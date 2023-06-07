package se.survivor.net.DTO;

import se.survivor.net.models.User;

import java.sql.Date;

public class UserDTO {
    private final long userId;
    private final String username;
    private final String name;
    private final String email;
    private final Date birthDate;
    private final Date joinedAt;
    private final String bio;
    private final long profilePicId;
    private final long backgroundPicId;

    public UserDTO(long userId, String username, String name, String email, Date birthDate, Date joinedAt, String bio, long profilePicId, long backgroundPicId) {
        this.userId = userId;
        this.username = username;
        this.name = name;
        this.email = email;
        this.birthDate = birthDate;
        this.joinedAt = joinedAt;
        this.bio = bio;
        this.profilePicId = profilePicId;
        this.backgroundPicId = backgroundPicId;
    }

    public UserDTO(User user) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.name = user.getName();
        this.email = user.getEmail();
        this.birthDate = user.getBirthDate();
        this.joinedAt = user.getJoinedAt();
        this.bio = user.getBio();
        this.profilePicId = user.getProfilePic().getPictureId();
        this.backgroundPicId = user.getBackgroundPic().getPictureId();
    }

    public long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public Date getJoinedAt() {
        return joinedAt;
    }

    public String getBio() {
        return bio;
    }

    public long getProfilePicId() {
        return profilePicId;
    }

    public long getBackgroundPicId() {
        return backgroundPicId;
    }
}
