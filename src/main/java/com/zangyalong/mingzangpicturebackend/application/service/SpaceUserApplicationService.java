package com.zangyalong.mingzangpicturebackend.application.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zangyalong.mingzangpicturebackend.interfaces.dto.spaceuser.SpaceUserAddRequest;
import com.zangyalong.mingzangpicturebackend.interfaces.dto.spaceuser.SpaceUserQueryRequest;
import com.zangyalong.mingzangpicturebackend.domain.space.entity.SpaceUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zangyalong.mingzangpicturebackend.interfaces.vo.space.SpaceUserVO;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

/**
* @author mingzang
* @description 针对表【space_user(空间用户关系表)】的数据库操作Service
* @createDate 2025-08-14 16:29:58
*/
public interface SpaceUserApplicationService extends IService<SpaceUser> {

    long addSpaceUser(SpaceUserAddRequest spaceUserAddRequest);

    void validSpaceUser(SpaceUser spaceUser, boolean add);

    QueryWrapper<SpaceUser> getQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest);

    SpaceUserVO getSpaceUserVO(SpaceUser spaceUser, HttpServletRequest request);

    List<SpaceUserVO> getSpaceUserVOList(List<SpaceUser> spaceUserList);
}
