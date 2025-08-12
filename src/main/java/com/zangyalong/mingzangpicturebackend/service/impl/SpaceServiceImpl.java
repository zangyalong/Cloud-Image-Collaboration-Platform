package com.zangyalong.mingzangpicturebackend.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zangyalong.mingzangpicturebackend.exception.BusinessException;
import com.zangyalong.mingzangpicturebackend.exception.ErrorCode;
import com.zangyalong.mingzangpicturebackend.exception.ThrowUtils;
import com.zangyalong.mingzangpicturebackend.model.dto.space.SpaceAddRequest;
import com.zangyalong.mingzangpicturebackend.model.entity.Space;
import com.zangyalong.mingzangpicturebackend.model.entity.User;
import com.zangyalong.mingzangpicturebackend.model.enums.SpaceLevelEnum;
import com.zangyalong.mingzangpicturebackend.service.SpaceService;
import com.zangyalong.mingzangpicturebackend.mapper.SpaceMapper;
import com.zangyalong.mingzangpicturebackend.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;

/**
* @author mingzang
* @description 针对表【space(空间)】的数据库操作Service实现
* @createDate 2025-08-12 17:20:37
*/
@Service
public class SpaceServiceImpl extends ServiceImpl<SpaceMapper, Space>
    implements SpaceService{

    @Resource
    private TransactionTemplate transactionTemplate;
    @Autowired
    private UserService userService;

    @Override
    public void validSpace(Space space, boolean add) {
        ThrowUtils.throwIf(space == null, ErrorCode.PARAMS_ERROR);
        String spaceName = space.getSpaceName();
        Integer spaceLevel = space.getSpaceLevel();
        SpaceLevelEnum spaceLevelEnum = SpaceLevelEnum.getEnumByValue(spaceLevel);

        if(add){
            if(StrUtil.isBlank(spaceName)){
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间名称不能为空");
            }
            if(spaceLevelEnum == null){
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间级别不能为空");
            }
        }

        if(spaceLevel != null && spaceLevelEnum == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间级别不存在");
        }
        if(StrUtil.isNotBlank(spaceName) && spaceName.length() > 30){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间名称过长");
        }
    }

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
    public long addSpace(SpaceAddRequest spaceAddRequest, User loginUser) {
        Space space = new Space();
        BeanUtils.copyProperties(spaceAddRequest, space);

        if(StrUtil.isBlank(spaceAddRequest.getSpaceName())){
            space.setSpaceName("默认空间");
        }
        if(spaceAddRequest.getSpaceLevel() == null){
            space.setSpaceLevel(SpaceLevelEnum.COMMON.getValue());
        }
        this.fillSpaceBySpaceLevel(space);
        this.validSpace(space, true);

        Long userId = loginUser.getId();
        space.setUserId(userId);
        if(SpaceLevelEnum.COMMON.getValue() != spaceAddRequest.getSpaceLevel() &&
        !userService.isAdmin(loginUser)){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限创建指定级别的空间");
        }

        String lock = String.valueOf(userId).intern();
        synchronized (lock) {
            Long newSpaceId = transactionTemplate.execute(status -> {
                boolean exists = this.lambdaQuery().eq(Space::getUserId,userId).exists();
                ThrowUtils.throwIf(exists, ErrorCode.OPERATION_ERROR, "每个用户只能有一个私有空间");

                boolean result = this.save(space);
                ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
                return space.getId();
            });
            return Optional.ofNullable(newSpaceId).orElse(-1L);
        }
    }
}




