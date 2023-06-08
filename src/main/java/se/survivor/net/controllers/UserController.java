package se.survivor.net.controllers;

import static se.survivor.net.utils.Constants.*;

import org.springframework.web.bind.annotation.*;
import se.survivor.net.DTO.UserDTO;
import se.survivor.net.models.Post;
import se.survivor.net.models.User;
import se.survivor.net.services.DBService;
import se.survivor.net.services.IDb;

import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    final private IDb dbService;

    public UserController(IDb dbService) {
        this.dbService = dbService;
    }


    @GetMapping("api/users/{userId}")
    public UserDTO getUserBuyId(@PathVariable(USER_ID) Long userId, @RequestHeader(AUTHORIZATION) String jwtToken) {
        return new UserDTO(dbService.getUserById(userId));
    }

    @GetMapping("api/users/{userId}/profile")
    public Map<String, Object> getUserProfile(@PathVariable(USER_ID) Long userId, @RequestHeader(AUTHORIZATION) String jwtToken) {
        List<Post> posts = dbService.getUserPosts(userId);
        return Map.of(USER_ID, userId,
                POSTS, posts);
    }

    @GetMapping("api/users/{userId}/followers")
    public Map<String, Object> getUserFollowers(@PathVariable(USER_ID) Long userId) {
        return Map.of(USER_ID, userId,
                FOLLOWERS, dbService.getFollowers(userId).stream().map(UserDTO::new));
    }

    @GetMapping("api/users/{userId}/followings")
    public Map<String, Object> getUserFollowings(@PathVariable(USER_ID) Long userId) {
        return Map.of(USER_ID, userId,
                FOLLOWINGS, dbService.getFollowings(userId).stream().map(UserDTO::new));
    }

    @GetMapping("api/users/search")
    public List<UserDTO> searchUsers(@RequestParam(QUERY) String query) {
        return dbService.searchUsers(query).stream().map(UserDTO::new).toList();
    }
}
