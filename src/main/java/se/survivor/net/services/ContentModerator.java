package se.survivor.net.services;

import org.springframework.stereotype.Service;

@Service
public class ContentModerator implements IContentModerator {
    @Override
    public boolean isValidPostTitle(String postTitle) {
        return true;
    }

    @Override
    public boolean isValidPostCaption(String caption) {
        return true;
    }

    @Override
    public boolean isValidCommentText(String commentText) {
        return true;
    }

    @Override
    public boolean isValidSolutionText(String SolutionText) {
        return true;
    }

    @Override
    public boolean isValidBio(String bioText) {
        return true;
    }
}
