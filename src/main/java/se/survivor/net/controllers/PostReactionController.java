package se.survivor.net.controllers;

import static se.survivor.net.utils.Constants.*;

import org.springframework.web.bind.annotation.*;
import se.survivor.net.DTO.PostReactionDTO;
import se.survivor.net.services.ReactionService;
import se.survivor.net.utils.JWTUtility;

import java.util.List;
import java.util.Map;

@RestController
public class PostReactionController {

    private ReactionService reactionService;

    public PostReactionController(ReactionService reactionService) {
        this.reactionService = reactionService;
    }

    @PostMapping("/posts/{postId}/reactions")
    public Map<String, Object> addReaction(
            @RequestHeader(AUTHORIZATION) String jwtToken,
            @PathVariable(POST_ID) long postId,
            @RequestParam(REACTION_TYPE) int reactionType) {
        String username = JWTUtility.getUsernameFromToken(jwtToken);
        reactionService.addReaction(username, postId, reactionType);
        return Map.of(
                STATUS, SUCCESS
        );
    }

    @GetMapping("/posts/{postId}/reactions")
    public List<PostReactionDTO> getPostReactions(
            @PathVariable(POST_ID) long postId) {
        return reactionService.getReactions(postId);
    }

}
