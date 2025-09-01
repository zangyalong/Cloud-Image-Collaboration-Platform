package com.zangyalong.mingzangpicturebackend.application.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zangyalong.mingzangpicturebackend.interfaces.dto.space.analyze.*;
import com.zangyalong.mingzangpicturebackend.interfaces.vo.user.space.analyze.*;
import com.zangyalong.mingzangpicturebackend.domain.space.entity.Space;
import com.zangyalong.mingzangpicturebackend.domain.user.entity.User;

import java.util.List;


public interface SpaceAnalyzeApplicationService extends IService<Space> {

    SpaceUsageAnalyzeResponse getSpaceUsageAnalyze(SpaceUsageAnalyzeRequest spaceUsageAnalyzeRequest,
                                                   User loginUser);

    List<SpaceCategoryAnalyzeResponse> getSpaceCategoryAnalyze(
            SpaceCategoryAnalyzeRequest spaceCategoryAnalyzeRequest, User loginUser);

    List<SpaceTagAnalyzeResponse> getSpaceTagAnalyze(SpaceTagAnalyzeRequest spaceTagAnalyzeRequest,
                                                     User loginUser);
    List<SpaceSizeAnalyzeResponse> getSpaceSizeAnalyze(SpaceSizeAnalyzeRequest spaceSizeAnalyzeRequest,
                                                       User loginUser);
    List<SpaceUserAnalyzeResponse> getSpaceUserAnalyze(SpaceUserAnalyzeRequest spaceUserAnalyzeRequest,
                                                       User loginUser);

    List<Space> getSpaceRankAnalyze(SpaceRankAnalyzeRequest spaceRankAnalyzeRequest, User loginUser);
}
