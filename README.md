# local-file-system
基于服务器磁盘的本地文件存储方案

仅提供后端方案



## 环境

- JDK11
- linux/windows/mac



## 应用场景

- 适用于ToB业务，中小企业的单体服务，仅使用磁盘存储文件的解决方案

- 仅使用服务器磁盘存储

- 与业务实体相结合的文件存储方案，如企业中对于设备信息维护，需要上传对应文件，类型包括”设备采购合同文件“，”设备验收照片“等，在本项目中的维护形式可见如下图。

  <img src="./README.assets/image-20250315162924364.png" alt="image-20250315162924364" style="zoom:70%;" /> 





## 效果示例

<img src="./README.assets/image-20250315220515807.png" alt="image-20250315220515807" style="zoom:67%;" />	 

<img src="./README.assets/image-20250315220553810.png" alt="image-20250315220553810" style="zoom:67%;" /> <img src="./README.assets/image-20250315220713103.png" alt="image-20250315220713103" style="zoom:67%;" /> 





## 表结构设计

本项目使用sqlserver作为数据库，目前仅提供sqlserver的表脚本初始化sql。

本项目持久性框架使用Mybatis-Plus，可轻松切换至Oracle，MySql等数据库。

![image-20250315165824369](./README.assets/image-20250315165824369.png) 

本项目主要使用两个表

- t_file 文件信息的存储表，包含文件名称、类型后缀、大小、相对文件路径(目前file_path表相对)、module模块名(可对应配置存储目录)。注意，此表中module_name模块用于分类。
- tr_link_file 文件与模块的关联表，使用link_id表示其他各种表(设备表、工厂表)的id主键， file_id表示t_file的id主键。module_name表示属于哪个模块的文件关联（可以表示此link_id的来源）。

见如下图，可表示为

- 设备需要维护<u>设备照片</u>信息，我们需要提前定义module_name为”`Equipment_photo`“，在前端通过api上传后存储到t_file，而实际文件存储到对应磁盘位置，并返回FileInfo信息(fileId，fileName)，前端得到已经存储的文件id后，可在用户点击保存信息时，将fileId集合携带，系统再进行关联操作，在tr_link_file表中维护对应的关联数据。（缺点：用户上传文件后，如果不进行保存设备信息，那么tr_link_file的关联是无法建立的。）

![image-20250315165753060](./README.assets/image-20250315165753060.png) 





## 测试API

使用apifox，提供apifox.json文件 [此处](./api_test_script)

![image-20250315215928166](./README.assets/image-20250315215928166.png) 
