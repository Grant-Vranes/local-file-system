-- dbo.t_file definition 文件信息存储表
-- Drop table
-- DROP TABLE dbo.t_file;
CREATE TABLE dbo.t_file (
    id nvarchar(32) COLLATE Chinese_PRC_CS_AI NOT NULL,
    file_name nvarchar(1000) COLLATE Chinese_PRC_CS_AI NOT NULL,
    file_path nvarchar(1000) COLLATE Chinese_PRC_CS_AI NOT NULL,
    [type] nvarchar(32) COLLATE Chinese_PRC_CS_AI NULL,
    [size] nvarchar(32) COLLATE Chinese_PRC_CS_AI NULL,
    module_name nvarchar(255) COLLATE Chinese_PRC_CS_AI NULL,
    deleted int DEFAULT 0 NULL,
    create_time datetime2(6) NULL,
    create_by nvarchar(64) COLLATE Chinese_PRC_CS_AI NULL,
    create_name nvarchar(500) COLLATE Chinese_PRC_CS_AI NULL,
    update_time datetime2(6) NULL,
    update_by nvarchar(64) COLLATE Chinese_PRC_CS_AI NULL,
    update_name nvarchar(500) COLLATE Chinese_PRC_CS_AI NULL,
    CONSTRAINT t_file_PK PRIMARY KEY (id)
);

-- Extended properties
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'文件列表', @level0type=N'Schema', @level0name=N'dbo', @level1type=N'Table', @level1name=N't_file';
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'文件名称', @level0type=N'Schema', @level0name=N'dbo', @level1type=N'Table', @level1name=N't_file', @level2type=N'Column', @level2name=N'file_name';
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'文件相对路径', @level0type=N'Schema', @level0name=N'dbo', @level1type=N'Table', @level1name=N't_file', @level2type=N'Column', @level2name=N'file_path';
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'文件类型：.png等', @level0type=N'Schema', @level0name=N'dbo', @level1type=N'Table', @level1name=N't_file', @level2type=N'Column', @level2name=N'type';
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'文件大小', @level0type=N'Schema', @level0name=N'dbo', @level1type=N'Table', @level1name=N't_file', @level2type=N'Column', @level2name=N'size';
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'module对应着具体的文件夹（为了做分类）（可以在FileSystemConstant.Module中找到关联）', @level0type=N'Schema', @level0name=N'dbo', @level1type=N'Table', @level1name=N't_file', @level2type=N'Column', @level2name=N'module_name';



-- dbo.tr_link_file definition 各个模块的表ID与文件ID的关联
-- Drop table
-- DROP TABLE dbo.tr_link_file;
CREATE TABLE dbo.tr_link_file (
    id nvarchar(32) COLLATE Chinese_PRC_CS_AI NOT NULL,
    module_name nvarchar(255) COLLATE Chinese_PRC_CS_AI NULL,
    link_id nvarchar(32) COLLATE Chinese_PRC_CS_AI NOT NULL,
    file_id nvarchar(255) COLLATE Chinese_PRC_CS_AI NOT NULL,
    CONSTRAINT tr_link_file_PK PRIMARY KEY (id)
);

-- Extended properties
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'各个模块的表ID与文件ID的关联', @level0type=N'Schema', @level0name=N'dbo', @level1type=N'Table', @level1name=N'tr_link_file';
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'功能模块标识名，如DEFAULT(默认文件库)，EQUIPMENT_FILE(设备上传附件)', @level0type=N'Schema', @level0name=N'dbo', @level1type=N'Table', @level1name=N'tr_link_file', @level2type=N'Column', @level2name=N'module_name';
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'功能模块表ID，如为设备添加的附件，应该关联到设备表id', @level0type=N'Schema', @level0name=N'dbo', @level1type=N'Table', @level1name=N'tr_link_file', @level2type=N'Column', @level2name=N'link_id';
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'文件ID，为t_file表id', @level0type=N'Schema', @level0name=N'dbo', @level1type=N'Table', @level1name=N'tr_link_file', @level2type=N'Column', @level2name=N'file_id';

