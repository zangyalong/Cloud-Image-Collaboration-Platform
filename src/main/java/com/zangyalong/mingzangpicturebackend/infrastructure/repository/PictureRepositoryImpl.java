package com.zangyalong.mingzangpicturebackend.infrastructure.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zangyalong.mingzangpicturebackend.domain.picture.entity.Picture;
import com.zangyalong.mingzangpicturebackend.domain.picture.repository.PictureRepository;
import com.zangyalong.mingzangpicturebackend.infrastructure.mapper.PictureMapper;
import org.springframework.stereotype.Service;

@Service
public class PictureRepositoryImpl extends ServiceImpl<PictureMapper, Picture> implements PictureRepository {

}
