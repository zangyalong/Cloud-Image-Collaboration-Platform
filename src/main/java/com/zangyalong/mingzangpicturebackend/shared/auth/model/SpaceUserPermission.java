package com.zangyalong.mingzangpicturebackend.shared.auth.model;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class SpaceUserPermission implements Serializable {

    /**
     * 权限键
     */
    private String key;

    /**
     * 权限名称
     */
    private String name;

    /**
     * 权限描述
     */
    private String description;

    @Serial
    private static final long serialVersionUID = 1L;

}

