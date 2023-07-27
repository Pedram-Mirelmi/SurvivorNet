package survivornet.DTO;

import survivornet.models.User;

import java.sql.Date;

public class UserDTO {
    private final long userId;
    private final String username;
    private final String name;
    private final String email;
    private final Date birthDate;
    private final Date joinedAt;
    private final String bio;
    private final long numberOfFollowers;
    private final long numberOfFollowings;
    private final long profilePicId;
    private final long backgroundPicId;


    public UserDTO(User user, long numberOfFollowers, long numberOfFollowings) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.name = user.getName();
        this.email = user.getEmail();
        this.birthDate = user.getBirthDate();
        this.joinedAt = user.getJoinedAt();
        this.bio = user.getBio();
        this.profilePicId = user.getProfilePic() == null ? 0 : user.getProfilePic().getPictureId();
        this.backgroundPicId = user.getBackgroundPic() == null ? 0 : user.getBackgroundPic().getPictureId();
        this.numberOfFollowers = numberOfFollowers;
        this.numberOfFollowings = numberOfFollowings;
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