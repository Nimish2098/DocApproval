package com.Project.DocApproval.exceptions;

import lombok.RequiredArgsConstructor;

import java.io.IOException;


public class FileStorageException extends RuntimeException {

    public FileStorageException(String message){
        super(message);

    }

    public FileStorageException(String message,Throwable cause){
        super(message,cause);

    }

}
