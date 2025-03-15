package com.shenhua.filectl.common.constants;

public class GlobalConstant {

    /**
     * 删除标记
     */
    public enum Deleted_Flag {
        NOT_DELETE(false, 0),
        DELETED(true, 1);

        private Boolean code;

        private Integer intVal;

        Deleted_Flag(Boolean code, Integer intVal) {
            this.code = code;
            this.intVal = intVal;
        }

        public Boolean getCode() {
            return this.code;
        }

        public Integer getIntVal() {
            return this.intVal;
        }
    }
}
