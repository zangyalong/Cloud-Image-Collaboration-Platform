package com.zangyalong.mingzangpicturebackend.manager.upload;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import com.zangyalong.mingzangpicturebackend.config.CosClientConfig;
import com.zangyalong.mingzangpicturebackend.exception.BusinessException;
import com.zangyalong.mingzangpicturebackend.exception.ErrorCode;
import com.zangyalong.mingzangpicturebackend.manager.CosManager;
import com.zangyalong.mingzangpicturebackend.model.dto.file.UploadPictureResult;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import javax.imageio.ImageIO;

@Slf4j
public abstract class PictureUploadTemplate {

    @Resource
    protected CosManager cosManager;

    @Resource
    protected CosClientConfig cosClientConfig;

    /**
     * 模板方法，定义上传流程
     */
    public final UploadPictureResult uploadPicture(Object inputSource, String uploadPathPrefix) {
        //1. 校验图片
        validPicture(inputSource);

        //2. 图片上传地址
        String uuid = RandomUtil.randomString(16);
        String originFileName = getOriginFileName(inputSource);
        String uploadFileName = String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid,
                FileUtil.getSuffix(originFileName));
        String uploadPath = String.format("/%s/%s", uploadPathPrefix, uploadFileName);

        File file = null;
        try {
            // 3.创建临时文件
            file = File.createTempFile(uploadPath, null);
            // 处理文件来源，本地或者URL
            processFile(inputSource, file);

            // 4.上传图片到对象存储 - 使用普通上传而不是图片处理上传
            PutObjectResult putObjectResult = cosManager.putObject(uploadPath, file);
            
            // 5.封装返回结果 - 使用本地图片信息
            return buildResultWithLocalInfo(originFileName, file, uploadPath);
        } catch (Exception  e) {
            log.error("图片上传到对象存储失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            // 6. 清理临时文件
            deleteTempFile(file);
        }
    }

    /**
     * 校验输入源（本地文件或 URL）
     */
    protected abstract void validPicture(Object inputSource);

    /**
     * 获取输入源的原始文件名
     */
    protected abstract String getOriginFileName(Object inputSource);

    /**
     * 处理输入源并生成本地临时文件
     */
    protected abstract void processFile(Object inputSource, File file) throws IOException;

    /**
     * 使用本地图片信息封装返回结果
     */
    protected UploadPictureResult buildResultWithLocalInfo(String originFileName, File file, String uploadPath) {
        try {
            // 使用Java自带的图片处理类获取图片信息
            java.awt.image.BufferedImage bufferedImage = javax.imageio.ImageIO.read(file);
            if (bufferedImage == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "无法读取图片文件");
            }
            
            UploadPictureResult uploadPictureResult = new UploadPictureResult();
            int picWidth = bufferedImage.getWidth();
            int picHeight = bufferedImage.getHeight();
            double picScale = NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue();
            
            uploadPictureResult.setPicName(FileUtil.mainName(originFileName));
            uploadPictureResult.setPicWidth(picWidth);
            uploadPictureResult.setPicHeight(picHeight);
            uploadPictureResult.setPicScale(picScale);
            
            // 从文件名获取格式
            String fileName = file.getName();
            String format = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            uploadPictureResult.setPicFormat(format);
            
            uploadPictureResult.setPicSize(FileUtil.size(file));
            uploadPictureResult.setUrl(cosClientConfig.getHost() + "/" + uploadPath);
            
            return uploadPictureResult;
        } catch (Exception e) {
            log.error("获取图片信息失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取图片信息失败");
        }
    }

    /**
     * 封装返回结果
     */
    public UploadPictureResult buildResult(String originFileName, File file, String uploadPath, ImageInfo imageInfo){
        UploadPictureResult uploadPictureResult = new UploadPictureResult();
        int picWidth = imageInfo.getWidth();
        int picHeight = imageInfo.getHeight();
        double picScale = NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue();
        uploadPictureResult.setPicName(FileUtil.mainName(originFileName));
        uploadPictureResult.setPicWidth(picWidth);
        uploadPictureResult.setPicHeight(picHeight);
        uploadPictureResult.setPicScale(picScale);
        uploadPictureResult.setPicFormat(imageInfo.getFormat());
        uploadPictureResult.setPicSize(FileUtil.size(file));
        uploadPictureResult.setUrl(cosClientConfig.getHost() + "/" + uploadPath);
        return uploadPictureResult;
    }

    /**
     * 删除临时文件
     */
    public void deleteTempFile(File file){
        if (file == null) {
            return;
        }
        boolean deleteResult = file.delete();
        if (!deleteResult) {
            log.error("file delete error, filepath = {}", file.getAbsolutePath());
        }
    }
}
