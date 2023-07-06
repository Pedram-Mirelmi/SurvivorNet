package se.survivor.net.controllers;

import static se.survivor.net.utils.Constants.*;

import org.springframework.web.bind.annotation.*;
import se.survivor.net.DTO.PostDTO;
import se.survivor.net.DTO.UserDTO;
import se.survivor.net.services.DbService;
import se.survivor.net.services.PostService;
import se.survivor.net.utils.JWTUtility;

import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    final private DbService dbService;
    final private PostService postService;

    public UserController(DbService dbService, PostService postService) {
        this.dbService = dbService;
        this.postService = postService;
    }


    @GetMapping("api/users/{userId}")
    public UserDTO getUserBuyId(@PathVariable(USER_ID) Long userId, @RequestHeader(AUTHORIZATION) String jwtToken) {
        return new UserDTO(dbService.getUserById(userId));
    }

    @GetMapping("api/users/{userId}/profile")
    public Map<String, Object> getUserProfile(
        @RequestHeader(AUTHORIZATION) String jwtToken,
        @PathVariable(USER_ID) Long userId,
        @RequestParam int chunk) {
        List<PostDTO> posts = postService.getUserPosts(userId, chunk);
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

    @PostMapping("api/users/follow/{userId}")
    public Map<String, Object> followUser(@PathVariable(USER_ID) Long userid,
                                          @RequestHeader(AUTHORIZATION) String jwtToken) {
        String username = JWTUtility.getUsernameFromToken(jwtToken);
        boolean result = dbService.follow(username, userid);
        return Map.of(STATUS, result ? SUCCESS : FAIL);
    }

    @DeleteMapping("api/users/follow/{userId}")
    public Map<String, Object> unfollowUser(@PathVariable(USER_ID) Long userid,
                                          @RequestHeader(AUTHORIZATION) String jwtToken) {
        String username = JWTUtility.getUsernameFromToken(jwtToken);
        boolean result = dbService.unfollow(username, userid);
        return Map.of(STATUS, result ? SUCCESS : FAIL);
    }

    @PostMapping("api/users/block/{userId}")
    public Map<String, Object> block(@PathVariable(USER_ID) Long userid,
                                          @RequestHeader(AUTHORIZATION) String jwtToken) {
        String username = JWTUtility.getUsernameFromToken(jwtToken);
        boolean result = dbService.block(username, userid);
        return Map.of(STATUS, result ? SUCCESS : FAIL);
    }

    @DeleteMapping("api/users/block/{userId}")
    public Map<String, Object> unblock(@PathVariable(USER_ID) Long userid,
                                            @RequestHeader(AUTHORIZATION) String jwtToken) {
        String username = JWTUtility.getUsernameFromToken(jwtToken);
        boolean result = dbService.unblock(username, userid);
        return Map.of(STATUS, result ? SUCCESS : FAIL);
    }
    @GetMapping("api/users/search")
    public List<UserDTO> searchUsers(@RequestParam(QUERY) String query) {
        return dbService.searchUsers(query).stream().map(UserDTO::new).toList();
    }


}
