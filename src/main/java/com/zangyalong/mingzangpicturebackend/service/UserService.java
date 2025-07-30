package com.zangyalong.mingzangpicturebackend.service;

import com.zangyalong.mingzangpicturebackend.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zangyalong.mingzangpicturebackend.model.vo.LoginUserVO;
import jakarta.servlet.http.HttpServletRequest;

/**
* @author mingzang
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2025-07-30 10:34:51
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);


    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     *
     * @return 用户脱敏后的信息 LoginUserVO
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    LoginUserVO getLoginUserVo(User loginUser);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);
}
