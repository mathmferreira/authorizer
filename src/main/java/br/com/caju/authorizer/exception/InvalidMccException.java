package br.com.caju.authorizer.exception;

public class InvalidMccException extends RuntimeException {

    public InvalidMccException() {
        super("the given mcc is invalid");
    }

}
