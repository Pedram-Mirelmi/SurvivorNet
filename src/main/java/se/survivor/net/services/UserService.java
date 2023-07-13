package se.survivor.net.services;

import org.springframework.stereotype.Service;
import se.survivor.net.DTO.UserDTO;
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

    public List<UserDTO> getUserFollowersDTO(Long userId) {
        return dbService.getFollowers(userId)
                .stream()
                .map(UserDTO::new)
                .toList();
    }

    public List<UserDTO> getUserFollowingsDTO(Long userId) {
        return dbService.getFollowings(userId)
                .stream()
                .map(UserDTO::new)
                .toList();
    }

    public boolean addFollow(String followerUsername, Long followeeId) {
        try {
            User follower = dbService.getUserByUsername(followerUsername);
            dbService.changeFollow(follower.getUserId(), followeeId, true);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    public boolean removeFollow(String followerUsername, Long followeeId) {
        try {
            User follower = dbService.getUserByUsername(followerUsername);
            dbService.changeFollow(follower.getUserId(), followeeId, false);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    public boolean addBlock(String blockerUsername, Long blockeeId) {
        try {
            User follower = dbService.getUserByUsername(blockerUsername);
            dbService.changeFollow(follower.getUserId(), blockeeId, true);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    public boolean removeBlock(String blockerUsername, Long blockeeId) {
        try {
            User follower = dbService.getUserByUsername(blockerUsername);
            dbService.changeFollow(follower.getUserId(), blockeeId, false);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    public List<UserDTO> searchUsers(String query) {
        return dbService.searchUsers(query).stream().map(UserDTO::new).toList();
    }
}
