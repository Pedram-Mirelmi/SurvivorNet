package se.survivor.net.services;

import se.survivor.net.models.User;

public interface IDb {
    User getUserById(Long userId);
}
