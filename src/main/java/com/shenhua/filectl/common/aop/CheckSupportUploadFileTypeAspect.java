package com.shenhua.filectl.common.aop;

import com.shenhua.filectl.common.aop.annotation.CheckSupportUploadFileType;
import com.shenhua.filectl.common.constants.FileType;
import com.shenhua.filectl.common.utils.FileUtil;
import com.shenhua.filectl.common.web.domain.ResultCode;
import com.shenhua.filectl.common.web.exception.base.BusinessException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

/**
 * 为标注@CheckSupportUploadFileType注解的接口做判断，判断其接收的MultipartFile文件是否在规范的上传文件类型范围内
 *
 */
@Aspect
@Component
public class CheckSupportUploadFileTypeAspect {

    /**
     * 切 面 编 程
     * */
    @Pointcut("@annotation(com.shenhua.filectl.common.aop.annotation.CheckSupportUploadFileType) || @within(com.shenhua.filectl.common.aop.annotation.CheckSupportUploadFileType)")
    public void dsPointCut() { }

    /**
     * 处 理
     * */
    @Before("dsPointCut()")
    private void around(JoinPoint joinPoint) throws Throwable {
        CheckSupportUploadFileType annotation = getAnnotation((ProceedingJoinPoint) joinPoint);
        FileType[] supportUploadFileType = annotation.supportUploadFileType();
        FileType[] supportUploadFileSuffix = annotation.supportUploadFileSuffix();

        try {
            MultipartFile file = (MultipartFile) joinPoint.getArgs()[annotation.fileArgsIndex()];
            if (supportUploadFileType.length > 0 &&
                    !Arrays.stream(supportUploadFileType)
                            .anyMatch(item -> Objects.equals(item.getContentType(), file.getContentType()))) {
                throw new BusinessException(ResultCode.UNSUPPORTED_UPLOAD_FILE_TYPE);
            }

            if (supportUploadFileSuffix.length > 0) {
                // 获取文件后缀
                String fileSuffix = FileUtil.getFileSuffix(file.getOriginalFilename());
                if (!Arrays.stream(supportUploadFileSuffix).anyMatch(item -> Objects.equals(item.getSuffix(), fileSuffix))) {
                    throw new BusinessException(ResultCode.UNSUPPORTED_UPLOAD_FILE_TYPE);
                }
            }
        } catch (Exception e) {
            // e.printStackTrace();
            throw new BusinessException(ResultCode.UNSUPPORTED_UPLOAD_FILE_TYPE);
        }
    }

    /**
     * 获取注解
     * */
    public CheckSupportUploadFileType getAnnotation(ProceedingJoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Class<? extends Object> targetClass = point.getTarget().getClass();
        CheckSupportUploadFileType targetExcel = targetClass.getAnnotation(CheckSupportUploadFileType.class);
        if ( targetExcel != null) {
            return targetExcel;
        } else {
            Method method = signature.getMethod();
            CheckSupportUploadFileType checkSupportUploadFileType = method.getAnnotation(CheckSupportUploadFileType.class);
            return checkSupportUploadFileType;
        }
    }
}
