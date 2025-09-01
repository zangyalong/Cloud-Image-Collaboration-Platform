package com.zangyalong.mingzangpicturebackend.application.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zangyalong.mingzangpicturebackend.infrastructure.api.aliyunai.CreateOutPaintingTaskResponse;
import com.zangyalong.mingzangpicturebackend.interfaces.dto.picture.*;
import com.zangyalong.mingzangpicturebackend.domain.picture.entity.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zangyalong.mingzangpicturebackend.domain.user.entity.User;
import com.zangyalong.mingzangpicturebackend.interfaces.vo.picture.PictureVO;
import javax.servlet.http.HttpServletRequest;

import java.net.MalformedURLException;
import java.util.List;

/**
* @author mingzang
* @description 针对表【picture(图片)】的数据库操作Service
* @createDate 2025-07-31 17:14:45
*/
public interface PictureApplicationService extends IService<Picture> {


    PictureVO uploadPicture(Object inputSource, PictureUploadRequest pictureUploadRequest,
                            User loginUser);

    QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);

    PictureVO getPictureVO(Picture picture, HttpServletRequest request);

    Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request);

    void doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser);

    void fillReviewParams(Picture picture, User loginUser);

    int uploadPictureByBatch(PictureUploadByBatchRequest pictureUploadByBatchRequest, User loginUser);

    void clearPictureFile(Picture oldPicture);

    void deletePicture(long pictureId, User loginUser) throws MalformedURLException;

    void checkPictureAuth(User loginUser, Picture picture);

    void editPicture(Picture picture, User loginUser);

    List<PictureVO> searchPictureByColor(Long spaceId, String picColor, User loginUser);

    void editPictureByBatch(PictureEditByBatchRequest pictureEditByBatchRequest, User loginUser);

    CreateOutPaintingTaskResponse createPictureOutPaintingTask(
            CreatePictureOutPaintingTaskRequest createPictureOutPaintingTaskRequest, User loginUser);
}
