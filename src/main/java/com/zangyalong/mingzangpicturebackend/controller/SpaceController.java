package com.zangyalong.mingzangpicturebackend.controller;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zangyalong.mingzangpicturebackend.annotation.AuthCheck;
import com.zangyalong.mingzangpicturebackend.common.BaseResponse;
import com.zangyalong.mingzangpicturebackend.common.DeleteRequest;
import com.zangyalong.mingzangpicturebackend.common.ResultUtils;
import com.zangyalong.mingzangpicturebackend.constant.UserConstant;
import com.zangyalong.mingzangpicturebackend.exception.BusinessException;
import com.zangyalong.mingzangpicturebackend.exception.ErrorCode;
import com.zangyalong.mingzangpicturebackend.exception.ThrowUtils;
import com.zangyalong.mingzangpicturebackend.model.dto.space.SpaceAddRequest;
import com.zangyalong.mingzangpicturebackend.model.dto.space.SpaceEditRequest;
import com.zangyalong.mingzangpicturebackend.model.dto.space.SpaceQueryRequest;
import com.zangyalong.mingzangpicturebackend.model.dto.space.SpaceUpdateRequest;
import com.zangyalong.mingzangpicturebackend.model.entity.Picture;
import com.zangyalong.mingzangpicturebackend.model.entity.Space;
import com.zangyalong.mingzangpicturebackend.model.entity.SpaceLevel;
import com.zangyalong.mingzangpicturebackend.model.entity.User;
import com.zangyalong.mingzangpicturebackend.model.enums.PictureReviewStatusEnum;
import com.zangyalong.mingzangpicturebackend.model.enums.SpaceLevelEnum;
import com.zangyalong.mingzangpicturebackend.model.vo.PictureVO;
import com.zangyalong.mingzangpicturebackend.model.vo.SpaceVO;
import com.zangyalong.mingzangpicturebackend.model.vo.UserVO;
import com.zangyalong.mingzangpicturebackend.service.PictureService;
import com.zangyalong.mingzangpicturebackend.service.SpaceService;
import com.zangyalong.mingzangpicturebackend.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/space")
public class SpaceController {

    @Resource
    private SpaceService spaceService;

    @Resource
    private UserService userService;

