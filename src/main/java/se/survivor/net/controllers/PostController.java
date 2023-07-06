package se.survivor.net.controllers;


import org.springframework.web.bind.annotation.*;
import se.survivor.net.DTO.PostDTO;
import se.survivor.net.exceptions.InvalidValueException;
import se.survivor.net.services.CommentService;
import se.survivor.net.services.DbService;
import se.survivor.net.services.PostService;
import se.survivor.net.utils.JWTUtility;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static se.survivor.net.utils.Constants.*;

@RestController
@RequestMapping("/api/posts/") // www.survivor-net.com/api/posts/******
public class PostController {

    private DbService dbService;
    private PostService postService;
    private CommentService commentService;

    PostController(DbService dbService,
                   PostService postService,
                   CommentService commentService) {
        this.dbService = dbService;
        this.postService = postService;
        this.commentService = commentService;
    }


    @GetMapping("home")
    public List<PostDTO> getHomePosts(
            @RequestHeader(AUTHORIZATION) String jwtToken,
            @RequestParam(CHUNK) int chunk) throws InvalidValueException {
        return postService.getHomePosts(JWTUtility.getUsernameFromToken(jwtToken), chunk);
    }

    @GetMapping("{postId}")
    public PostDTO getPostDTO(
            @PathVariable(POST_ID) long postId) {
        return postService.getPostDTOWithComments(postId);
    }

    // www.survivor-net.com/api/posts/
    @PostMapping("")
    public Map<String, String > addPost(
            @RequestHeader(AUTHORIZATION) String jwtToken,
            @RequestBody Map<String, String> postInfo) {
        String title = Objects.requireNonNull(postInfo.get(TITLE));
        String caption = Objects.requireNonNull(postInfo.get(CAPTION));
        long parentId = -1;
        if(postInfo.containsKey(PARENT_ID)) {
            parentId = Long.parseLong(postInfo.get(PARENT_ID));
        }
        postService.addPost(JWTUtility.getUsernameFromToken(jwtToken),
                title,
                caption,
                parentId);
        return Map.of(SUCCESS, TRUE);
    }

}
