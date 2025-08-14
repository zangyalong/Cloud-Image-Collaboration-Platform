package com.zangyalong.mingzangpicturebackend.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zangyalong.mingzangpicturebackend.model.entity.Space;
import com.zangyalong.mingzangpicturebackend.model.entity.SpaceUser;
import com.zangyalong.mingzangpicturebackend.model.vo.SpaceVO;
import com.zangyalong.mingzangpicturebackend.service.SpaceService;
import com.zangyalong.mingzangpicturebackend.service.SpaceUserService;
import com.zangyalong.mingzangpicturebackend.mapper.SpaceUserMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
* @author mingzang
* @description 针对表【space_user(空间用户关系表)】的数据库操作Service实现
* @createDate 2025-08-14 16:29:58
*/
@Service
public class SpaceUserServiceImpl extends ServiceImpl<SpaceUserMapper, SpaceUser>
    implements SpaceUserService{

    @Resource
    private SpaceService spaceService;
    @Override
    public List<SpaceVO> listMyTeamSpace(Long loginUserId) {

        // 1. 根据 userId 从 space_user 表查询所有关联的 spaceId
        QueryWrapper<SpaceUser> spaceUserQueryWrapper = new QueryWrapper<>();
        spaceUserQueryWrapper.eq("userId", loginUserId);
        List<SpaceUser> spaceUserList = this.list(spaceUserQueryWrapper);
        if (CollUtil.isEmpty(spaceUserList)) {
            return List.of();
        }
        List<Long> spaceIds = spaceUserList.stream().map(SpaceUser::getSpaceId).collect(Collectors.toList());
        // 2. 根据 spaceIds 查询所有空间详情
        List<Space> spaceList = spaceService.listByIds(spaceIds);
        // 3. 封装为 SpaceVO 返回 (这里的 SpaceVO 应该已经包含了图片数量和容量信息，如果没有，需要在这里补充)
        return spaceList.stream().map(space -> {
            SpaceVO spaceVO = new SpaceVO();
            BeanUtils.copyProperties(space, spaceVO);
            // todo: 补充图片数量和已用容量的逻辑，如果 SpaceVO 需要的话
            return spaceVO;
        }).collect(Collectors.toList());
    }
}




