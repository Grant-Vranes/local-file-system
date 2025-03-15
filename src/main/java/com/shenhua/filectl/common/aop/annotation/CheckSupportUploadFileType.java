package com.shenhua.filectl.common.aop.annotation;

import com.shenhua.filectl.common.constants.FileType;

import java.lang.annotation.*;

/**
 * @description: 用于校验文件contentType 和 文件后缀
 * @author:
 * @date: 2024/9/5 下午10:17
 * @Copyright：
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface CheckSupportUploadFileType {

    /**
     * 文件是第几个参数
     * 使用index计数，0、1、2
     * 由于存在接口传递参数的同时还传递文件
     * 如/file/uploadWithModule中第一个参数是module，第二个参数是file，因此使用此属性标记那个字段是文件
     * @return
     */
    int fileArgsIndex() default 0;

    /**
     * 文件的contentType
     * application/pdf
     * 有数据存在就校验
     * @return
     */
    FileType[] supportUploadFileType() default {};

    /**
     * 文件的后缀
     * 有数据存在就校验
     * .png .bat
     * @return
     */
    FileType[] supportUploadFileSuffix() default {};
}
