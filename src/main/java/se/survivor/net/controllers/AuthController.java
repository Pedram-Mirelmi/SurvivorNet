package se.survivor.net.controllers;


import org.springframework.web.bind.annotation.RestController;
import se.survivor.net.services.IDb;

@RestController
public class AuthController {
    final IDb db;

    public AuthController(IDb db) {
        this.db = db;
    }
}
