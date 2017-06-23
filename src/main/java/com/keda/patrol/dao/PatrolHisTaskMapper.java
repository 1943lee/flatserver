package com.keda.patrol.dao;

import com.keda.patrol.model.PatrolHisTask;

public interface PatrolHisTaskMapper {
    int deleteByPrimaryKey(String rwbh);

    int insert(PatrolHisTask record);

    int insertSelective(PatrolHisTask record);

    PatrolHisTask selectByPrimaryKey(String rwbh);

    int updateByPrimaryKeySelective(PatrolHisTask record);

    int updateByPrimaryKey(PatrolHisTask record);
}