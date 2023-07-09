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


}
