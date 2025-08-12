package com.zangyalong.mingzangpicturebackend.service;

import com.zangyalong.mingzangpicturebackend.model.dto.space.SpaceAddRequest;
import com.zangyalong.mingzangpicturebackend.model.entity.Space;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zangyalong.mingzangpicturebackend.model.entity.User;

/**
* @author mingzang
* @description 针对表【space(空间)】的数据库操作Service
* @createDate 2025-08-12 17:20:37
*/
public interface SpaceService extends IService<Space> {

    public void validSpace(Space space, boolean add);

    public void fillSpaceBySpaceLevel(Space space);

    public long addSpace(SpaceAddRequest spaceAddRequest, User loginUser);
}
