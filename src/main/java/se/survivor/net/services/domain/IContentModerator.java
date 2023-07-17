package se.survivor.net.services.domain;

public interface IContentModerator {
    boolean isValidPostTitle(String postTitle);

    boolean isValidPostCaption(String caption);

    boolean isValidCommentText(String commentText);

    boolean isValidSolutionText(String SolutionText);

    boolean isValidBio(String bioText);

}

