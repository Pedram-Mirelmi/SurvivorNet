package se.survivor.net.controllers;


import org.springframework.web.bind.annotation.*;
import se.survivor.net.DTO.CommentDTO;
import se.survivor.net.exceptions.InvalidRequestParamsException;
import se.survivor.net.exceptions.UnauthorizedException;
import se.survivor.net.services.CommentService;
import se.survivor.net.utils.JWTUtility;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static se.survivor.net.utils.Constants.*;

@RestController
public class CommentController {
    private CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }


    @GetMapping("/api/posts/{postId}/comments")
    public List<CommentDTO> getPostComments(
            @RequestHeader(AUTHORIZATION) String jwtToken,
            @PathVariable long postId,
            @RequestParam(value = CHUNK) Optional<Integer> chunk) throws UnauthorizedException {

        return commentService.getPostComments(
                JWTUtility.getUsernameFromToken(jwtToken),
                postId,
                chunk.orElse(0));
    }

    @PostMapping("/api/posts/{postId}/comments")
    public CommentDTO addComment(
            @RequestHeader(AUTHORIZATION) String jwtToken,
            @PathVariable(POST_ID) long postId,
            @RequestBody Map<String, String> comment) throws InvalidRequestParamsException, UnauthorizedException {
        String commentText;
        try {
            commentText = comment.get(TEXT);
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

    @GetMapping("/api/posts/{postId}/solutions")
    public List<CommentDTO> getSolutions(
            @RequestHeader(AUTHORIZATION) String jwtToken,
            @PathVariable long postId,
            @RequestParam(value = CHUNK, required = false)Optional<Integer> chunk) throws UnauthorizedException {
        return commentService.getPostSolutions(
                JWTUtility.getUsernameFromToken(jwtToken),
                postId,
                chunk.orElse(0));
    }

    @PostMapping("/api/posts/{postId}/solutions")
    public CommentDTO addSolution(
            @PathVariable(POST_ID) long postId,
            @RequestHeader(AUTHORIZATION) String jwtToken,
            @RequestBody Map<String, String> solution) throws InvalidRequestParamsException, UnauthorizedException {
        String solutionText;
        try {
            solutionText = solution.get(TEXT);
        }
        catch (Exception e) {
            throw new InvalidRequestParamsException("No text for solution provided");
        }
        return commentService.addSolution(JWTUtility.getUsernameFromToken(jwtToken),
                postId,
                solutionText);
    }
}
