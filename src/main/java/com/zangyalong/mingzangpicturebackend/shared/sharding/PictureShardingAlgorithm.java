package com.zangyalong.mingzangpicturebackend.shared.sharding;

import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

public class PictureShardingAlgorithm implements StandardShardingAlgorithm<Long> {

    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Long> preciseShardingValue) {
        // 处理可能的类型不匹配问题
        Number spaceIdNumber = preciseShardingValue.getValue();
        Long spaceId;
        if (spaceIdNumber instanceof Integer) {
            spaceId = ((Integer) spaceIdNumber).longValue();
        } else {
            spaceId = spaceIdNumber.longValue();
        }
        
        String logicTableName = preciseShardingValue.getLogicTableName();
        // spaceId 为 null 或 0 表示查询所有图片（公共图库）
        if(spaceId == null || spaceId == 0){
            return logicTableName;
        }

        // 根据 spaceId 动态生成分表名
        String realTableName = "picture_" + spaceId;
        if (availableTargetNames.contains(realTableName)) {
            return realTableName;
        } else {
            // 如果找不到对应的分表，返回逻辑表名（这通常发生在查询不存在的spaceId时）
            return logicTableName;
        }
    }

    @Override
    public Collection<String> doSharding(Collection<String> collection, RangeShardingValue<Long> rangeShardingValue) {
        return new ArrayList<>();
    }

    @Override
    public Properties getProps() {
        return null;
    }

    @Override
    public void init(Properties properties) {

    }
}
