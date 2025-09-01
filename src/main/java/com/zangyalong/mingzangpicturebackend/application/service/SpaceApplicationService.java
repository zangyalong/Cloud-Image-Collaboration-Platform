package com.zangyalong.mingzangpicturebackend.application.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zangyalong.mingzangpicturebackend.interfaces.dto.space.SpaceAddRequest;
import com.zangyalong.mingzangpicturebackend.interfaces.dto.space.SpaceQueryRequest;
import com.zangyalong.mingzangpicturebackend.domain.space.entity.Space;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zangyalong.mingzangpicturebackend.domain.user.entity.User;
import com.zangyalong.mingzangpicturebackend.interfaces.vo.space.SpaceVO;
import javax.servlet.http.HttpServletRequest;

/**
* @author mingzang
* @description 针对表【space(空间)】的数据库操作Service
* @createDate 2025-08-12 17:20:37
*/
public interface SpaceApplicationService extends IService<Space> {

    public void fillSpaceBySpaceLevel(Space space);

    public long addSpace(SpaceAddRequest spaceAddRequest, User loginUser);

    QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest);

    Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage, HttpServletRequest request);

    void checkSpaceAuth(User loginUser, Space oldSpace);

    SpaceVO getSpaceVO(Space space, HttpServletRequest request);


}
