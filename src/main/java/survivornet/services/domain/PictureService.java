package survivornet.services.domain;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import survivornet.exceptions.UnauthorizedException;
import survivornet.models.Picture;
import survivornet.services.AuthorizationService;
import survivornet.services.db.PictureDbService;

import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLIntegrityConstraintViolationException;

@Service
public class PictureService {
    private final PictureDbService pictureDbService;
    private final AuthorizationService authorizationService;

    public PictureService(PictureDbService pictureDbService, AuthorizationService authorizationService) {
        this.pictureDbService = pictureDbService;
        this.authorizationService = authorizationService;
    }

    public Picture addPictureToPost(String username, long postId, MultipartFile file) throws UnauthorizedException, IOException {
        if(!authorizationService.canAddPictureToPost(username, postId)) {
            throw new UnauthorizedException("User cannot add picture to this post");
        }
        Picture picture = pictureDbService.addPicturePost(postId);
        saveFile(file, picture.getPictureId() + ".png");
        return picture;
    }

    public Picture addProfilePicture(String username, MultipartFile file) throws IOException, SQLIntegrityConstraintViolationException {
        Picture picture = pictureDbService.addPictureForProfile(username);
        saveFile(file, picture.getPictureId() + ".png");
        return picture;
    }

    public Picture addBackgroundProfilePicture(String username, MultipartFile file) throws IOException, SQLIntegrityConstraintViolationException {
        Picture picture = pictureDbService.addBackgroundPictureForProfile(username);
        saveFile(file, picture.getPictureId() + ".png");
        return picture;
    }

    private void saveFile(MultipartFile file, String name) throws IOException {
        file.transferTo(Paths.get("src",
                "main",
                "resources",
                "data",
                "pictures",
                name));
    }

}
