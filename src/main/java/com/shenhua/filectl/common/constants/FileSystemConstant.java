package com.shenhua.filectl.common.constants;

import com.shenhua.filectl.common.utils.DatePlusUtil;
import com.shenhua.filectl.common.utils.FileUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 文件系统需要用到的实体
 */
@Component
public class FileSystemConstant {

    /**
     * 通用文件夹 /file_pool
     */
    public static String FILE_DIR;
    @Value("${file-system.file-dir}")
    public void setFileDir(String fileDir) {
        FileSystemConstant.FILE_DIR = fileDir;
    }

    /**
     * 存放平面图文件夹 /plane_drawing_dir
     * 实例
     */
    public static String PLANE_DRAWING_DIR;
    @Value("${file-system.plane-drawing-dir}")
    public void setPlaneDrawingDir(String planeDrawingDir) {
        FileSystemConstant.PLANE_DRAWING_DIR = planeDrawingDir;
    }


    /**
     * 上传的文件归于某模块
     */
    public enum Module {
        DEFAULT("DEFAULT", FILE_DIR), // 默认,无法确认module时使用
        PLANE_DRAWING("PLANE_DRAWING", PLANE_DRAWING_DIR), // 存放平面图文件夹

        ;

        private String module;
        private String folderName;

        Module(String module, String folderName) {
            this.module = module;
            this.folderName = FileUtil.concatenateWithSlash(folderName, DatePlusUtil.getCurrentDay());
        }

        public String getModule() {
            return module;
        }

        public String getFolderName() {
            return folderName;
        }
    }
}
