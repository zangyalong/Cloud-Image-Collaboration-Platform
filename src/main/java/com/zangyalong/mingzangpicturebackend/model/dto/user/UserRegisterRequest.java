package com.zangyalong.mingzangpicturebackend.model.dto.user;

import lombok.Data;

@Data
public class UserRegisterRequest {

    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 确认密码
     */
    private String checkPassword;
}
