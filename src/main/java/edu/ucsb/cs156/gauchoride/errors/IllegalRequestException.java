package edu.ucsb.cs156.gauchoride.errors;

public class IllegalRequestException extends RuntimeException {
    public IllegalRequestException() {
        super("HTTP request cannot be processed.");
    }
}
