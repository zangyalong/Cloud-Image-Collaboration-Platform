package com.zangyalong.mingzangpicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zangyalong.mingzangpicturebackend.model.dto.picture.PictureQueryRequest;
import com.zangyalong.mingzangpicturebackend.model.dto.space.SpaceAddRequest;
import com.zangyalong.mingzangpicturebackend.model.dto.space.SpaceQueryRequest;
import com.zangyalong.mingzangpicturebackend.model.entity.Picture;
import com.zangyalong.mingzangpicturebackend.model.entity.Space;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zangyalong.mingzangpicturebackend.model.entity.User;
import com.zangyalong.mingzangpicturebackend.model.vo.SpaceVO;
import javax.servlet.http.HttpServletRequest;

/**
* @author mingzang
* @description 针对表【space(空间)】的数据库操作Service
* @createDate 2025-08-12 17:20:37
*/
public interface SpaceService extends IService<Space> {

    public void validSpace(Space space, boolean add);

    public void fillSpaceBySpaceLevel(Space space);

    public long addSpace(SpaceAddRequest spaceAddRequest, User loginUser);

    boolean deleteSpace(Long id, User loginUser);

    QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest);

    Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage);

    void checkSpaceAuth(User loginUser, Space oldSpace);

    SpaceVO getSpaceVO(Space space, HttpServletRequest request);


}
