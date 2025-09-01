package com.zangyalong.mingzangpicturebackend.domain.space.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zangyalong.mingzangpicturebackend.application.service.SpaceApplicationService;
import com.zangyalong.mingzangpicturebackend.application.service.SpaceUserApplicationService;
import com.zangyalong.mingzangpicturebackend.application.service.UserApplicationService;
import com.zangyalong.mingzangpicturebackend.domain.space.entity.Space;
import com.zangyalong.mingzangpicturebackend.domain.space.entity.SpaceUser;
import com.zangyalong.mingzangpicturebackend.domain.space.service.SpaceUserDomainService;
import com.zangyalong.mingzangpicturebackend.domain.space.valueobject.SpaceRoleEnum;
import com.zangyalong.mingzangpicturebackend.domain.user.entity.User;
import com.zangyalong.mingzangpicturebackend.infrastructure.exception.BusinessException;
import com.zangyalong.mingzangpicturebackend.infrastructure.exception.ErrorCode;
import com.zangyalong.mingzangpicturebackend.infrastructure.exception.ThrowUtils;
import com.zangyalong.mingzangpicturebackend.infrastructure.mapper.SpaceUserMapper;
import com.zangyalong.mingzangpicturebackend.interfaces.vo.space.SpaceUserVO;
import com.zangyalong.mingzangpicturebackend.interfaces.vo.space.SpaceVO;
import com.zangyalong.mingzangpicturebackend.interfaces.vo.user.UserVO;
import com.zangyalong.mingzangpicturebackend.interfaces.dto.spaceuser.SpaceUserAddRequest;
import com.zangyalong.mingzangpicturebackend.interfaces.dto.spaceuser.SpaceUserQueryRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author mingzang
* @description 针对表【space_user(空间用户关系表)】的数据库操作Service实现
* @createDate 2025-08-14 16:29:58
*/
@Service
public class SpaceUserDomainServiceImpl implements SpaceUserDomainService {

    @Resource
    private SpaceApplicationService spaceService;
    @Resource
    private UserApplicationService userService;

    @Override
    public QueryWrapper<SpaceUser> getQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest) {
        QueryWrapper<SpaceUser> queryWrapper = new QueryWrapper<>();
        if (spaceUserQueryRequest == null) {
            return queryWrapper;
        }
        // 从对象中取值
        Long id = spaceUserQueryRequest.getId();
        Long spaceId = spaceUserQueryRequest.getSpaceId();
        Long userId = spaceUserQueryRequest.getUserId();
        String spaceRole = spaceUserQueryRequest.getSpaceRole();
        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceId), "spaceId", spaceId);
        queryWrapper.eq(ObjUtil.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceRole), "spaceRole", spaceRole);
        return queryWrapper;
    }
}




