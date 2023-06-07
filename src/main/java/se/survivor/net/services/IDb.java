package se.survivor.net.services;

import se.survivor.net.DTO.UserDTO;
import se.survivor.net.models.User;

import java.sql.Date;

public interface IDb {
    User getUserById(Long userId);

    User getUserByEmail(String email);

    void updateUser(long userId, String username, Date birthDate);

    void addUser(UserDTO user);

    boolean authenticate(String username, String password);
}
