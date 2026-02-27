package com.Project.DocApproval.exceptions;

public class InvalidJobDescriptionException extends RuntimeException {
    public InvalidJobDescriptionException(String message) {
        super(message);
    }
}
