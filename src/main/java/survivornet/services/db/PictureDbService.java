package survivornet.services.db;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import survivornet.models.Picture;
import survivornet.models.Post;
import survivornet.models.User;
import survivornet.repositories.PictureRepository;

import java.sql.SQLIntegrityConstraintViolationException;

@Service
public class PictureDbService {
    private final UserDbService userDbService;
    private final PostDbService postDbService;
    private final PictureRepository pictureRepository;



    public PictureDbService(UserDbService userDbService, PostDbService postDbService, PictureRepository pictureRepository) {
        this.userDbService = userDbService;
        this.postDbService = postDbService;
        this.pictureRepository = pictureRepository;
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public Picture addPictureForProfile(String username) throws SQLIntegrityConstraintViolationException {
        User user = userDbService.getUserByUsername(username);
        if(user.getProfilePic() != null) {
            pictureRepository.delete(user.getProfilePic());
        }
        Picture newPicture = pictureRepository.save(new Picture(user, null));
        user.setProfilePic(newPicture);
        userDbService.persistUser(user);
        return newPicture;
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public Picture addBackgroundPictureForProfile(String username) throws SQLIntegrityConstraintViolationException {
        User user = userDbService.getUserByUsername(username);
        if(user.getBackgroundPic() != null) {
            pictureRepository.delete(user.getBackgroundPic());
        }
        Picture newPicture = pictureRepository.save(new Picture(user, null));
        user.setBackgroundPic(newPicture);
        userDbService.persistUser(user);
        return newPicture;
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public Picture addPicturePost(long postId) {
        Post post = postDbService.getPostById(postId);
        return pictureRepository.save(new Picture(post.getUser(), post));
    }



}
