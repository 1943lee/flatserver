package com.keda.patrol.dao;

import com.keda.patrol.model.PatrolAlarmConfig;

public interface PatrolAlarmConfigMapper {
    int insert(PatrolAlarmConfig record);

    int insertSelective(PatrolAlarmConfig record);
}