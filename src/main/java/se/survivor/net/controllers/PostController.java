package se.survivor.net.controllers;


import org.springframework.web.bind.annotation.*;
import se.survivor.net.DTO.PostDTO;
import se.survivor.net.models.User;
import se.survivor.net.services.IDb;
import se.survivor.net.utils.JWTUtility;

import java.util.List;

import static se.survivor.net.utils.Constants.*;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private IDb db;

    PostController(IDb db) {
        this.db = db;
    }

    @GetMapping("")
    public List<PostDTO> getHomePosts(@RequestHeader(AUTHORIZATION) String jwtToken) {
        User user = db.getUserByUsername(JWTUtility.getUsernameFromToken(jwtToken));
        return db.getUserHomePosts(user);
    }

    @GetMapping("{postId}")
    public List<PostDTO> getPost(@PathVariable(POST_ID) long postId) {
        return db.getPost(postId);
    }

}
