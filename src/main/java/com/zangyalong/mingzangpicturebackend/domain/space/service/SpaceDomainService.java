package com.zangyalong.mingzangpicturebackend.domain.space.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zangyalong.mingzangpicturebackend.domain.space.entity.Space;
import com.zangyalong.mingzangpicturebackend.domain.user.entity.User;
import com.zangyalong.mingzangpicturebackend.interfaces.dto.space.SpaceAddRequest;
import com.zangyalong.mingzangpicturebackend.interfaces.dto.space.SpaceQueryRequest;
import com.zangyalong.mingzangpicturebackend.interfaces.vo.space.SpaceVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author mingzang
* @description 针对表【space(空间)】的数据库操作Service
* @createDate 2025-08-12 17:20:37
*/
public interface SpaceDomainService  {

    public void fillSpaceBySpaceLevel(Space space);

    QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest);

    void checkSpaceAuth(User loginUser, Space oldSpace);

}
