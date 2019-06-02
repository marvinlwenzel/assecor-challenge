package assecorpeople.services;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class IdOccupiedException extends RuntimeException {
    public IdOccupiedException() {
    }

    public IdOccupiedException(String message) {
        super(message);
    }

    public IdOccupiedException(String message, Throwable cause) {
        super(message, cause);
    }

    public IdOccupiedException(Throwable cause) {
        super(cause);
    }

    public IdOccupiedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
