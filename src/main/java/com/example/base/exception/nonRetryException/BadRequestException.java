package com.example.base.exception.nonRetryException;

import org.springframework.http.HttpStatus;

public class BadRequestException extends NonRetryableAppException {

    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }

    public BadRequestException(String fieldName, Object fieldValue) {
        super(
                HttpStatus.BAD_REQUEST,
                String.format("Giá trị không hợp lệ với %s: %s", fieldName, fieldValue)
        );
    }
}
