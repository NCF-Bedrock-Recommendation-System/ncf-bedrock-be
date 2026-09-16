package com.example.base.exception.nonRetryException;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends NonRetryableAppException {

    public ForbiddenException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}
