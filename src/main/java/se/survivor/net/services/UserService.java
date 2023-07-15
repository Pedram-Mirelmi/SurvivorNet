package se.survivor.net.services;

import org.springframework.stereotype.Service;
import se.survivor.net.DTO.UserDTO;
import se.survivor.net.models.Picture;
import se.survivor.net.models.User;

import java.util.List;

@Service
public class UserService {

    private DbService dbService;

    public UserService(DbService dbService) {
        this.dbService = dbService;
    }

    public UserDTO getUserById(Long userId) {
        return new UserDTO(dbService.getUserById(userId));
    }

    public UserDTO getUserDTOById(Long userId) {
        return new UserDTO(dbService.getUserById(userId));
    }

    public UserDTO getUserDTOByUsername(String username) {
        return new UserDTO(dbService.getUserByUsername(username));
    }

    public UserDTO getUserDTOByEmail(String email) {
        return new UserDTO(dbService.getUserByEmail(email));
    }

    public List<UserDTO> getUserFollowersDTO(String username) {
        return dbService.getFollowers(username)
                .stream()
                .map(UserDTO::new)
                .toList();
    }

    public List<UserDTO> getUserFollowingsDTO(String username) {
        return dbService.getFollowings(username)
                .stream()
                .map(UserDTO::new)
                .toList();
    }

    public boolean addFollow(String follower, String followee) {
        try {
            dbService.changeFollow(follower, followee, true);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    public boolean removeFollow(String follower, String followee) {
        try {
            dbService.changeFollow(follower, followee, false);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    public boolean addBlock(String blocker, String blockee) {
        try {
            dbService.changeFollow(blocker, blockee, true);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    public boolean removeBlock(String blocker, String blockee) {
        try {
            dbService.changeFollow(blocker, blockee, false);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    public List<UserDTO> searchUsers(String query) {
        return dbService.searchUsers(query).stream().map(UserDTO::new).toList();
    }

    public Picture addProfilePicture(String username) {
        return dbService.addPictureForProfile(username);
    }

    public Picture addBackgroundProfilePicture(String username) {
        return dbService.addBackgroundPictureForProfile(username);
    }
}
