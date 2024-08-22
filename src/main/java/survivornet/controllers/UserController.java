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

import java.sql.Date;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    @PutMapping("{username}")
    public UserDTO updateProfile(
            @PathVariable(Constants.USERNAME) String oldUsername,
            @RequestHeader(Constants.AUTHORIZATION) String jwtToken,
            @RequestBody Map<String, String> body) throws UnauthorizedException, InvalidValueException {
        if(oldUsername.equals(JWTUtility.getUsernameFromToken(jwtToken))) {
            try {
                return userService.updateProfile(
                    oldUsername,
                    Objects.requireNonNull(body.get(Constants.FIRSTNAME)),
                    Objects.requireNonNull(body.get(Constants.LASTNAME)),
                    Objects.requireNonNull(body.get(Constants.USERNAME)),
                    Objects.requireNonNull(body.get(Constants.PASSWORD)),
                    Objects.requireNonNull(body.get(Constants.EMAIL)),
                    Date.valueOf(body.get(Constants.BIRTHDATE))
                );

            } catch (SQLIntegrityConstraintViolationException e) {
                throw new InvalidValueException("Couldn't update");
            }
        } else {
            throw new UnauthorizedException("Unauthorized access to other user's profile");
        }
    }

    @GetMapping("{username}/posts")
    public List<PostDTO> getUserPosts(
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

    @PostMapping("follow")
    public Map<String, Object> followUser(@RequestParam(Constants.USERNAME) String followee,
                                          @RequestHeader(Constants.AUTHORIZATION) String jwtToken) {
        String username = JWTUtility.getUsernameFromToken(jwtToken);
        boolean success = userService.changeFollow(username, followee, true);
        return Map.of(Constants.STATUS, success ? Constants.SUCCESS : Constants.FAIL);
    }

    @DeleteMapping("follow")
    public Map<String, Object> unfollowUser(@RequestParam(Constants.USERNAME) String followee,
                                            @RequestHeader(Constants.AUTHORIZATION) String jwtToken) {
        String username = JWTUtility.getUsernameFromToken(jwtToken);
        boolean success = userService.changeBlock(username, followee, false);
        return Map.of(Constants.STATUS, success ? Constants.SUCCESS : Constants.FAIL);
    }

    @PostMapping("block")
    public Map<String, Object> block(@RequestParam(Constants.USERNAME) String blockee,
                                     @RequestHeader(Constants.AUTHORIZATION) String jwtToken) {
        String username = JWTUtility.getUsernameFromToken(jwtToken);
        boolean success = userService.changeBlock(username, blockee, true);
        return Map.of(Constants.STATUS, success ? Constants.SUCCESS : Constants.FAIL);
    }

    @DeleteMapping("block")
    public Map<String, Object> unblock(@RequestParam(Constants.USERNAME) String blockee,
                                       @RequestHeader(Constants.AUTHORIZATION) String jwtToken) {
        String username = JWTUtility.getUsernameFromToken(jwtToken);
        boolean success = userService.changeBlock(username, blockee, true);
        return Map.of(Constants.STATUS, success ? Constants.SUCCESS : Constants.FAIL);
    }
    @GetMapping("search")
    public List<UserDTO> searchUsers(@RequestParam(Constants.QUERY) String query,
                                     @RequestParam(Constants.CHUNK) int chunk) {
        return userService.searchUsers(query, chunk);
    }
}
