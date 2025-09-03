CREATE TABLE space_user
(
    id          bigint AUTO_INCREMENT COMMENT 'id' PRIMARY KEY,
    spaceId     bigint                             NOT NULL COMMENT '空间 id',
    userId      bigint                             NOT NULL COMMENT '用户 id',
    spaceRole        varchar(256) DEFAULT 'member'      NOT NULL COMMENT '用户角色: owner, admin, member',
    createTime  datetime     DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updateTime  datetime     DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    isDelete    tinyint      DEFAULT 0                 NOT NULL COMMENT '是否删除',
    INDEX idx_spaceId (spaceId),
    INDEX idx_userId (userId)
) COMMENT '空间用户关系表';

ALTER TABLE `space_user`
    ADD (
        isDelete   tinyint    DEFAULT 0   NOT NULL COMMENT '是否删除'
    );