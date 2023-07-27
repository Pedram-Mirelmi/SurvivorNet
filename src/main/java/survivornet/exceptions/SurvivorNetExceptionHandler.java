package survivornet.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class SurvivorNetExceptionHandler {

    @ExceptionHandler(InvalidIdException.class)
    public ResponseEntity<ErrorResponse> handleInvalidIdException(InvalidIdException exception) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse(404, exception.getMessage(),"Not Found"),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidValueException.class)
    public ResponseEntity<ErrorResponse> handleInvalidValueException(InvalidValueException exception) {
        return new ResponseEntity<>(new ErrorResponse(400, exception.getMessage(), "Not Acceptable"),
                HttpStatus.NOT_ACCEPTABLE);
    }


    @ExceptionHandler(InvalidRequestParamsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequestParamsException(InvalidRequestParamsException exception) {
        return new ResponseEntity<>(new ErrorResponse(400, exception.getMessage(), "Bad Request"),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException exception) {
        return new ResponseEntity<>(new ErrorResponse(401, exception.getMessage(), "Unauthorized"),
                HttpStatus.UNAUTHORIZED);
    }
}
