package com.shenhua.filectl.common.web.domain;

/**
 * Describe: Ajax 响应类型
 *
 */
public enum ResultCode {

    SUCCESS(200, "成功"),
    FAILURE(500, "失败"),

    /**
     * 业务相关
     */
    MISS_PARAMETER(1, "缺少必要参数"),

    /**
     * 文件系统
     */
    FILE_TRANSFER_ERROR(1, "文件传输异常"),
    FILE_NOT_FOUND(1, "没有找到文件"),
    FILE_DOMAIN_WAS_NOT_SPECIFIED(1, "未指定文件所在域"),
    NO_FILE_RECORD_FOUND(1, "未找到文件记录"),
    RENAME_FAILED(1, "重命名失败"),
    FILE_VERSION_INVALID(1, "文件版本格式不合法"),
    BUILD_FILE_DIRECTORY_IS_ABNORMAL(1, "构建文件目录异常，可能在目标文件夹存在同名文件"),
    AN_ATTACHMENT_WITH_THE_SAME_NAME_ALREADY_EXISTS(1, "已存在同名文件的附件"),
    UNSUPPORTED_UPLOAD_FILE_TYPE(1, "不支持上传的文件类型"),

    /**
     * caffeine
     */
    NOT_FOUND_IN_CACHE(1, "缓存中未找到"),
    ;

    /**
     * 标识
     * */
    private int code;

    /**
     * 消息
     * */
    private String message;

    /**
     * 构 造 方 法
     * */
    ResultCode(int code,String message){
        this.code = code;
        this.message = message;
    }

    /**
     * 获 取 标 识
     * */
    public int code(){
        return code;
    }

    /**
     * 获 取 消 息
     * */
    public String message(){
        return message;
    }

    public static Integer getCodeForName(String name) {
        for (ResultCode resultCode : values()) {
            if (resultCode.name().equals(name)) {
                return resultCode.code;
            }
        }
        // 如果错误无法获取，就判定为FAILURE
        return FAILURE.code;
    }
}
