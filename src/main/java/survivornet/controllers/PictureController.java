package survivornet.controllers;


import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import survivornet.exceptions.UnauthorizedException;
import survivornet.models.Picture;
import survivornet.services.domain.PictureService;
import survivornet.utils.JWTUtility;
import survivornet.utils.Constants;

import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Map;

@RestController
@RequestMapping("api/pictures")
public class PictureController {
    private final PictureService pictureService;

    public PictureController(PictureService pictureService) {
        this.pictureService = pictureService;
    }

    @PostMapping("post/{postId}")
    public Map<String, String> addPostPicture(
            @RequestHeader(Constants.AUTHORIZATION) String jwtToken,
            @PathVariable(Constants.POST_ID) long postId,
            @RequestParam("picture.png") MultipartFile file) throws IOException, UnauthorizedException {

        Picture picture = pictureService.addPictureToPost(
                JWTUtility.getUsernameFromToken(jwtToken),
                postId,
                file);

        return Map.of(Constants.STATUS, Constants.SUCCESS,
                Constants.PICTURE_ID, picture.getPictureId().toString());
    }

    @PostMapping("profile-picture")
    public Map<String, String> addProfilePicture(
            @RequestHeader(Constants.AUTHORIZATION) String jwtToken,
            @RequestParam("picture.png") MultipartFile file) throws IOException, SQLIntegrityConstraintViolationException {
        Picture picture = pictureService.addProfilePicture(
                JWTUtility.getUsernameFromToken(jwtToken),
                file);

        return Map.of(Constants.STATUS, Constants.SUCCESS,
                Constants.PICTURE_ID, picture.getPictureId().toString());
    }

    @PostMapping("background-picture")
    public Map<String, String> addBackgroundPicture(
            @RequestHeader(Constants.AUTHORIZATION) String jwtToken,
            @RequestParam("picture.png") MultipartFile file) throws IOException, SQLIntegrityConstraintViolationException {
        Picture picture = pictureService.addBackgroundProfilePicture(
                JWTUtility.getUsernameFromToken(jwtToken),
                file);

        return Map.of(Constants.STATUS, Constants.SUCCESS,
                Constants.PICTURE_ID, picture.getPictureId().toString());
    }

}
