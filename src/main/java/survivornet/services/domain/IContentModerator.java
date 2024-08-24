package survivornet.services.domain;

public interface IContentModerator {
    boolean isValidPostTitle(String postTitle);

    boolean isValidPostCaption(String caption);

    boolean isValidCommentText(String commentText);

    boolean isValidSuggestionText(String SolutionText);

    boolean isValidBio(String bioText);

}

