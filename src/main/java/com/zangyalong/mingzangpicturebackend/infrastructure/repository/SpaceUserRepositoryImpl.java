package com.zangyalong.mingzangpicturebackend.infrastructure.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zangyalong.mingzangpicturebackend.domain.space.entity.SpaceUser;
import com.zangyalong.mingzangpicturebackend.domain.space.repository.SpaceUserRepository;
import com.zangyalong.mingzangpicturebackend.infrastructure.mapper.SpaceUserMapper;
import org.springframework.stereotype.Service;

@Service
public class SpaceUserRepositoryImpl extends ServiceImpl<SpaceUserMapper, SpaceUser> implements SpaceUserRepository {
}
