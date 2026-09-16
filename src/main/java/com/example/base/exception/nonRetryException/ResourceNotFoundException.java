package com.example.base.exception.nonRetryException;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends NonRetryableAppException {

    public ResourceNotFoundException(String resource, String field, Object value) {
        super(
                HttpStatus.NOT_FOUND,
                String.format("Không tìm thấy %s với %s: %s", resource, field, value)
        );
    }

    public ResourceNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
