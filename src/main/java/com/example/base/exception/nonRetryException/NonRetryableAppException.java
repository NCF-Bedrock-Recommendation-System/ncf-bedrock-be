package com.example.base.exception.nonRetryException;

import com.example.base.exception.AppException;
import org.springframework.http.HttpStatus;

public abstract class NonRetryableAppException extends AppException {

    protected NonRetryableAppException(HttpStatus status, String message) {
        super(status, message);
    }
}
