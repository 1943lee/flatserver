package com.keda.wange.dao;

import com.keda.wange.model.WanGeMsg;
import com.keda.wange.model.WanGeMsgExample;
import java.math.BigDecimal;
import java.util.List;

public interface WanGeMsgMapper {
    int countByExample(WanGeMsgExample example);

    int deleteByExample(WanGeMsgExample example);

    int deleteByPrimaryKey(BigDecimal id);

    int insert(WanGeMsg record);

    int insertSelective(WanGeMsg record);

    List<WanGeMsg> selectByExample(WanGeMsgExample example);

    WanGeMsg selectByPrimaryKey(BigDecimal id);

    int updateByPrimaryKeySelective(WanGeMsg record);

    int updateByPrimaryKey(WanGeMsg record);
}