package se.survivor.net.exceptions;

public class ErrorResponse {
    final private int statusCode;
    final private String message;
    final private String title;

    public ErrorResponse(int statusCode, String message, String title) {
        this.statusCode = statusCode;
        this.message = message;
        this.title = title;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public String getTitle() {
        return title;
    }
}
