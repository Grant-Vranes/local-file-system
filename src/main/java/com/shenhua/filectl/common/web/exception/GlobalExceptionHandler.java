package com.shenhua.filectl.common.web.exception;

import com.shenhua.filectl.common.web.domain.Result;
import com.shenhua.filectl.common.web.exception.base.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLSyntaxErrorException;

/**
 * 全 局 异 常 处 理
 */
@Order(2)
@RestControllerAdvice
// @Aspect
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 业 务 异 常
     */
    @ExceptionHandler(BusinessException.class)
    public Result businessException(BusinessException e) {
        // return ResponseEntity.status(ResultCode.getCodeForName(e.getMessage())).body(Result.failure(e.getMessage()));
        LOG.error("GlobalExceptionHandler BusinessException =>{}", e);
        return Result.failure(e.getMessage(), e.getMsg());
    }

    /**
     * 数据校验的异常回报
     */
    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public Result methodArgumentNotValidException(MethodArgumentNotValidException e) {
        LOG.error("GlobalExceptionHandler methodArgumentNotValidException =>{}", e);
        return Result.failure("发生运行时异常，请根据TRACE-ID查询日志");
    }


    /**
     * 不 支 持 的 请 求 类 型
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result handleException(HttpRequestMethodNotSupportedException e) {
        return Result.failure("不支持" + e.getMethod() + "请求");
    }

    /**
     * 未 知 的 运 行 时 异 常
     */
    @ExceptionHandler(RuntimeException.class)
    public Result notFount(RuntimeException e) {
        // e.printStackTrace();
        LOG.error("GlobalExceptionHandler RuntimeException=>{}", e);
        // return Result.failure("运行时异常：" + e.getMessage());
        return Result.failure("发生运行时异常，请根据TRACE-ID查询日志");
    }

    /**
     * 未 知 的 运 行 时 异 常
     */
    @ExceptionHandler(SQLSyntaxErrorException.class)
    public Result notColumn(RuntimeException e) {
        // e.printStackTrace();
        LOG.error("GlobalExceptionHandler SQLSyntaxErrorException=>{}", e);
        return Result.failure("列不存在：" + e.getMessage());
    }

    /**
     * 系 统 异 常
     */
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        // e.printStackTrace();
        LOG.error("GlobalExceptionHandler Exception=>{}", e);
        return Result.failure("服务器错误，请联系管理员");
    }

    /**
     * 异常细节
     * @param e
     * @return
     */
    private String getExceptionDetail(Exception e) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(e.getClass() + System.getProperty("line.separator"));
        stringBuilder.append(e.getLocalizedMessage() + System.getProperty("line.separator"));
        StackTraceElement[] arr = e.getStackTrace();
        for (int i = 0; i < arr.length; i++) {
            stringBuilder.append(arr[i].toString() + System.getProperty("line.separator"));
        }
        return stringBuilder.toString();
    }
}
