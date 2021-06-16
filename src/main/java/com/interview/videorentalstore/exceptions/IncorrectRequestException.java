package com.interview.videorentalstore.exceptions;

import java.util.ArrayList;
import java.util.List;


public class IncorrectRequestException
        extends RuntimeException {

    private final List<String> validationErrors;

    public IncorrectRequestException() {
        super();
        this.validationErrors = new ArrayList<>();
    }

    public IncorrectRequestException(List<String> validationErrors) {
        super();
        this.validationErrors = validationErrors;
    }

    public IncorrectRequestException(String message) {
        super(message);
        this.validationErrors = new ArrayList<>();
    }

    public IncorrectRequestException(String message, List<String> validationErrors) {
        super(message);
        this.validationErrors = validationErrors;
    }

    public IncorrectRequestException(String message, Throwable cause) {
        super(message, cause);
        this.validationErrors = new ArrayList<>();
    }

    public IncorrectRequestException(String message, Throwable cause, List<String> validationErrors) {
        super(message, cause);
        this.validationErrors = validationErrors;
    }

    public IncorrectRequestException(Throwable cause) {
        super(cause);
        this.validationErrors = new ArrayList<>();
    }

    public IncorrectRequestException(Throwable cause, List<String> validationErrors) {
        super(cause);
        this.validationErrors = validationErrors;
    }

    protected IncorrectRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.validationErrors = new ArrayList<>();
    }

    protected IncorrectRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, List<String> validationErrors) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.validationErrors = validationErrors;
    }

    public List<String> getValidationErrors() {
        return validationErrors;
    }
}
