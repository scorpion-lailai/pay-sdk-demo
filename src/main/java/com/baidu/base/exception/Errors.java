package com.baidu.base.exception;


public enum Errors {
    OK("200"),
    ERROR("-1"),
    CODE_ERROR("10001"),
    MAIL_PRESENT("10002"),
    TOKEN_INVALID("10003"),
    MAIL_NOT_PRESENT("10004"),
    WRONG_PASSWORD("10005"),
    FILE_IS_NULL("10006"),
    IP_BLOCKING("10007"),
    PAY_FAIL("10008"),
    VIDEO_PROCESS_FAIL("10009"),
    PRODUCT_IS_NULL("10010"),
    CREDIT_NOT_ENOUGH("10011"),
    NO_SUBSCRIBE_PRODUCT("10012"),
    USER_NO_VIP("10013"),
    ;
    private String code;

    Errors(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
