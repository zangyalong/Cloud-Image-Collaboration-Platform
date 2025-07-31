package com.zangyalong.mingzangpicturebackend.service;

import com.zangyalong.mingzangpicturebackend.model.dto.picture.PictureUploadRequest;
import com.zangyalong.mingzangpicturebackend.model.entity.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zangyalong.mingzangpicturebackend.model.entity.User;
import com.zangyalong.mingzangpicturebackend.model.vo.PictureVO;
import org.springframework.web.multipart.MultipartFile;

/**
* @author mingzang
* @description 针对表【picture(图片)】的数据库操作Service
* @createDate 2025-07-31 17:14:45
*/
public interface PictureService extends IService<Picture> {

    /**
     * 上传图片
     *
     * @param multipartFile
     * @param pictureUploadRequest
     * @param loginUser
     * @return
     */
    PictureVO uploadPicture(MultipartFile multipartFile,
                            PictureUploadRequest pictureUploadRequest,
                            User loginUser);

}
