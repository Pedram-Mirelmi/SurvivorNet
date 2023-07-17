package se.survivor.net.services.domain;

import org.springframework.stereotype.Service;
import se.survivor.net.exceptions.UnauthorizedException;
import se.survivor.net.models.Picture;
import se.survivor.net.services.AuthorizationService;
import se.survivor.net.services.db.PictureDbService;

@Service
public class PictureService {
    private final PictureDbService pictureDbService;
    private final AuthorizationService authorizationService;

    public PictureService(PictureDbService pictureDbService, AuthorizationService authorizationService) {
        this.pictureDbService = pictureDbService;
        this.authorizationService = authorizationService;
    }

    public Picture addPictureToPost(String username, long postId) throws UnauthorizedException {
        if(!authorizationService.canAddPictureToPost(username, postId)) {
            throw new UnauthorizedException("User cannot add picture to this post");
        }
        return pictureDbService.addPicturePost(postId);
    }

    public Picture addProfilePicture(String username) {
        return pictureDbService.addPictureForProfile(username);
    }

    public Picture addBackgroundProfilePicture(String username) {
        return pictureDbService.addBackgroundPictureForProfile(username);
    }


}
