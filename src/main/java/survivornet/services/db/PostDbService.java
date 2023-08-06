package survivornet.services.db;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import survivornet.DTO.PostDTO;
import survivornet.DTO.PostReactionDTO;
import survivornet.exceptions.InvalidIdException;
import survivornet.models.Post;
import survivornet.models.PostReaction;
import survivornet.models.User;
import survivornet.repositories.CommentRepository;
import survivornet.repositories.PostReactionRepository;
import survivornet.repositories.PostRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static survivornet.utils.Constants.CHUNK_SIZE;

@Service
public class PostDbService {
    private final UserDbService userDbService;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostReactionRepository postReactionRepository;


    public PostDbService(UserDbService userDbService, PostRepository postRepository, CommentRepository commentRepository, PostReactionRepository postReactionRepository) {
        this.userDbService = userDbService;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.postReactionRepository = postReactionRepository;
    }


    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public Post addPost(String username, String title, String caption, long parentId) {
        Post parent = null;
        if(parentId != -1) {
            parent = getPostById(parentId);
        }
        return postRepository.save(new Post(userDbService.getUserByUsername(username), title, caption, LocalDateTime.now(), parent));
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Post getPostById(long postId) {
        Optional<Post> post = postRepository.findById(postId);
        if(post.isEmpty()) {
            throw new InvalidIdException("Invalid post Id");
        }
        return post.get();
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public PostDTO getPostDTO(long postId) {
        Optional<PostDTO> post = postRepository.getDtoById(postId);
        if(post.isEmpty()) {
            throw new InvalidIdException("Invalid post id");
        }
        return post.get();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
    public List<Post> getUserPosts(String username, int chunk) {
        return postRepository.findAllByUsername(username, PageRequest.of(chunk, CHUNK_SIZE));
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<PostDTO> getUserPostDTOs(String username, int chunk) {
        return postRepository.findAllDtoByUsername(username, PageRequest.of(chunk, CHUNK_SIZE));
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<PostDTO> getUserHomePostDTOs(String username, int chunk) {
        return postRepository.findAllHomePostDtoByUsername(username, PageRequest.of(chunk, CHUNK_SIZE));
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public long getPostCommentCount(long postId) {
        return commentRepository.countByPostId(postId);
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public long getPostReactionCount(long postId) {
        return postReactionRepository.countByPostId(postId);
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<PostReactionDTO> getPostReactionDTOs(long postId, int chunk) {
        return postReactionRepository.findAllDtoByPostId(postId, PageRequest.of(chunk, CHUNK_SIZE));
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public boolean addPostReaction(String username, long postId, int reactionType) {
        Optional<PostReaction> postReactionOpt = postReactionRepository.findByUsernameAndPostId(username, postId);
        PostReaction postReaction;
        if(postReactionOpt.isEmpty()) {
            User user = userDbService.getUserByUsername(username);
            Post post = getPostById(postId);
            postReaction = new PostReaction(user, post, reactionType);
        }
        else {
            postReaction = postReactionOpt.get();
            postReaction.setReactionType(reactionType);
        }
        postReactionRepository.save(postReaction);
        return true;
    }


}
