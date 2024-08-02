package br.com.caju.authorizer.exception;

public class InvalidAmountException extends RuntimeException {

    public InvalidAmountException() {
        super("amount cannot be negative");
    }

}
