package survivornet.controllers;

import org.springframework.web.bind.annotation.*;
import survivornet.DTO.PostDTO;
import survivornet.DTO.UserDTO;
import survivornet.exceptions.InvalidValueException;
import survivornet.exceptions.UnauthorizedException;
import survivornet.services.domain.PostService;
import survivornet.services.domain.UserService;
import survivornet.utils.JWTUtility;
import survivornet.utils.Constants;

import java.util.List;
import java.util.Map;

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
            @RequestHeader(Constants.AUTHORIZATION) String jwtToken,
            @PathVariable(Constants.USERNAME) String username) throws UnauthorizedException {
        return userService.getUserDTOByUsername(
                JWTUtility.getUsernameFromToken(jwtToken),
                username);
    }

    @GetMapping("{username}/posts")
    public List<PostDTO> getUserProfile(
            @RequestHeader(Constants.AUTHORIZATION) String jwtToken,
            @PathVariable(Constants.USERNAME) String username,
            @RequestParam(Constants.CHUNK) int chunk) throws InvalidValueException, UnauthorizedException {
        return postService.getUserPosts(
            JWTUtility.getUsernameFromToken(jwtToken),
            username,
            chunk);
    }

    @GetMapping("{username}/followers")
    public List<UserDTO> getUserFollowers(
            @RequestHeader(Constants.AUTHORIZATION) String jwtToken,
            @PathVariable(Constants.USERNAME) String username,
            @RequestParam(Constants.CHUNK) int chunk) throws UnauthorizedException {
        return userService.getUserFollowersDTO(
            JWTUtility.getUsernameFromToken(jwtToken),
            username, chunk);
    }

    @GetMapping("{username}/followings")
    public List<UserDTO> getUserFollowings(
            @RequestHeader(Constants.AUTHORIZATION) String jwtToken,
            @PathVariable(Constants.USERNAME) String username,
            @RequestParam(Constants.CHUNK) int chunk) throws UnauthorizedException {
        return userService.getUserFollowingsDTO(
                JWTUtility.getUsernameFromToken(jwtToken),
                username,
                chunk);
    }

    @PostMapping("follow/{username}")
    public Map<String, Object> followUser(@PathVariable(Constants.USERNAME) String followee,
                                          @RequestHeader(Constants.AUTHORIZATION) String jwtToken) {
        String username = JWTUtility.getUsernameFromToken(jwtToken);
        boolean success = userService.addFollow(username, followee);
        return Map.of(Constants.STATUS, success ? Constants.SUCCESS : Constants.FAIL);
    }

    @DeleteMapping("follow/{username}")
    public Map<String, Object> unfollowUser(@PathVariable(Constants.USERNAME) String followee,
                                          @RequestHeader(Constants.AUTHORIZATION) String jwtToken) {
        String username = JWTUtility.getUsernameFromToken(jwtToken);
        boolean success = userService.removeFollow(username, followee);
        return Map.of(Constants.STATUS, success ? Constants.SUCCESS : Constants.FAIL);
    }

    @PostMapping("block/{username}")
    public Map<String, Object> block(@PathVariable(Constants.USERNAME) String blockee,
                                          @RequestHeader(Constants.AUTHORIZATION) String jwtToken) {
        String username = JWTUtility.getUsernameFromToken(jwtToken);
        boolean success = userService.addBlock(username, blockee);
        return Map.of(Constants.STATUS, success ? Constants.SUCCESS : Constants.FAIL);
    }

    @DeleteMapping("block/{username}")
    public Map<String, Object> unblock(@PathVariable(Constants.USERNAME) String blockee,
                                            @RequestHeader(Constants.AUTHORIZATION) String jwtToken) {
        String username = JWTUtility.getUsernameFromToken(jwtToken);
        boolean success = userService.removeBlock(username, blockee);
        return Map.of(Constants.STATUS, success ? Constants.SUCCESS : Constants.FAIL);
    }
    @GetMapping("search")
    public List<UserDTO> searchUsers(@RequestParam(Constants.QUERY) String query,
                                     @RequestParam(Constants.CHUNK) int chunk) {
        return userService.searchUsers(query, chunk);
    }
}