    @Resource
    private PictureService pictureService;

    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateSpace(@RequestBody SpaceUpdateRequest spaceUpdateRequest) {
        if (spaceUpdateRequest == null || spaceUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 将实体类和 DTO 进行转换
        Space space = new Space();
        BeanUtils.copyProperties(spaceUpdateRequest, space);
        // 自动填充数据
        spaceService.fillSpaceBySpaceLevel(space);
        // 数据校验
        spaceService.validSpace(space, false);
        // 判断是否存在
        long id = spaceUpdateRequest.getId();
        Space oldSpace = spaceService.getById(id);
        ThrowUtils.throwIf(oldSpace == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = spaceService.updateById(space);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    @PostMapping("/add")
    public BaseResponse<Long> addSpace(@RequestBody SpaceAddRequest spaceAddRequest, HttpServletRequest request) {
        if (spaceAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);

        Long spaceId = spaceService.addSpace(spaceAddRequest, loginUser);

        return ResultUtils.success(spaceId);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteSpace(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if(deleteRequest == null || deleteRequest.getId() <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        User loginUser = userService.getLoginUser(request);
        Long id = deleteRequest.getId();
        Space space = spaceService.getById(id);
        ThrowUtils.throwIf(space == null, ErrorCode.PARAMS_ERROR);

        if(!space.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        boolean result = spaceService.deleteSpace(id, loginUser);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "删除空间失败");

        return ResultUtils.success(true);
    }

    /**
     * 编辑空间（给用户使用）
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editSpace(@RequestBody SpaceEditRequest spaceEditRequest, HttpServletRequest request) {
        if (spaceEditRequest == null || spaceEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 在此处将实体类和 DTO 进行转换
        Space space = new Space();
        BeanUtils.copyProperties(spaceEditRequest, space);
        // 自动填充数据
        spaceService.fillSpaceBySpaceLevel(space);
        // 设置编辑时间
        space.setEditTime(new Date());
        // 数据校验
        spaceService.validSpace(space, false);
        User loginUser = userService.getLoginUser(request);
        // 判断是否存在
        long id = spaceEditRequest.getId();
        Space oldSpace = spaceService.getById(id);
        ThrowUtils.throwIf(oldSpace == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        spaceService.checkSpaceAuth(loginUser, oldSpace);
        // 操作数据库
        boolean result = spaceService.updateById(space);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    @PostMapping("/list/page/vo")
    public BaseResponse<Page<SpaceVO>> listSpaceVOByPage(@RequestBody SpaceQueryRequest spaceQueryRequest, HttpServletRequest request) {
        long current = spaceQueryRequest.getCurrent();
        long size = spaceQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库

        Page<Space> spacePage = spaceService.page(new Page<>(current, size),
                spaceService.getQueryWrapper(spaceQueryRequest));

        Page<SpaceVO> spaceVOPage = spaceService.getSpaceVOPage(spacePage);

        return ResultUtils.success(spaceVOPage);

    }

    /**
     * 分页获取空间列表（仅管理员可用）
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Space>> listSpaceByPage(@RequestBody SpaceQueryRequest spaceQueryRequest, HttpServletRequest request) {
        long current = spaceQueryRequest.getCurrent();
        long size = spaceQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库

        Page<Space> spacePage = spaceService.page(new Page<>(current, size),
                spaceService.getQueryWrapper(spaceQueryRequest));

        //Page<SpaceVO> spaceVOPage = spaceService.getSpaceVOPage(spacePage);

        return ResultUtils.success(spacePage);

    }

    @GetMapping("/list/level")
    public BaseResponse<List<SpaceLevel>> listSpaceLevel() {
        List<SpaceLevel> spaceLevelList = Arrays.stream(SpaceLevelEnum.values()) // 获取所有枚举
                .map(spaceLevelEnum -> new SpaceLevel(
                        spaceLevelEnum.getValue(),
                        spaceLevelEnum.getText(),
                        spaceLevelEnum.getMaxCount(),
                        spaceLevelEnum.getMaxSize()))
                .collect(Collectors.toList());
        return ResultUtils.success(spaceLevelList);
    }

    @GetMapping("/get/vo")
    public BaseResponse<SpaceVO> getSpaceVOById(@RequestParam Long id) {
        Space space = spaceService.getById(id);
        SpaceVO spaceVO = new SpaceVO();
        BeanUtils.copyProperties(space, spaceVO);

        User user = userService.getById(space.getUserId());
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        spaceVO.setUser(userVO);

        return ResultUtils.success(spaceVO);
    }

    /**
     * 根据 id 获取空间（仅管理员可用）
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Space> getSpaceById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Space space = spaceService.getById(id);
        ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(space);
    }

    @PostMapping("/list/my")
    public BaseResponse<List<SpaceVO>> listMySpaces(@RequestBody(required = false) SpaceQueryRequest spaceQueryRequest,
                                                    HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 1. 直接构建查询条件，只根据 userId 查询
        QueryWrapper<Space> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", loginUser.getId());
        List<Space> spaceList = spaceService.list(queryWrapper);
        if (CollUtil.isEmpty(spaceList)) {
            return ResultUtils.success(new ArrayList<>());
        }
        // 2. 获取用户信息
        UserVO userVO = userService.getUserVO(loginUser);
        // 3. 组装 SpaceVO 列表 (这部分在问题2中会进一步修改)
        List<SpaceVO> spaceVOList = spaceList.stream().map(space -> {
            SpaceVO spaceVO = SpaceVO.objToVo(space);
            spaceVO.setUser(userVO);

            // 创建查询条件，查询当前空间下的图片
            QueryWrapper<Picture> pictureQueryWrapper = new QueryWrapper<>();
            pictureQueryWrapper.eq("spaceId", space.getId());
            // 1. 统计图片数量
            long pictureCount = pictureService.count(pictureQueryWrapper);
            spaceVO.setTotalCount(pictureCount);
            // 2. 统计已用容量 (假设 Picture 实体中有 pictureSize 字段)
            // 清空 select 条件，只查询 sum(pictureSize)
            pictureQueryWrapper.select("sum(pictureSize) as totalSize");
            Map<String, Object> sizeMap = pictureService.getMap(pictureQueryWrapper);
            long usedSize = 0L;
            if (sizeMap != null && sizeMap.get("totalSize") != null) {
                // getMap 返回的是 BigDecimal 类型，需要转换为 long
                usedSize = ((Number) sizeMap.get("totalSize")).longValue();
            }
            spaceVO.setTotalSize(usedSize);

            return spaceVO;
        }).collect(Collectors.toList());
        return ResultUtils.success(spaceVOList);
    }
}
