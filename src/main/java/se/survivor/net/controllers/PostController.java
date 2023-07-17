package se.survivor.net.controllers;


import org.springframework.web.bind.annotation.*;
import se.survivor.net.DTO.PostDTO;
import se.survivor.net.DTO.PostReactionDTO;
import se.survivor.net.exceptions.InvalidValueException;
import se.survivor.net.exceptions.UnauthorizedException;
import se.survivor.net.services.PostService;
import se.survivor.net.utils.JWTUtility;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static se.survivor.net.utils.Constants.*;

@RestController
@RequestMapping("/api/posts/")
public class PostController {

    private final PostService postService;

    PostController(PostService postService) {
        this.postService = postService;
    }


    @GetMapping("home")
    public List<PostDTO> getHomePosts(
            @RequestHeader(AUTHORIZATION) String jwtToken,
            @RequestParam(CHUNK) int chunk) throws InvalidValueException {
        return postService.getHomePosts(JWTUtility.getUsernameFromToken(jwtToken), chunk);
    }

    @GetMapping("{postId}")
    public PostDTO getPostDTO(
            @RequestHeader(AUTHORIZATION) String jwtToken,
            @PathVariable(POST_ID) long postId) throws UnauthorizedException {
        return postService.getPostDTO(
                JWTUtility.getUsernameFromToken(jwtToken),
                postId);
    }

    @PostMapping("")
    public Map<String, String> addPost(
            @RequestHeader(AUTHORIZATION) String jwtToken,
            @RequestBody Map<String, String> postInfo) {
        String title = Objects.requireNonNull(postInfo.get(TITLE));
        String caption = Objects.requireNonNull(postInfo.get(CAPTION));
        long parentId = -1;
        if(postInfo.containsKey(PARENT_ID)) {
            parentId = Long.parseLong(postInfo.get(PARENT_ID));
        }
        postService.addPost(
                JWTUtility.getUsernameFromToken(jwtToken),
                title,
                caption,
                parentId
        );
        return Map.of(SUCCESS, TRUE);
    }


    @PostMapping("/posts/{postId}/reactions")
    public Map<String, Object> addReaction(
            @RequestHeader(AUTHORIZATION) String jwtToken,
            @PathVariable(POST_ID) long postId,
            @RequestParam(REACTION_TYPE) int reactionType) throws UnauthorizedException {
        String username = JWTUtility.getUsernameFromToken(jwtToken);
        postService.addReaction(username, postId, reactionType);
        return Map.of(
                STATUS, SUCCESS
        );
    }

    @GetMapping("/posts/{postId}/reactions")
    public List<PostReactionDTO> getPostReactions(
            @RequestHeader(AUTHORIZATION) String jwtToken,
            @PathVariable(POST_ID) long postId) throws UnauthorizedException {
        return postService.getReactions(
                JWTUtility.getUsernameFromToken(jwtToken),
                postId);
    }

}
