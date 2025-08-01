package com.zangyalong.mingzangpicturebackend.model.entity;

import lombok.Data;

import java.util.List;

@Data
public class PictureTagCategory {

    List<String> tagList;

    List<String> categoryList;
}
