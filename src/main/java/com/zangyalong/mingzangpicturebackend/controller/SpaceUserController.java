package com.zangyalong.mingzangpicturebackend.controller;

import com.zangyalong.mingzangpicturebackend.common.BaseResponse;
import com.zangyalong.mingzangpicturebackend.common.ResultUtils;
import com.zangyalong.mingzangpicturebackend.exception.BusinessException;
import com.zangyalong.mingzangpicturebackend.exception.ErrorCode;
import com.zangyalong.mingzangpicturebackend.model.entity.User;
import com.zangyalong.mingzangpicturebackend.model.vo.SpaceVO;
import com.zangyalong.mingzangpicturebackend.service.SpaceUserService;
import com.zangyalong.mingzangpicturebackend.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/spaceUser")
public class SpaceUserController {
    @Resource
    private UserService userService;
    @Resource
    private SpaceUserService spaceUserService;
    /**
     * 列出我加入的空间
     * @param request
     * @return
     */
    @PostMapping("/list/my")
    public BaseResponse<List<SpaceVO>> listMyTeamSpace(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        List<SpaceVO> spaceVOList = spaceUserService.listMyTeamSpace(loginUser.getId());
        return ResultUtils.success(spaceVOList);
    }
}
