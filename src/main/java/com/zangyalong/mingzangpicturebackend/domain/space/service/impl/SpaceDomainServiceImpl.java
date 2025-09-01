package com.zangyalong.mingzangpicturebackend.domain.space.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zangyalong.mingzangpicturebackend.application.service.PictureApplicationService;
import com.zangyalong.mingzangpicturebackend.application.service.SpaceApplicationService;
import com.zangyalong.mingzangpicturebackend.application.service.SpaceUserApplicationService;
import com.zangyalong.mingzangpicturebackend.application.service.UserApplicationService;
import com.zangyalong.mingzangpicturebackend.domain.picture.entity.Picture;
import com.zangyalong.mingzangpicturebackend.domain.space.entity.Space;
import com.zangyalong.mingzangpicturebackend.domain.space.entity.SpaceUser;
import com.zangyalong.mingzangpicturebackend.domain.space.service.SpaceDomainService;
import com.zangyalong.mingzangpicturebackend.domain.space.valueobject.SpaceLevelEnum;
import com.zangyalong.mingzangpicturebackend.domain.space.valueobject.SpaceRoleEnum;
import com.zangyalong.mingzangpicturebackend.domain.space.valueobject.SpaceTypeEnum;
import com.zangyalong.mingzangpicturebackend.domain.user.entity.User;
import com.zangyalong.mingzangpicturebackend.infrastructure.api.CosManager;
import com.zangyalong.mingzangpicturebackend.infrastructure.exception.BusinessException;
import com.zangyalong.mingzangpicturebackend.infrastructure.exception.ErrorCode;
import com.zangyalong.mingzangpicturebackend.infrastructure.exception.ThrowUtils;
import com.zangyalong.mingzangpicturebackend.infrastructure.mapper.SpaceMapper;
import com.zangyalong.mingzangpicturebackend.interfaces.dto.space.SpaceAddRequest;
import com.zangyalong.mingzangpicturebackend.interfaces.dto.space.SpaceQueryRequest;
import com.zangyalong.mingzangpicturebackend.interfaces.vo.space.SpaceVO;
import com.zangyalong.mingzangpicturebackend.interfaces.vo.user.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
* @author mingzang
* @description 针对表【space(空间)】的数据库操作Service实现
* @createDate 2025-08-12 17:20:37
*/
@Service
public class SpaceDomainServiceImpl implements SpaceDomainService {

    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private UserApplicationService userService;

    @Lazy
    @Resource
    private PictureApplicationService pictureService;

    @Resource
    private CosManager cosManager;

    @Resource
    @Lazy
    private SpaceUserApplicationService spaceUserService;

//    @Lazy
//    @Resource
//    private DynamicShardingManager dynamicShardingManager;

    @Override
    public void fillSpaceBySpaceLevel(Space space) {
        // 根据空间级别，自动填充限额
        SpaceLevelEnum spaceLevelEnum = SpaceLevelEnum.getEnumByValue(space.getSpaceLevel());
        if (spaceLevelEnum != null) {
            long maxSize = spaceLevelEnum.getMaxSize();
            if (space.getMaxSize() == null) {
                space.setMaxSize(maxSize);
            }
            long maxCount = spaceLevelEnum.getMaxCount();
            if (space.getMaxCount() == null) {
                space.setMaxCount(maxCount);
            }
        }
    }

    @Override
    public QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest) {
        QueryWrapper<Space> queryWrapper = new QueryWrapper<>();

        if(spaceQueryRequest == null){
            return queryWrapper;
        }

        Long id = spaceQueryRequest.getId();
        Long userId = spaceQueryRequest.getUserId();
        String spaceName = spaceQueryRequest.getSpaceName();
        Integer spaceLevel = spaceQueryRequest.getSpaceLevel();
        Integer spaceType = spaceQueryRequest.getSpaceType();
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceType), "spaceType", spaceType);
        queryWrapper.eq(ObjUtil.isNotEmpty(id),"Id", id);
        queryWrapper.eq(ObjUtil.isNotEmpty(userId),"userId", userId);
        queryWrapper.like(StrUtil.isNotBlank(spaceName), "spaceName", spaceName);
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceLevel),"spaceLevel", spaceLevel);
        return queryWrapper;
    }

    @Override
    public void checkSpaceAuth(User loginUser, Space oldSpace) {
        Long spaceId = oldSpace.getId();
        ThrowUtils.throwIf(spaceId == null, ErrorCode.PARAMS_ERROR);
        Long userId = loginUser.getId();
        ThrowUtils.throwIf(userId == null, ErrorCode.NOT_LOGIN_ERROR);

        if(!Objects.equals(oldSpace.getUserId(), userId) && !loginUser.isAdmin()){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限");
        }
    }
}




