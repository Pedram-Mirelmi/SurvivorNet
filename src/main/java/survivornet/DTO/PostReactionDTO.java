package survivornet.DTO;

import survivornet.models.PostReaction;

public class PostReactionDTO {
    private UserDTO user;
    private long postId;
    private int reactionType;

    public PostReactionDTO(UserDTO user, long postId, int reactionType) {
        this.user = user;
        this.postId = postId;
        this.reactionType = reactionType;
    }

    public PostReactionDTO(PostReaction postReaction) {
        this.user = new UserDTO(postReaction.getUser(), 0, 0);
        this.postId = postReaction.getPost().getPostId();
        this.reactionType = postReaction.getReactionType();
    }

    public UserDTO getUser() {
        return user;
    }

    public long getPostId() {
        return postId;
    }

    public int getReactionType() {
        return reactionType;
    }
}
