package se.survivor.net.services;

import se.survivor.net.DTO.PostDTO;
import se.survivor.net.exceptions.InvalidValueException;

import java.util.List;

public interface IPostService {

    public List<PostDTO> getUsersHomePosts(long userId, int chunk) throws InvalidValueException;

    public PostDTO getPost(long postId) ;
}
