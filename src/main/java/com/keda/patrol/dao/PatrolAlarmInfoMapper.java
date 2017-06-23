package com.keda.patrol.dao;

import com.keda.patrol.model.PatrolAlarmInfo;

public interface PatrolAlarmInfoMapper {
    int insert(PatrolAlarmInfo record);

    int insertSelective(PatrolAlarmInfo record);
}