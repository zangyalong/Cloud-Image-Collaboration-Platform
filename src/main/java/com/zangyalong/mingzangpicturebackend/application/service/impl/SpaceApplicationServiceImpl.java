package com.zangyalong.mingzangpicturebackend.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zangyalong.mingzangpicturebackend.domain.space.service.SpaceDomainService;
import com.zangyalong.mingzangpicturebackend.infrastructure.exception.BusinessException;
import com.zangyalong.mingzangpicturebackend.infrastructure.exception.ErrorCode;
import com.zangyalong.mingzangpicturebackend.infrastructure.exception.ThrowUtils;
import com.zangyalong.mingzangpicturebackend.infrastructure.api.CosManager;
import com.zangyalong.mingzangpicturebackend.interfaces.dto.space.SpaceAddRequest;
import com.zangyalong.mingzangpicturebackend.interfaces.dto.space.SpaceQueryRequest;
import com.zangyalong.mingzangpicturebackend.domain.picture.entity.Picture;
import com.zangyalong.mingzangpicturebackend.domain.space.entity.Space;
import com.zangyalong.mingzangpicturebackend.domain.space.entity.SpaceUser;
import com.zangyalong.mingzangpicturebackend.domain.user.entity.User;
import com.zangyalong.mingzangpicturebackend.domain.space.valueobject.SpaceLevelEnum;
import com.zangyalong.mingzangpicturebackend.domain.space.valueobject.SpaceRoleEnum;
import com.zangyalong.mingzangpicturebackend.domain.space.valueobject.SpaceTypeEnum;
import com.zangyalong.mingzangpicturebackend.interfaces.vo.space.SpaceVO;
import com.zangyalong.mingzangpicturebackend.interfaces.vo.user.UserVO;
import com.zangyalong.mingzangpicturebackend.application.service.PictureApplicationService;
import com.zangyalong.mingzangpicturebackend.application.service.SpaceApplicationService;
import com.zangyalong.mingzangpicturebackend.infrastructure.mapper.SpaceMapper;
import com.zangyalong.mingzangpicturebackend.application.service.SpaceUserApplicationService;
import com.zangyalong.mingzangpicturebackend.application.service.UserApplicationService;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
* @author mingzang
* @description 针对表【space(空间)】的数据库操作Service实现
* @createDate 2025-08-12 17:20:37
*/
@Service
public class SpaceApplicationServiceImpl extends ServiceImpl<SpaceMapper, Space>
    implements SpaceApplicationService {

    @Resource
    private SpaceDomainService spaceDomainService;

    @Resource
    private TransactionTemplate transactionTemplate;
    @Autowired
    private UserApplicationService userApplicationService;

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
        spaceDomainService.fillSpaceBySpaceLevel(space);
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

        if (spaceAddRequest.getSpaceType() == null) {
            spaceAddRequest.setSpaceType(SpaceTypeEnum.PRIVATE.getValue());
        }

        this.fillSpaceBySpaceLevel(space);
        space.validSpace(true);

        Long userId = loginUser.getId();
        space.setUserId(userId);
        if(SpaceLevelEnum.COMMON.getValue() != spaceAddRequest.getSpaceLevel() && !loginUser.isAdmin()){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限创建指定级别的空间");
        }

        String lock = String.valueOf(userId).intern();
        synchronized (lock) {
            Long newSpaceId = transactionTemplate.execute(status -> {
                boolean exists = this.lambdaQuery()
                        .eq(Space::getUserId,userId)
                        .eq(Space::getSpaceType, spaceAddRequest.getSpaceType())
                        .exists();
                ThrowUtils.throwIf(exists, ErrorCode.OPERATION_ERROR, "每个用户每类空间仅能创建一个");

                boolean result = this.save(space);
                ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

                // 如果是团队空间，关联新增团队成员记录
                if (SpaceTypeEnum.TEAM.getValue() == spaceAddRequest.getSpaceType()) {
                    SpaceUser spaceUser = new SpaceUser();
                    spaceUser.setSpaceId(space.getId());
                    spaceUser.setUserId(userId);
                    spaceUser.setSpaceRole(SpaceRoleEnum.ADMIN.getValue());
                    result = spaceUserService.save(spaceUser);
                    ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "创建团队成员记录失败");
                }

                // 创建分表,内部自行判断是否创建，调用即可
                // dynamicShardingManager.createSpacePictureTable(space);

                return space.getId();
            });
            return Optional.ofNullable(newSpaceId).orElse(-1L);
        }
    }

    @Override
    public QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest) {
        return spaceDomainService.getQueryWrapper(spaceQueryRequest);
    }

    @Override
    public Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage, HttpServletRequest request) {
        List<Space> spaceList = spacePage.getRecords();
        Page<SpaceVO> spaceVOPage = new Page<>(spacePage.getCurrent(), spacePage.getSize(), spacePage.getTotal());
        if (CollUtil.isEmpty(spaceList)) {
            return spaceVOPage;
        }
        // 1. 关联查询用户信息，先收集所有不重复的 userId
        Set<Long> userIdSet = spaceList.stream()
            .map(Space::getUserId)
            .collect(Collectors.toSet());
        // 2. 使用 listByIds 一次性查询所有相关的用户
        Map<Long, User> userIdUserMap = userApplicationService.listByIds(userIdSet).stream()
            .collect(Collectors.toMap(User::getId, user -> user));

        // 3. 遍历 spaceList，从 Map 中获取用户信息并组装 VO
        List<SpaceVO> spaceVOList = spaceList.stream().map(space -> {
            SpaceVO spaceVO = SpaceVO.objToVo(space);
            User user = userIdUserMap.get(space.getUserId());
            spaceVO.setUser(userApplicationService.getUserVO(user));
            return spaceVO;
        }).collect(Collectors.toList());
        spaceVOPage.setRecords(spaceVOList);
        return spaceVOPage;
    }

    @Override
    public void checkSpaceAuth(User loginUser, Space space) {
        spaceDomainService.checkSpaceAuth(loginUser, space);
    }

    @Override
    public SpaceVO getSpaceVO(Space space, HttpServletRequest request) {
        // 对象转封装类
        SpaceVO spaceVO = SpaceVO.objToVo(space);
        // 关联查询用户信息
        Long userId = space.getUserId();
        if (userId != null && userId > 0) {
            User user = userApplicationService.getUserById(userId);
            UserVO userVO = userApplicationService.getUserVO(user);
            spaceVO.setUser(userVO);
        }
        return spaceVO;
    }
}




