package survivornet.services.domain;

import org.springframework.stereotype.Service;
import survivornet.DTO.UserDTO;
import survivornet.exceptions.UnauthorizedException;
import survivornet.models.User;
import survivornet.services.AuthorizationService;
import survivornet.services.db.UserDbService;

import java.sql.Date;
import java.util.List;

@Service
public class UserService {

    private final UserDbService userDbService;
    private final AuthorizationService authorizationService;

    public UserService(UserDbService userDbService,
                       AuthorizationService authorizationService) {
        this.userDbService = userDbService;
        this.authorizationService = authorizationService;
    }

    public UserDTO getUserDTOByUsername(String viewerUser, String username) throws UnauthorizedException {
        if(viewerUser != null && !authorizationService.canAccessProfile(viewerUser, username)) {
            throw new UnauthorizedException("User cannot see the other user's information");
        }
        return userDbService.getUserDtoByUsername(username);
    }

    public UserDTO getUserDTOByEmail(String viewerUsername, String email) throws UnauthorizedException {
        UserDTO user = userDbService.getUserDtoByEmail(email);
        if(viewerUsername != null && !authorizationService.canAccessProfile(viewerUsername, user.getUsername())) {
            throw new UnauthorizedException("User cannot see the other user's information");
        }
        return user;
    }

    public List<UserDTO> getUserFollowersDTO(String viewerUsername, String username, int chunk) throws UnauthorizedException {
        if(viewerUsername != null && !authorizationService.canViewFollowList(viewerUsername, username)) {
            throw new UnauthorizedException("User cannot access other user's any follow list");
        }
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
        return userDbService.getFollowings(username, chunk)
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

    public User getUserByEmail(String email) {
        return userDbService.getUserByEmail(email);
    }

    public UserDTO addUser(String username, String name, String password, String email, Date birthdate, String bio) {
        return new UserDTO(userDbService.addUser(username, name, password, email, birthdate, bio), 0, 0);
    }

    public boolean authenticateByPassword(String username, String password) {
        return userDbService.authenticateByPassword(username, password);
    }
}
