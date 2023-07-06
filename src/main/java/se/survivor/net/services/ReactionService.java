package se.survivor.net.services;

import org.springframework.stereotype.Service;
import se.survivor.net.DTO.PostReactionDTO;
import se.survivor.net.models.User;

import java.util.List;

@Service
public class ReactionService {

    DbService dbService;

    public ReactionService(DbService dbService) {
        this.dbService = dbService;
    }

    public void addReaction(String username, long postId, int reactionType) {
        User user = dbService.getUserByUsername(username);
        dbService.addReaction(user.getUserId(), postId, reactionType);
    }

    public List<PostReactionDTO> getReactions(long postId) {
        return dbService.getPostReactions(postId).
                stream().
                map(PostReactionDTO::new).toList();
    }
}
