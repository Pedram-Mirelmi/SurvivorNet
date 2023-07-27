package se.survivor.net.controllers;

import org.springframework.web.bind.annotation.*;
import se.survivor.net.DTO.PostDTO;
import se.survivor.net.DTO.UserDTO;
import se.survivor.net.exceptions.InvalidValueException;
import se.survivor.net.exceptions.UnauthorizedException;
import se.survivor.net.services.domain.PostService;
import se.survivor.net.services.domain.UserService;
import se.survivor.net.utils.JWTUtility;

import java.util.List;
import java.util.Map;

import static se.survivor.net.utils.Constants.*;

@RestController
@RequestMapping("api/users")
public class UserController {

    final private UserService userService;
    final private PostService postService;

    public UserController(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }


    @GetMapping("{username}")
    public UserDTO getUserBuyUsername(
            @RequestHeader(AUTHORIZATION) String jwtToken,
            @PathVariable(USERNAME) String username) throws UnauthorizedException {
        return userService.getUserDTOByUsername(
                JWTUtility.getUsernameFromToken(jwtToken),
                username);
    }

    @GetMapping("{username}/posts")
    public List<PostDTO> getUserProfile(
            @RequestHeader(AUTHORIZATION) String jwtToken,
            @PathVariable(USERNAME) String username,
            @RequestParam(CHUNK) int chunk) throws InvalidValueException, UnauthorizedException {
        return postService.getUserPosts(
            JWTUtility.getUsernameFromToken(jwtToken),
            username,
            chunk);
    }

    @GetMapping("{username}/followers")
    public List<UserDTO> getUserFollowers(
            @RequestHeader(AUTHORIZATION) String jwtToken,
            @PathVariable(USERNAME) String username,
            @RequestParam(CHUNK) int chunk) throws UnauthorizedException {
        return userService.getUserFollowersDTO(
            JWTUtility.getUsernameFromToken(jwtToken),
            username, chunk);
    }

    @GetMapping("{username}/followings")
    public List<UserDTO> getUserFollowings(
            @RequestHeader(AUTHORIZATION) String jwtToken,
            @PathVariable(USERNAME) String username,
            @RequestParam(CHUNK) int chunk) throws UnauthorizedException {
        return userService.getUserFollowingsDTO(
                JWTUtility.getUsernameFromToken(jwtToken),
                username,
                chunk);
    }

    @PostMapping("follow/{username}")
    public Map<String, Object> followUser(@PathVariable(USERNAME) String followee,
                                          @RequestHeader(AUTHORIZATION) String jwtToken) {
        String username = JWTUtility.getUsernameFromToken(jwtToken);
        boolean success = userService.addFollow(username, followee);
        return Map.of(STATUS, success ? SUCCESS : FAIL);
    }

    @DeleteMapping("follow/{username}")
    public Map<String, Object> unfollowUser(@PathVariable(USERNAME) String followee,
                                          @RequestHeader(AUTHORIZATION) String jwtToken) {
        String username = JWTUtility.getUsernameFromToken(jwtToken);
        boolean success = userService.removeFollow(username, followee);
        return Map.of(STATUS, success ? SUCCESS : FAIL);
    }

    @PostMapping("block/{username}")
    public Map<String, Object> block(@PathVariable(USERNAME) String blockee,
                                          @RequestHeader(AUTHORIZATION) String jwtToken) {
        String username = JWTUtility.getUsernameFromToken(jwtToken);
        boolean success = userService.addBlock(username, blockee);
        return Map.of(STATUS, success ? SUCCESS : FAIL);
    }

    @DeleteMapping("block/{username}")
    public Map<String, Object> unblock(@PathVariable(USERNAME) String blockee,
                                            @RequestHeader(AUTHORIZATION) String jwtToken) {
        String username = JWTUtility.getUsernameFromToken(jwtToken);
        boolean success = userService.removeBlock(username, blockee);
        return Map.of(STATUS, success ? SUCCESS : FAIL);
    }
    @GetMapping("search")
    public List<UserDTO> searchUsers(@RequestParam(QUERY) String query,
                                     @RequestParam(CHUNK) int chunk) {
        return userService.searchUsers(query, chunk);
    }
}
