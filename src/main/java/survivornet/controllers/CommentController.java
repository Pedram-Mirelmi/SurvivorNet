package survivornet.controllers;


import org.springframework.web.bind.annotation.*;
import survivornet.DTO.CommentDTO;
import survivornet.exceptions.InvalidRequestParamsException;
import survivornet.exceptions.UnauthorizedException;
import survivornet.services.domain.CommentService;
import survivornet.utils.JWTUtility;
import survivornet.utils.Constants;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts/{postId}")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }


    @GetMapping("comments")
    public List<CommentDTO> getPostComments(
            @RequestHeader(Constants.AUTHORIZATION) String jwtToken,
            @PathVariable long postId,
            @RequestParam(value = Constants.CHUNK) Optional<Integer> chunk) throws UnauthorizedException {

        return commentService.getPostComments(
                JWTUtility.getUsernameFromToken(jwtToken),
                postId,
                chunk.orElse(0));
    }

    @PostMapping("comments")
    public CommentDTO addComment(
            @RequestHeader(Constants.AUTHORIZATION) String jwtToken,
            @PathVariable(Constants.POST_ID) long postId,
            @RequestBody Map<String, String> comment) throws InvalidRequestParamsException, UnauthorizedException {
        String commentText;
        try {
            commentText = comment.get(Constants.TEXT);
        }
        catch (Exception e) {
            throw new InvalidRequestParamsException("No text for comment provided");
        }
        long parentId = Long.parseLong(comment.getOrDefault("parent", "-1"));

        return commentService.addComment(JWTUtility.getUsernameFromToken(jwtToken),
                postId,
                commentText,
                parentId);
    }

    @PostMapping("comments/{commentId}/likes")
    public Map<String, Object> likeComment(
            @RequestHeader(Constants.AUTHORIZATION) String jwtToken,
            @PathVariable(Constants.COMMENT_ID) long commentId,
            @RequestParam(Constants.LIKES) boolean likes) {
        boolean success = commentService.likeComment(
                JWTUtility.getUsernameFromToken(jwtToken),
                commentId,
                likes);
        return Map.of(Constants.STATUS, success);
    }


    @GetMapping("suggestions")
    public List<CommentDTO> getSuggestions(
            @RequestHeader(Constants.AUTHORIZATION) String jwtToken,
            @PathVariable long postId,
            @RequestParam Optional<Integer> chunk) throws UnauthorizedException {
        return commentService.getPostSuggestion(
                JWTUtility.getUsernameFromToken(jwtToken),
                postId,
                chunk.orElse(0));
    }

    @PostMapping("suggestions")
    public CommentDTO addSuggestion(
            @PathVariable(Constants.POST_ID) long postId,
            @RequestHeader(Constants.AUTHORIZATION) String jwtToken,
            @RequestBody Map<String, String> suggestion) throws InvalidRequestParamsException, UnauthorizedException {
        String suggestionText;
        try {
            suggestionText = suggestion.get(Constants.TEXT);
        }
        catch (Exception e) {
            throw new InvalidRequestParamsException("No text for suggestion provided");
        }
        return commentService.addSuggestion(JWTUtility.getUsernameFromToken(jwtToken),
                postId,
                suggestionText);
    }

    @PostMapping("solutions/{commentId}/likes")
    public Map<String, Object> likeSuggestion(
            @RequestHeader(Constants.AUTHORIZATION) String jwtToken,
            @PathVariable(Constants.COMMENT_ID) long commentId,
            @RequestParam(Constants.LIKES) boolean likes) {
        boolean success = commentService.likeComment(
                JWTUtility.getUsernameFromToken(jwtToken),
                commentId,
                likes);
        return Map.of(Constants.STATUS, success);
    }
}
