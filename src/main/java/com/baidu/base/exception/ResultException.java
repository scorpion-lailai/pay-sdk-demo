package com.baidu.base.exception;

public class ResultException extends RuntimeException {

    private Errors errors;

    public ResultException(Errors errors) {
        this.errors = errors;
    }

    public Errors getErrors() {
        return errors;
    }

    public void setErrors(Errors errors) {
        this.errors = errors;
    }



}
