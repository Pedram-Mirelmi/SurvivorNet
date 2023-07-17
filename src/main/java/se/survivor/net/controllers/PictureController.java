package se.survivor.net.controllers;


import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import se.survivor.net.exceptions.UnauthorizedException;
import se.survivor.net.models.Picture;
import se.survivor.net.services.domain.PictureService;
import se.survivor.net.utils.JWTUtility;

import java.io.IOException;
import java.util.Map;

import static se.survivor.net.utils.Constants.*;

@RestController
@RequestMapping("api/pictures")
public class PictureController {
    private final PictureService pictureService;

    public PictureController(PictureService pictureService) {
        this.pictureService = pictureService;
    }

    @PostMapping("post/{postId}")
    public Map<String, String> addPostPicture(
            @RequestHeader(AUTHORIZATION) String jwtToken,
            @PathVariable(POST_ID) long postId,
            @RequestParam("picture.png") MultipartFile file) throws IOException, UnauthorizedException {

        Picture picture = pictureService.addPictureToPost(
                JWTUtility.getUsernameFromToken(jwtToken),
                postId,
                file);

        return Map.of(STATUS, SUCCESS,
                PICTURE_ID, picture.getPictureId().toString());
    }

    @PostMapping("profile-picture")
    public Map<String, String> addProfilePicture(
            @RequestHeader(AUTHORIZATION) String jwtToken,
            @RequestParam("picture.png") MultipartFile file) throws IOException {
        Picture picture = pictureService.addProfilePicture(
                JWTUtility.getUsernameFromToken(jwtToken),
                file);

        return Map.of(STATUS, SUCCESS,
                PICTURE_ID, picture.getPictureId().toString());
    }

    @PostMapping("background-picture")
    public Map<String, String> addBackgroundPicture(
            @RequestHeader(AUTHORIZATION) String jwtToken,
            @RequestParam("picture.png") MultipartFile file) throws IOException {
        Picture picture = pictureService.addBackgroundProfilePicture(
                JWTUtility.getUsernameFromToken(jwtToken),
                file);

        return Map.of(STATUS, SUCCESS,
                PICTURE_ID, picture.getPictureId().toString());
    }

}
