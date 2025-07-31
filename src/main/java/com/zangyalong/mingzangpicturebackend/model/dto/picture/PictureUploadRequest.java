package com.zangyalong.mingzangpicturebackend.model.dto.picture;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class PictureUploadRequest implements Serializable {

    /**
     * 图片 id（用于修改）
     */
    private Long id;

    @Serial
    private static final long serialVersionUID = 1L;
}
