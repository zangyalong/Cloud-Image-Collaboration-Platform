package com.zangyalong.mingzangpicturebackend.application.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zangyalong.mingzangpicturebackend.infrastructure.common.DeleteRequest;
import com.zangyalong.mingzangpicturebackend.interfaces.dto.user.UserLoginRequest;
import com.zangyalong.mingzangpicturebackend.interfaces.dto.user.UserQueryRequest;
import com.zangyalong.mingzangpicturebackend.domain.user.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zangyalong.mingzangpicturebackend.interfaces.dto.user.UserRegisterRequest;
import com.zangyalong.mingzangpicturebackend.interfaces.vo.user.LoginUserVO;
import com.zangyalong.mingzangpicturebackend.interfaces.vo.user.UserVO;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Set;

/**
* @author mingzang
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2025-07-30 10:34:51
*/
public interface UserApplicationService {


    @Transactional
    long userRegister(UserRegisterRequest userRegisterRequest);

    LoginUserVO userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request);

    User getLoginUser(HttpServletRequest request);

    boolean userLogout(HttpServletRequest request);

    LoginUserVO getLoginUserVO(User user);

    UserVO getUserVO(User user);

    List<UserVO> getUserVOList(List<User> userList);

    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    long addUser(User user);

    User getUserById(long id);

    UserVO getUserVOById(long id);

    boolean deleteUser(DeleteRequest deleteRequest);

    void updateUser(User user);

    Page<UserVO> listUserVOByPage(UserQueryRequest userQueryRequest);

    List<User> listByIds(Set<Long> userIdSet);

    String getEncryptPassword(String userPassword);
}
