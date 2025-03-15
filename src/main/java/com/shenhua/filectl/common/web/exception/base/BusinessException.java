package com.shenhua.filectl.common.web.exception.base;

import com.shenhua.filectl.common.web.domain.ResultCode;
import lombok.Data;

/**
 * 业 务 异 常
 *
 * */
@Data
public class BusinessException extends RuntimeException {

    private String resultCode;
    private String msg;


    public BusinessException() {
    }

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(Throwable cause) {
        super(cause);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(String resultCode, String msg) {
        super(resultCode);
        this.resultCode = resultCode;
        this.msg = msg;
    }

    public BusinessException(ResultCode resultCode, String msg) {
        super(resultCode.name());
        this.resultCode = resultCode.name();
        this.msg = msg;
    }

    public BusinessException(ResultCode resultCode) {
        super(resultCode.name());
    }
}