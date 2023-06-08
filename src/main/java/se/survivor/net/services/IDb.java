package se.survivor.net.services;

import org.jetbrains.annotations.NotNull;
import se.survivor.net.models.Post;
import se.survivor.net.models.User;

import java.sql.Date;
import java.util.List;

public interface IDb {
    User getUserById(Long userId);

    void addUser(@NotNull String username,
                 @NotNull String name,
                 String password,
                 @NotNull String email,
                 Date birthDate,
                 @NotNull String bio);

    User getUserByEmail(String email);

    User getUserByUsername(String username);

    boolean authenticate(String username, String password);

    List<Post> getUserPosts(Long userId);

    List<User> getFollowers(Long userId);

    List<User> getFollowings(Long userId);

    List<User> searchUsers(String query);

    boolean follow(String followerUsername, Long followeeId);

    boolean unfollow(String unfollowerUsername, Long unfolloweeId);

    boolean block(String blockerUsername, Long blockeeId);

    boolean unblock(String unblockerUsername, Long unblockeeId);
}
