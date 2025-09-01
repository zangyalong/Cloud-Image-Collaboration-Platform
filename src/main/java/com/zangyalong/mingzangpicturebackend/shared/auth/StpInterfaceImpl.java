package com.zangyalong.mingzangpicturebackend.shared.auth;

import cn.dev33.satoken.stp.StpInterface;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.json.JSONUtil;
import com.zangyalong.mingzangpicturebackend.infrastructure.exception.BusinessException;
import com.zangyalong.mingzangpicturebackend.infrastructure.exception.ErrorCode;
import com.zangyalong.mingzangpicturebackend.shared.auth.model.SpaceUserPermissionConstant;
import com.zangyalong.mingzangpicturebackend.domain.picture.entity.Picture;
import com.zangyalong.mingzangpicturebackend.domain.space.entity.Space;
import com.zangyalong.mingzangpicturebackend.domain.space.entity.SpaceUser;
import com.zangyalong.mingzangpicturebackend.domain.user.entity.User;
import com.zangyalong.mingzangpicturebackend.domain.space.valueobject.SpaceRoleEnum;
import com.zangyalong.mingzangpicturebackend.domain.space.valueobject.SpaceTypeEnum;
import com.zangyalong.mingzangpicturebackend.application.service.PictureApplicationService;
import com.zangyalong.mingzangpicturebackend.application.service.SpaceApplicationService;
import com.zangyalong.mingzangpicturebackend.application.service.SpaceUserApplicationService;
import com.zangyalong.mingzangpicturebackend.application.service.UserApplicationService;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import java.util.*;

import static com.zangyalong.mingzangpicturebackend.domain.user.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 自定义权限加载接口实现类
 */
@Component    // 保证此类被 SpringBoot 扫描，完成 Sa-Token 的自定义权限验证扩展
public class StpInterfaceImpl implements StpInterface {

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Resource
    private SpaceUserAuthManager spaceUserAuthManager;

    @Resource
    private SpaceUserApplicationService spaceUserService;

    @Resource
    private UserApplicationService userService;
    @Resource
    private PictureApplicationService pictureService;
    @Resource
    private SpaceApplicationService spaceService;

    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        if(!StpKit.SPACE_TYPE.equals(loginType)){
            return new ArrayList<>();
        }

        List<String> ADMIN_PERMISSIONS = spaceUserAuthManager.
                getPermissionsByRole(SpaceRoleEnum.ADMIN.getValue());
        SpaceUserAuthContext authContext = getAuthContextByRequest();
        if(isAllFieldsNull(authContext)){
            return ADMIN_PERMISSIONS;
        }

