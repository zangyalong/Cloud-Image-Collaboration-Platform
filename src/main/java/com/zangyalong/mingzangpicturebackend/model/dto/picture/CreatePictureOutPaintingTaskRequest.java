package com.zangyalong.mingzangpicturebackend.model.dto.picture;

import com.zangyalong.mingzangpicturebackend.api.aliyunai.CreateOutPaintingTaskRequest;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class CreatePictureOutPaintingTaskRequest implements Serializable {

    /**
     * 图片 id
     */
    private Long pictureId;

    /**
     * 扩图参数
     */
    private CreateOutPaintingTaskRequest.Parameters parameters;

    private static final long serialVersionUID = 1L;
}

