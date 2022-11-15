package com.baidu.base.json;

import com.baidu.base.exception.Errors;
import com.baidu.base.language.LocalSource;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Data
@NoArgsConstructor
@Component
public class Result {


    private String code;
    private String message;
    private Object data;

    private static LocalSource localSource;

    @Autowired
    private LocalSource localSourceTmp;

    @PostConstruct
    public void init() {
        localSource = localSourceTmp;

    }

    @Override
    public String toString() {
        return JSONUtils.objectForJson(this);
    }

    public static String ok() {
        Result result = new Result();

        result.code = Errors.OK.getCode();
        result.message = getMessage(Errors.OK.getCode());

        return result.toString();
    }

    public static String ok(Object data) {
        Result result = new Result();

        result.code = Errors.OK.getCode();
        result.message = getMessage(Errors.OK.getCode());
        result.data = data;

        return result.toString();
    }

    public static String fail(Errors error) {
        Result result = new Result();

        result.code = error.getCode();
        result.message = getMessage(error.getCode());

        return result.toString();
    }

    public static String getMessage(String code) {
        return localSource.getMessage(code);
    }

}