        User loginUser = (User) StpKit.SPACE.getSessionByLoginId(loginId).get(USER_LOGIN_STATE);
        if(loginUser == null){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "用户未登录");
        }

        Long userId = loginUser.getId();
        SpaceUser spaceUser = authContext.getSpaceUser();
        if(spaceUser != null){
            return spaceUserAuthManager.getPermissionsByRole(spaceUser.getSpaceRole());
        }

        Long spaceUserId = authContext.getSpaceUserId();
        if(spaceUserId != null){
            spaceUser = spaceUserService.getById(spaceUserId);
            if(spaceUser == null){
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "未找到空间用户信息");
            }

            // 取出当前登录用户对应的 spaceUser
            SpaceUser loginSpaceUser = spaceUserService.lambdaQuery()
                    .eq(SpaceUser::getSpaceId, spaceUser.getSpaceId())
                    .eq(SpaceUser::getUserId, userId)
                    .one();
            if (loginSpaceUser == null) {
                return new ArrayList<>();
            }
            // 这里会导致管理员在私有空间没有权限，可以再查一次库处理
            return spaceUserAuthManager.getPermissionsByRole(loginSpaceUser.getSpaceRole());
        }
        // 如果没有 spaceUserId，尝试通过 spaceId 或 pictureId 获取 Space 对象并处理
        Long spaceId = authContext.getSpaceId();
        if (spaceId == null) {
            // 如果没有 spaceId，通过 pictureId 获取 Picture 对象和 Space 对象
            Long pictureId = authContext.getPictureId();
            // 图片 id 也没有，则默认通过权限校验
            if (pictureId == null) {
                return ADMIN_PERMISSIONS;
            }
            Picture picture = pictureService.lambdaQuery()
                    .eq(Picture::getId, pictureId)
                    .select(Picture::getId, Picture::getSpaceId, Picture::getUserId)
                    .one();
            if (picture == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "未找到图片信息");
            }
            spaceId = picture.getSpaceId();
            // 公共图库，仅本人或管理员可操作
            if (spaceId == null) {
                if (picture.getUserId().equals(userId) || loginUser.isAdmin()) {
                    return ADMIN_PERMISSIONS;
                } else {
                    // 不是自己的图片，仅可查看
                    return Collections.singletonList(SpaceUserPermissionConstant.PICTURE_VIEW);
                }
            }
        }
        // 获取 Space 对象
        Space space = null;
        if (spaceId != null && spaceId > 0) {
            space = spaceService.getById(spaceId);
            if (space == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "未找到空间信息");
            }
        }
        // 如果 spaceId 为 null 或 0，表示是公共图库，space 保持为 null
        // 根据 Space 类型判断权限
        if (space == null) {
            // 公共图库，仅本人或管理员可操作
            // 从 authContext 获取 pictureId
            Long pictureId = authContext.getPictureId();
            if (pictureId != null) {
                Picture picture = pictureService.lambdaQuery()
                        .eq(Picture::getId, pictureId)
                        .select(Picture::getId, Picture::getUserId)
                        .one();
                if (picture != null && (picture.getUserId().equals(userId) || loginUser.isAdmin())) {
                    return ADMIN_PERMISSIONS;
                } else {
                    // 不是自己的图片，仅可查看
                    return Collections.singletonList(SpaceUserPermissionConstant.PICTURE_VIEW);
                }
            } else {
                // 如果没有 pictureId，返回空权限列表
                return new ArrayList<>();
            }
        } else if (space.getSpaceType() == SpaceTypeEnum.PRIVATE.getValue()) {
            // 私有空间，仅本人或管理员有权限
            if (space.getUserId().equals(userId) || loginUser.isAdmin()) {
                return ADMIN_PERMISSIONS;
            } else {
                return new ArrayList<>();
            }
        } else {
            // 团队空间，查询 SpaceUser 并获取角色和权限
            spaceUser = spaceUserService.lambdaQuery()
                    .eq(SpaceUser::getSpaceId, spaceId)
                    .eq(SpaceUser::getUserId, userId)
                    .one();
            if (spaceUser == null) {
                return new ArrayList<>();
            }
            return spaceUserAuthManager.getPermissionsByRole(spaceUser.getSpaceRole());
        }
    }


    private boolean isAllFieldsNull(Object object) {
        if(object == null){
            return true;
        }
        return Arrays.stream(ReflectUtil.getFields(object.getClass()))
                .map(field -> ReflectUtil.getFieldValue(object,field))
                .allMatch(ObjectUtil::isEmpty);
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return new ArrayList<>();
    }



    /**
     * 从请求中获取上下文对象
     */
    private SpaceUserAuthContext getAuthContextByRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String contentType = request.getHeader(Header.CONTENT_TYPE.getValue());
        SpaceUserAuthContext authRequest;
        // 兼容 get 和 post 操作
        if (ContentType.JSON.getValue().equals(contentType)) {
            String body = ServletUtil.getBody(request);
            authRequest = JSONUtil.toBean(body, SpaceUserAuthContext.class);
        } else {
            Map<String, String> paramMap = ServletUtil.getParamMap(request);
            authRequest = BeanUtil.toBean(paramMap, SpaceUserAuthContext.class);
        }
        // 根据请求路径区分 id 字段的含义
        Long id = authRequest.getId();
        if (ObjUtil.isNotNull(id)) {
            String requestUri = request.getRequestURI();
            String partUri = requestUri.replace(contextPath + "/", "");
            String moduleName = StrUtil.subBefore(partUri, "/", false);
            switch (moduleName) {
                case "picture":
                    authRequest.setPictureId(id);
                    break;
                case "spaceUser":
                    authRequest.setSpaceUserId(id);
                    break;
                case "space":
                    authRequest.setSpaceId(id);
                    break;
                default:
            }
        }
        return authRequest;
    }

}

