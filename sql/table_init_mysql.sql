-- 文件信息存储表
DROP TABLE IF EXISTS `t_file`;
CREATE TABLE `t_file` (
                          `id` varchar(32) NOT NULL COMMENT '主键',
                          `file_name` varchar(1000) NOT NULL COMMENT '文件名称',
                          `file_path` varchar(1000) NOT NULL COMMENT '文件相对路径',
                          `type` varchar(32) DEFAULT NULL COMMENT '文件类型：.png等',
                          `size` varchar(32) DEFAULT NULL COMMENT '文件大小',
                          `module_name` varchar(255) DEFAULT NULL COMMENT 'module 对应着具体的文件夹',
                          `deleted` int DEFAULT 0 COMMENT '逻辑删除标记',
                          `create_time` datetime(6) DEFAULT NULL,
                          `create_by` varchar(64) DEFAULT NULL,
                          `create_name` varchar(500) DEFAULT NULL,
                          `update_time` datetime(6) DEFAULT NULL,
                          `update_by` varchar(64) DEFAULT NULL,
                          `update_name` varchar(500) DEFAULT NULL,
                          PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件列表';

-- 各个模块的表 ID 与文件 ID 的关联
DROP TABLE IF EXISTS `tr_link_file`;
CREATE TABLE `tr_link_file` (
                                `id` varchar(32) NOT NULL COMMENT '主键',
                                `module_name` varchar(255) DEFAULT NULL COMMENT '功能模块标识名',
                                `link_id` varchar(32) NOT NULL COMMENT '功能模块表ID',
                                `file_id` varchar(255) NOT NULL COMMENT '文件ID，为 t_file 表 id',
                                PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='各个模块的表ID与文件ID的关联';