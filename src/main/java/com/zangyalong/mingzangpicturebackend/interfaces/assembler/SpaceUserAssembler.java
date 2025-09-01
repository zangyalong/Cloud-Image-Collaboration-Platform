package com.zangyalong.mingzangpicturebackend.interfaces.assembler;

import com.zangyalong.mingzangpicturebackend.domain.space.entity.SpaceUser;
import com.zangyalong.mingzangpicturebackend.interfaces.dto.spaceuser.SpaceUserAddRequest;
import com.zangyalong.mingzangpicturebackend.interfaces.dto.spaceuser.SpaceUserEditRequest;
import org.springframework.beans.BeanUtils;

public class SpaceUserAssembler {

    public static SpaceUser toSpaceUserEntity(SpaceUserAddRequest request) {
        SpaceUser spaceUser = new SpaceUser();
        BeanUtils.copyProperties(request, spaceUser);
        return spaceUser;
    }

    public static SpaceUser toSpaceUserEntity(SpaceUserEditRequest request) {
        SpaceUser spaceUser = new SpaceUser();
        BeanUtils.copyProperties(request, spaceUser);
        return spaceUser;
    }
}

