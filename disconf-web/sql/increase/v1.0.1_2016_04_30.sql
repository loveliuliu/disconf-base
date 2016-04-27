
-- 删除测试管理员角色
DELETE from role where role_id = 3;

-- 用户表增加邮箱 手机号
ALTER TABLE `user`
ADD COLUMN `email`  varchar(255) NULL COMMENT '邮件' AFTER `role_id`,
ADD COLUMN `phone`  varchar(255) NULL COMMENT '手机号' AFTER `email`;

-- 修改环境名称字段 区分大小写
ALTER TABLE `env`
MODIFY COLUMN `name`  varchar(255) BINARY CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'DEFAULT_ENV' COMMENT '环境名字' AFTER `env_id`;

-- 新增表

-- APP 用户关系表
drop table if exists disconf.user_app;

/*==============================================================*/
/* Table: user_app                                              */
/*==============================================================*/
create table disconf.user_app
(
   user_id              bigint(20) comment '用户id',
   app_id               bigint(20) comment 'appId',
   type                 varchar(20) comment '关系类型（normal 普通用户,auditor 审核员 ）'
);

-- 配置草稿表


drop table if exists disconf.config_draft;

/*==============================================================*/
/* Table: config_draft                                          */
/*==============================================================*/
create table disconf.config_draft
(
   config_draft_id      bigint(20) not null auto_increment comment '主键',
   config_id            bigint(20) comment '对应配置id',
   type                 tinyint(4) not null default 0 comment '配置文件/配置项',
   status               tinyint(4) not null default 1 comment '状态：2是已提交审核 1是正常 0是删除',
   name                 varchar(255) not null comment '配置文件名/配置项KeY名',
   value                text not null comment '0 配置文件：文件的内容，1 配置项：配置值',
   app_id               bigint(20) not null comment 'appid',
   app_name             varchar(255) comment '应用名称',
   version              varchar(255) not null default '' comment '版本',
   env_id               bigint(20) not null comment 'envid',
   env_name             varchar(255) comment '环境名称',
   user_id              bigint(20) not null comment '用户id',
   create_time          varchar(14) not null default '99991231235959' comment '创建时间',
   update_time          varchar(14) not null default '99991231235959' comment '修改时间',
   task_id              bigint(20) comment '任务id',
   draft_type           varchar(20) not null comment '草稿类型(新建 create,修改 modify,删除 delete)',
   primary key (config_draft_id)
);

alter table disconf.config_draft comment '配置草稿';

-- 任务表
drop table if exists disconf.Task;

/*==============================================================*/
/* Table: Task                                                  */
/*==============================================================*/
create table disconf.Task
(
   task_id              bigint(20) not null auto_increment comment '主键',
   app_id               bigint(20) not null comment '应用id',
   app_name             varchar(255) not null comment '应用名称',
   env_id               bigint(20) not null comment '环境id',
   env_name             varchar(255) not null comment '环境名称',
   version              varchar(255) not null comment '版本号',
   audit_status         varchar(20) not null comment '任务状态(待审核 wait_audit,通过 pass,不通过 fail,取消 cancel)',
   create_user_id       bigint(20) not null comment '创建用户',
   create_time          varchar(14) not null comment '创建时间',
   audit_user_id        bigint(20) comment '审核用户',
   audit_time           varchar(14) comment '审核时间',
   audit_comment        text comment '审核意见',
   exec_time            varchar(14) not null comment '执行时间(审核通过马上生效now,其他时间yyyyMMddHHmmss )',
   exec_status          varchar(14) comment '执行状态(初始化 init ,待执行 wait,已执行 done,已取消 cancel)',
   memo                 text comment '任务说明',
   primary key (task_id)
);

