package edu.ucsb.cs156.gauchoride.errors;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IllegalRequestException extends RuntimeException {
    public IllegalRequestException() {
        super("HTTP request cannot be processed.");
    }
}