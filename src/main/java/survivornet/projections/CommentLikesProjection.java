package survivornet.projections;

//public class CommentLikesProjection {
//    private final long likes;
//    private final long dislikes;
//
//    public CommentLikesProjection(long likes, long dislikes) {
//        this.likes = likes;
//        this.dislikes = dislikes;
//    }
//
//    public long getLikes() {
//        return likes;
//    }
//
//    public long getDislikes() {
//        return dislikes;
//    }
//}


public interface CommentLikesProjection {
    long getLikes();
    long getDislikes();
}
