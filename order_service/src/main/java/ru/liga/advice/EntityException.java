package ru.liga.advice;

public class EntityException extends RuntimeException {

    private final ExceptionStatus exceptionStatus;

    public EntityException(ExceptionStatus exceptionStatus) {
        this.exceptionStatus = exceptionStatus;
    }

    public ExceptionStatus getErrorType() {
        return exceptionStatus;
    }

}
