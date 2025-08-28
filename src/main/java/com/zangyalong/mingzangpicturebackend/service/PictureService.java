package com.zangyalong.mingzangpicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zangyalong.mingzangpicturebackend.api.aliyunai.CreateOutPaintingTaskResponse;
import com.zangyalong.mingzangpicturebackend.common.BaseResponse;
import com.zangyalong.mingzangpicturebackend.model.dto.picture.*;
import com.zangyalong.mingzangpicturebackend.model.entity.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zangyalong.mingzangpicturebackend.model.entity.User;
import com.zangyalong.mingzangpicturebackend.model.vo.PictureVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.util.List;

/**
* @author mingzang
* @description 针对表【picture(图片)】的数据库操作Service
* @createDate 2025-07-31 17:14:45
*/
public interface PictureService extends IService<Picture> {

    /**
     * 上传图片
     *
     * @param inputSource
     * @param pictureUploadRequest
     * @param loginUser
     * @return
     */
    PictureVO uploadPicture(/*MultipartFile multipartFile*/Object inputSource,
                            PictureUploadRequest pictureUploadRequest,
                            User loginUser);

    QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);

    PictureVO getPictureVO(Picture picture, HttpServletRequest request);

    void fillReviewParams(Picture picture, User loginUser);

    /**
     * 分页获取图片封装
     */
    Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request);

    /**
     * 图片数据校验
     *
     * @param picture
     */
    void validPicture(Picture picture);

    /**
     * 图片审核
     *
     * @param pictureReviewRequest
     * @param loginUser
     */
    void doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser);

    /**
     * 批量抓取和创建图片
     *
     * @param pictureUploadByBatchRequest
     * @param loginUser
     * @return 成功创建的图片数
     */
    Integer uploadPictureByBatch(
            PictureUploadByBatchRequest pictureUploadByBatchRequest,
            User loginUser
    );

    /**
     * 图片清理
     * @param oldPicture
     */
    public void clearPictureFile(Picture oldPicture) throws MalformedURLException;

    /**
     * 检验图片权限（编辑和删除权限）
     * @param loginUser
     * @param picture
     */
    public void checkPictureAuth(User loginUser, Picture picture);

    /**
     * 删除图片
     * @param pictureId
     * @param loginUser
     */
    public void deletePicture(long pictureId, User loginUser) throws MalformedURLException;

    /**
     * 编辑图片
     * @param pictureEditRequest
     * @param loginUser
     */

    public void editPicture(PictureEditRequest pictureEditRequest, User loginUser);
    /**
     * Todo Spr؜ing Scheduler 定时任务实现定时清理图片
     */

    public List<PictureVO> searchPictureByColor(Long spaceId, String picColor, User loginUser);

    public void editPictureByBatch(PictureEditByBatchRequest pictureEditByBatchRequest, User loginUser);

    public CreateOutPaintingTaskResponse createPictureOutPaintingTask
            (CreatePictureOutPaintingTaskRequest createPictureOutPaintingTaskRequest, User loginUser);
}
