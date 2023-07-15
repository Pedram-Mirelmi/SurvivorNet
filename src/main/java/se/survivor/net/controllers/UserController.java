package se.survivor.net.controllers;

import static se.survivor.net.utils.Constants.*;

import org.springframework.web.bind.annotation.*;
import se.survivor.net.DTO.PostDTO;
import se.survivor.net.DTO.UserDTO;
import se.survivor.net.exceptions.InvalidValueException;
import se.survivor.net.services.PostService;
import se.survivor.net.services.UserService;
import se.survivor.net.utils.JWTUtility;

import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    final private UserService userService;
    final private PostService postService;

    public UserController(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }


    @GetMapping("api/users/{username}")
    public UserDTO getUserBuyUsername(
            @PathVariable(USERNAME) String username,
            @RequestHeader(AUTHORIZATION) String jwtToken) {
        return userService.getUserDTOByUsername(username);
    }

    @GetMapping("api/users/{username}/profile")
    public Map<String, Object> getUserProfile(
            @RequestHeader(AUTHORIZATION) String jwtToken,
            @PathVariable(USERNAME) String username,
            @RequestParam int chunk) throws InvalidValueException {
        return Map.of(USER, userService.getUserDTOByUsername(username),
                POSTS, postService.getUserPosts(username, chunk));
    }

    @GetMapping("api/users/{username}/followers")
    public Map<String, Object> getUserFollowers(
            @PathVariable(USERNAME) String username) {
        return Map.of(USER, userService.getUserDTOByUsername(username),
                FOLLOWERS, userService.getUserFollowersDTO(username));
    }

    @GetMapping("api/users/{username}/followings")
    public Map<String, Object> getUserFollowings(@PathVariable(USERNAME) String username) {
        return Map.of(USERNAME, username,
                FOLLOWINGS, userService.getUserFollowingsDTO(username));
    }

    @PostMapping("api/users/follow/{username}")
    public Map<String, Object> followUser(@PathVariable(USERNAME) String followee,
                                          @RequestHeader(AUTHORIZATION) String jwtToken) {
        String username = JWTUtility.getUsernameFromToken(jwtToken);
        boolean success = userService.addFollow(username, followee);
        return Map.of(STATUS, success ? SUCCESS : FAIL);
    }

    @DeleteMapping("api/users/follow/{username}")
    public Map<String, Object> unfollowUser(@PathVariable(USERNAME) String followee,
                                          @RequestHeader(AUTHORIZATION) String jwtToken) {
        String username = JWTUtility.getUsernameFromToken(jwtToken);
        boolean success = userService.removeFollow(username, followee);
        return Map.of(STATUS, success ? SUCCESS : FAIL);
    }

    @PostMapping("api/users/block/{username}")
    public Map<String, Object> block(@PathVariable(USERNAME) String blockee,
                                          @RequestHeader(AUTHORIZATION) String jwtToken) {
        String username = JWTUtility.getUsernameFromToken(jwtToken);
        boolean success = userService.addBlock(username, blockee);
        return Map.of(STATUS, success ? SUCCESS : FAIL);
    }

    @DeleteMapping("api/users/block/{username}")
    public Map<String, Object> unblock(@PathVariable(USERNAME) String blockee,
                                            @RequestHeader(AUTHORIZATION) String jwtToken) {
        String username = JWTUtility.getUsernameFromToken(jwtToken);
        boolean success = userService.removeBlock(username, blockee);
        return Map.of(STATUS, success ? SUCCESS : FAIL);
    }
    @GetMapping("api/users/search")
    public List<UserDTO> searchUsers(@RequestParam(QUERY) String query) {
        return userService.searchUsers(query);
    }


}
