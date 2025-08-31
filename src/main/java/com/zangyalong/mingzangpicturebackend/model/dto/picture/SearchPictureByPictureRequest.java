package com.zangyalong.mingzangpicturebackend.model.dto.picture;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class SearchPictureByPictureRequest implements Serializable {

    /**
     * 图片 id
     */
    private Long pictureId;

    private static final long serialVersionUID = 1L;
}

