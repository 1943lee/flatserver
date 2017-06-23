package com.keda.patrol.dao;

import com.keda.patrol.model.PatrolRtTask;

import java.util.List;

public interface PatrolRtTaskMapper {
    int deleteByPrimaryKey(String rwbh);

    int insert(PatrolRtTask record);

    int insertSelective(PatrolRtTask record);

    PatrolRtTask selectByPrimaryKey(String rwbh);

    List<PatrolRtTask> selectAll();

    int updateByPrimaryKeySelective(PatrolRtTask record);

    int updateByPrimaryKey(PatrolRtTask record);
}