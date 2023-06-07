package se.survivor.net.controllers;

import static se.survivor.net.utils.Constants.*;

import org.springframework.web.bind.annotation.*;
import se.survivor.net.DTO.UserDTO;
import se.survivor.net.services.DBService;
import se.survivor.net.services.IDb;

@RestController("/api/users")
public class UserController {

    final private IDb dbService;

    public UserController(IDb dbService) {
        this.dbService = dbService;
    }


    @GetMapping("/{userId}")
    public UserDTO getUserBuyId(@PathVariable(USER_ID) Long userId, @RequestHeader(AUTHORIZATION) String jwtToken) {
        return new UserDTO(dbService.getUserById(userId));
    }
}
