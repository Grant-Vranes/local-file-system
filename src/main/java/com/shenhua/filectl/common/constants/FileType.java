package com.shenhua.filectl.common.constants;

/**
 * 文件支持类型的枚举
 */
public enum FileType {
    JPG("image/jpeg", ".jpg"),
    JPEG("image/jpeg", ".jpeg"),
    PNG("image/png", ".png"),
    GIF("image/gif", ".gif"),
    BMP("image/bmp", ".bmp"),
    PDF("application/pdf", ".pdf"),
    WORD_97_2003("application/msword", ".doc"),
    WORD_2007("application/vnd.openxmlformats-officedocument.wordprocessingml.document", ".doc"),
    POWERPOINT_97_2007("application/vnd.ms-powerpoint", ".ppt"),
    POWERPOINT_2007("application/vnd.openxmlformats-officedocument.presentationml.presentation", ".pptx"),
    EXCEL_97_2003("application/vnd.ms-excel", ".xls"),
    EXCEL_2007("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", ".xlsx"),
    ;

    private final String contentType;
    private final String suffix;

    FileType(String contentType, String suffix) {
        this.contentType = contentType;
        this.suffix = suffix;
    }

    public String getContentType() {
        return contentType;
    }

    public String getSuffix() {
        return suffix;
    }
}