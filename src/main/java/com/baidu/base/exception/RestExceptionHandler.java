package com.baidu.base.exception;

import com.baidu.base.json.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

@ControllerAdvice
public class RestExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);


    @ExceptionHandler(NullPointerException.class)
    @ResponseBody
    public String nullPointerExceptionHandler(NullPointerException ex) {
        return error(ex);
    }
    // 类型转换异常

    @ExceptionHandler(ClassCastException.class)
    @ResponseBody
    public String classCastExceptionHandler(ClassCastException ex) {
        return error(ex);
    }

    // IO异常
    @ExceptionHandler(IOException.class)
    @ResponseBody
    public String iOExceptionHandler(IOException ex) {
        return error(ex);
    } // 未知方法异常

    @ExceptionHandler(NoSuchMethodException.class)
    @ResponseBody
    public String noSuchMethodExceptionHandler(NoSuchMethodException ex) {
        return error(ex);
    }

    @ExceptionHandler(IndexOutOfBoundsException.class)
    @ResponseBody
    public String indexOutOfBoundsExceptionHandler(IndexOutOfBoundsException ex) {
        return error(ex);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    @ResponseBody
    public String requestNotReadable(HttpMessageNotReadableException ex) {
        return error(ex);
    }

    @ExceptionHandler({TypeMismatchException.class})
    @ResponseBody
    public String requestTypeMismatch(TypeMismatchException ex) {
        return error(ex);
    }

    @ExceptionHandler({MissingServletRequestParameterException.class})
    @ResponseBody
    public String requestMissingServletRequest(MissingServletRequestParameterException ex) {
        return error(ex);
    }

    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    @ResponseBody
    public String request405(HttpRequestMethodNotSupportedException ex) {
        return error(ex);
    }

    @ExceptionHandler({HttpMediaTypeNotAcceptableException.class})
    @ResponseBody
    public String request406(HttpMediaTypeNotAcceptableException ex) {
        return error(ex);
    }

    @ExceptionHandler({ConversionNotSupportedException.class, HttpMessageNotWritableException.class})
    @ResponseBody
    public String server500(RuntimeException ex) {
        return error(ex);
    }

    @ExceptionHandler({RuntimeException.class})
    @ResponseBody
    public String exception(RuntimeException ex) {
        return error(ex);
    }

    // 统一错误返回
    @ExceptionHandler({ResultException.class})
    @ResponseBody
    public String ResultException(ResultException ex) {
        return error(ex, ex.getErrors(), false);
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String globalExceptionHandle(Exception ex) {
        return error(ex);
    }

    private static String getExceptionInfo(Exception ex) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(out);
        ex.printStackTrace(printStream);
        String rs = out.toString();
        printStream.close();

        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            log.error("统一错误返回", ex);
        }
        return rs;
    }

    public String error(Exception ex) {
        return error(ex, null);
    }

    public String error(Exception ex, Errors error) {
        return error(ex, error, true);
    }

    public String error(Exception ex, Errors error, Boolean printLog) {
        if (printLog)
            log.error(getExceptionInfo(ex));

        if (error == null)
            return Result.fail(Errors.ERROR);
        else
            return Result.fail(error);
    }
}
