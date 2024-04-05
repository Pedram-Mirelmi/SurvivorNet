package survivornet.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import survivornet.models.Post;
import survivornet.models.User;
import survivornet.repositories.BlockRepository;
import survivornet.services.db.PostDbService;
import survivornet.services.db.UserDbService;

@Service
public class AuthorizationService {


    private final UserDbService userDbService;
    private final PostDbService postDbService;
    private final BlockRepository blockRepository;

    public AuthorizationService(UserDbService userDbService, PostDbService postDbService, BlockRepository blockRepository) {
        this.userDbService = userDbService;
        this.postDbService = postDbService;
        this.blockRepository = blockRepository;
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public boolean canAccessProfile(String viewerUsername, String underViewUsername) {
        return blockRepository.findByBlockerAndBlockee(
                underViewUsername, viewerUsername)
            .isEmpty();
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public boolean canFollow(String followerUsername, String followeeUsername) {
        // later we can add different profile privacy modes
        return canAccessProfile(followerUsername, followeeUsername);
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public boolean canViewFollowList(String viewerUsername, String underViewUsername) {
        // later we can add different profile privacy modes
        return canAccessProfile(viewerUsername, underViewUsername);
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public boolean canViewPost(String viewerUsername, long postId) {
        Post post = postDbService.getPostById(postId);
        User targetUser = userDbService.getUserById(post.getUser().getUserId());
        return canAccessProfile(viewerUsername, targetUser.getUsername());
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public boolean canAddReaction(String doerUser, long postId) {
        // for now it's the same
        return canViewPost(doerUser, postId);
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public boolean canViewPostReactions(String viewerUsername, long postId) {
        // for now it's the same
        return canAddReaction(viewerUsername, postId);
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public boolean canLeaveComment(String doerUsername, long postId) {
        // for now it's the same
        return canViewPost(doerUsername, postId);
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public boolean canAddSolution(String doerUsername, long postId) {
        // for now it's the same
        return canViewPost(doerUsername, postId);
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public boolean canViewPostComments(String viewerUsername, long postId) {
        // for now it's the same
        return canViewPost(viewerUsername, postId);
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public boolean canAddPictureToPost(String username, long postId) {
        return postDbService.getPostById(postId)
                .getUser().getUsername().equals(username);
    }
}
