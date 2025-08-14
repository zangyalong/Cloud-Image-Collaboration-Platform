package com.zangyalong.mingzangpicturebackend.service;

import com.zangyalong.mingzangpicturebackend.model.entity.SpaceUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zangyalong.mingzangpicturebackend.model.vo.SpaceVO;

import java.util.List;

/**
* @author mingzang
* @description 针对表【space_user(空间用户关系表)】的数据库操作Service
* @createDate 2025-08-14 16:29:58
*/
public interface SpaceUserService extends IService<SpaceUser> {

    /**
     * 获取我加入的空间列表
     * @param loginUserId 当前登录用户 id
     * @return
     */
    List<SpaceVO> listMyTeamSpace(Long loginUserId);
}
