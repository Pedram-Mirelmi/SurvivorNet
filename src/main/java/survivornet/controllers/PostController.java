package survivornet.controllers;


import org.springframework.web.bind.annotation.*;
import survivornet.DTO.PostDTO;
import survivornet.DTO.PostReactionDTO;
import survivornet.exceptions.InvalidValueException;
import survivornet.exceptions.UnauthorizedException;
import survivornet.services.domain.PostService;
import survivornet.utils.JWTUtility;
import survivornet.utils.Constants;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("api/posts")
public class PostController {

    private final PostService postService;

    PostController(PostService postService) {
        this.postService = postService;
    }


    @GetMapping("home")
    public List<PostDTO> getHomePosts(
            @RequestHeader(Constants.AUTHORIZATION) String jwtToken,
            @RequestParam(Constants.CHUNK) int chunk) throws InvalidValueException {
        return postService.getHomePosts(
                JWTUtility.getUsernameFromToken(jwtToken),
                chunk);
    }

    @GetMapping("{postId}")
    public PostDTO getPostDTO(
            @RequestHeader(Constants.AUTHORIZATION) String jwtToken,
            @PathVariable(Constants.POST_ID) long postId) throws UnauthorizedException {
        return postService.getPostDTO(
                JWTUtility.getUsernameFromToken(jwtToken),
                postId);
    }

    @PostMapping("")
    public Map<String, Object> addPost(
            @RequestHeader(Constants.AUTHORIZATION) String jwtToken,
            @RequestBody Map<String, String> postInfo) {
        String title = Objects.requireNonNull(postInfo.get(Constants.TITLE));
        String caption = Objects.requireNonNull(postInfo.get(Constants.CAPTION));
        long parentId = -1;
        if(postInfo.containsKey(Constants.PARENT_ID)) {
            parentId = Long.parseLong(postInfo.get(Constants.PARENT_ID));
        }
        PostDTO post = postService.addPost(
                JWTUtility.getUsernameFromToken(jwtToken),
                title,
                caption,
                parentId
        );
        return Map.of(Constants.SUCCESS, Constants.TRUE,
                Constants.INFO, post);
    }


    @PostMapping("{postId}/reactions")
    public Map<String, Object> addReaction(
            @RequestHeader(Constants.AUTHORIZATION) String jwtToken,
            @PathVariable(Constants.POST_ID) long postId,
            @RequestParam(Constants.REACTION_TYPE) int reactionType) throws UnauthorizedException {
        String username = JWTUtility.getUsernameFromToken(jwtToken);
        boolean success = postService.addReaction(username, postId, reactionType);
        return Map.of(
                Constants.STATUS, success
        );
    }

    @GetMapping("{postId}/reactions")
    public List<PostReactionDTO> getPostReactions(
            @RequestHeader(Constants.AUTHORIZATION) String jwtToken,
            @PathVariable(Constants.POST_ID) long postId,
            @RequestParam(Constants.CHUNK) int chunk) throws UnauthorizedException {
        return postService.getReactions(
                JWTUtility.getUsernameFromToken(jwtToken),
                postId,
                chunk);
    }

}
