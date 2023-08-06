package survivornet.utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import survivornet.DTO.CommentDTO;
import survivornet.models.Comment;
import survivornet.models.Post;
import survivornet.models.User;
import survivornet.services.db.CommentDbService;
import survivornet.services.db.PostDbService;
import survivornet.services.db.UserDbService;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataGenerator {


    private static UserDbService userDbService;
    private static PostDbService postService;
    private static CommentDbService commentService;
    private static EntityManagerFactory entityManagerFactory;

    private static EntityManager entityManager;

    private static final List<User> users = new ArrayList<>();
    private static final List<Post> posts = new ArrayList<>();
    private static final List<Comment> comments = new ArrayList<>();

    public static void deleteData() {
        for (var u : users) {
            userDbService.removeUser(u.getUsername());
        }
    }
    
    public static void generateData(int numberOfUsers, int numberOfPosts, int numberOfComments) {
        generateUsers(numberOfUsers);
        generateFollows(numberOfUsers/10, numberOfUsers/2);
        generatePosts(numberOfPosts);
        generateComments(numberOfComments);
    }

    private static void generateComments(int numberOfComments) {
        // Adding comments for each post
        for (Post post : posts) {
            System.out.println("Goint to next post");
            int numComments = new Random().nextInt(numberOfComments) + numberOfComments;
            for (int i = 0; i < numComments; i++) {
                System.out.println("Goint to next comment");
                User owner = users.get(new Random().nextInt(users.size()));
                String text = "Comment " + (i + 1) + " for Post " + post.getPostId();
                if(new Random().nextInt(2) == 1) { // solution or comment
                    long parentId = -1; // Default value for no parent comment
                    var postComments = getPostComments(post.getPostId());
                    if (!postComments.isEmpty() && new Random().nextInt(2) == 1) { // 50% chance of having a parent comment
                        CommentDTO parentComment = postComments.get(new Random().nextInt(postComments.size()));
                        parentId = parentComment.getCommentId();
                    }
                    comments.add(commentService.addComment(owner.getUsername(), post.getPostId(), text, parentId));
                }
                else {
                    comments.add(commentService.addSolution(owner.getUsername(), post.getPostId(), text));
                }
            }
        }
    }

    private static List<CommentDTO> getPostComments(long postId) {
        List<CommentDTO> comments = new ArrayList<>();
        int chunk = 0;
        while (true) {
            System.out.println("Getting post comment");
            var chunkedPosts = commentService.getPostComments(postId, chunk);
            if(chunkedPosts.isEmpty()) {
                break;
            }
            comments.addAll(chunkedPosts);
            chunk++;
        }
        return comments;
    }

    private static void generatePosts(int numberOfPosts) {
        for (User user : users) {
            int numPosts = new Random().nextInt(numberOfPosts) + numberOfPosts; // Random number of posts: 3 to 7
            for (int i = 0; i < numPosts; i++) {
                String title = "Post Title " + (i + 1);
                String caption = "Post Caption " + (i + 1);
                long parentId = -1; // Default value for no parent

                if (new Random().nextInt(2) == 1) { // 50% chance of having a parent
                    List<Post> userPosts = getUserPosts(user);
                    if (!userPosts.isEmpty()) {
                        Post parentPost = userPosts.get(new Random().nextInt(userPosts.size()));
                        parentId = parentPost.getPostId();
                    }
                }
                
                posts.add(postService.addPost(user.getUsername(), title, caption, parentId));
            }
        }
    }


    private static List<Post> getUserPosts(User user) {
        List<Post> posts = new ArrayList<>();
        int chunk = 0;
        while (true) {
            var chunkedPosts = postService.getUserPosts(user.getUsername(), chunk);
            if(chunkedPosts.isEmpty()) {
                break;
            }
            posts.addAll(chunkedPosts);
            chunk++;
        }
        return posts;
    }

    private static void generateFollows(int min, int max) {
        for (User user : users) {
            List<User> usersToFollow = selectRandomUsers(users, user, min, max);
            for (User userToFollow : usersToFollow) {
                if (!user.getUsername().equals(userToFollow.getUsername())) {
                    userDbService.changeFollow(user.getUsername(), userToFollow.getUsername(), true);
                }
            }
        }
    }


    private static void generateUsers(long count) {
        for (int i = 0; i < count; i++) {
            User user = userDbService.addUser(
                    "username(" + i + ")",
                    "name(" + i + ")",
                    "pass(" + i + ")",
                    "user" + i + "@survivornet.com",
                    Date.valueOf("2000-01-01"),
                    "Bio of User " + i
            );
            users.add(user);
        }
    }

    // Helper method to select random users to follow for a given user
    private static List<User> selectRandomUsers(List<User> users, User currentUser, long minFollows, long maxFollows) {
        List<User> usersToFollow = new ArrayList<>();
        long numFollows = new Random().nextLong(maxFollows - minFollows + 1) + minFollows;
        while (usersToFollow.size() < numFollows) {
            User randomUser = users.get(new Random().nextInt(users.size()));
            if (!usersToFollow.contains(randomUser) && !randomUser.getUsername().equals(currentUser.getUsername())) {
                usersToFollow.add(randomUser);
            }
        }
        return usersToFollow;
    }

    public static void setUserDbService(UserDbService userDbService) {
        DataGenerator.userDbService = userDbService;
    }

    public static void setPostService(PostDbService postService) {
        DataGenerator.postService = postService;
    }

    public static void setCommentService(CommentDbService commentService) {
        DataGenerator.commentService = commentService;
    }
}
