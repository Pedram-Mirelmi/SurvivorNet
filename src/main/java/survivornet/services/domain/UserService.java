package survivornet.services.domain;

import org.springframework.stereotype.Service;
import survivornet.DTO.UserDTO;
import survivornet.exceptions.UnauthorizedException;
import survivornet.services.AuthorizationService;
import survivornet.services.db.UserDbService;

import java.util.List;

@Service
public class UserService {

    private final UserDbService userDbService;
    private final AuthorizationService authorizationService;

    public UserService(UserDbService userDbService, AuthorizationService authorizationService) {
        this.userDbService = userDbService;
        this.authorizationService = authorizationService;
    }

    public UserDTO getUserDTOByUsername(String viewerUser, String username) throws UnauthorizedException {
        if(!authorizationService.canAccessProfile(viewerUser, username)) {
            throw new UnauthorizedException("User cannot see the other user's information");
        }
        // TODO fix number of followers
        return new UserDTO(userDbService.getUserByUsername(username), 0, 0);
    }

    public UserDTO getUserDTOByEmail(String viewerUsername, String email) throws UnauthorizedException {
        // TODO fix number of followers
        UserDTO targetUser = new UserDTO(userDbService.getUserByEmail(email), 0, 0);
        if(!authorizationService.canAccessProfile(viewerUsername, targetUser.getUsername())) {
            throw new UnauthorizedException("User cannot access other user's profile information");
        }
        return targetUser;
    }

    public List<UserDTO> getUserFollowersDTO(String viewerUsername, String username, int chunk) throws UnauthorizedException {
        if(!authorizationService.canViewFollowList(viewerUsername, username)) {
            throw new UnauthorizedException("User cannot access other user's any follow list");
        }
        // TODO fix number of followers
        return userDbService.getFollowers(username, chunk)
                .stream()
                .map(user -> new UserDTO(user, 0, 0))
                .toList();
    }

    public List<UserDTO> getUserFollowingsDTO(String viewerUsername, String username, int chunk) throws UnauthorizedException {
        if(!authorizationService.canViewFollowList(viewerUsername, username)) {
            throw new UnauthorizedException("User cannot access other user's any follow list");
        }
        // TODO fix number of followers
        return userDbService.getFollowings(username, 0)
                .stream()
                .map(user -> new UserDTO(user, 0, 0))
                .toList();
    }

    public boolean addFollow(String follower, String followee) {
        try {
            userDbService.changeFollow(follower, followee, true);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    public boolean removeFollow(String follower, String followee) {
        try {
            userDbService.changeFollow(follower, followee, false);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    public boolean addBlock(String blocker, String blockee) {
        try {
            userDbService.changeFollow(blocker, blockee, true);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    public boolean removeBlock(String blocker, String blockee) {
        try {
            userDbService.changeFollow(blocker, blockee, false);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    public List<UserDTO> searchUsers(String query, int chunk) {
        return userDbService.searchUsers(query, chunk).stream().map(user -> new UserDTO(user, 0, 0)).toList();
    }
}