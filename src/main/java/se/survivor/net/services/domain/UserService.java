package se.survivor.net.services.domain;

import org.springframework.stereotype.Service;
import se.survivor.net.DTO.UserDTO;
import se.survivor.net.exceptions.UnauthorizedException;
import se.survivor.net.services.AuthorizationService;
import se.survivor.net.services.db.UserDbService;

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
        return new UserDTO(userDbService.getUserByUsername(username));
    }

    public UserDTO getUserDTOByEmail(String viewerUsername, String email) throws UnauthorizedException {
        UserDTO targetUser = new UserDTO(userDbService.getUserByEmail(email));
        if(!authorizationService.canAccessProfile(viewerUsername, targetUser.getUsername())) {
            throw new UnauthorizedException("User cannot access other user's profile information");
        }
        return targetUser;
    }

    public List<UserDTO> getUserFollowersDTO(String viewerUsername, String username) throws UnauthorizedException {
        if(!authorizationService.canViewFollowList(viewerUsername, username)) {
            throw new UnauthorizedException("User cannot access other user's any follow list");
        }
        return userDbService.getFollowers(username)
                .stream()
                .map(UserDTO::new)
                .toList();
    }

    public List<UserDTO> getUserFollowingsDTO(String viewerUsername, String username) throws UnauthorizedException {
        if(!authorizationService.canViewFollowList(viewerUsername, username)) {
            throw new UnauthorizedException("User cannot access other user's any follow list");
        }

        return userDbService.getFollowings(username)
                .stream()
                .map(UserDTO::new)
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

    public List<UserDTO> searchUsers(String query) {
        return userDbService.searchUsers(query).stream().map(UserDTO::new).toList();
    }
}
