package com.keda.patrol.dao;

import com.keda.patrol.model.PatrolConfig;

public interface PatrolConfigMapper {
    int deleteByPrimaryKey(String xlbh);

    int insert(PatrolConfig record);

    int insertSelective(PatrolConfig record);

    PatrolConfig selectByPrimaryKey(String xlbh);

    int updateByPrimaryKeySelective(PatrolConfig record);

    int updateByPrimaryKeyWithBLOBs(PatrolConfig record);

    int updateByPrimaryKey(PatrolConfig record);
}