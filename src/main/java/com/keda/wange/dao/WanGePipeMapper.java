package com.keda.wange.dao;

import com.keda.wange.model.WanGePipe;
import com.keda.wange.model.WanGePipeExample;
import java.math.BigDecimal;
import java.util.List;

public interface WanGePipeMapper {
    int countByExample(WanGePipeExample example);

    int deleteByExample(WanGePipeExample example);

    int deleteByPrimaryKey(BigDecimal id);

    int insert(WanGePipe record);

    int insertSelective(WanGePipe record);

    List<WanGePipe> selectByExample(WanGePipeExample example);

    WanGePipe selectByPrimaryKey(BigDecimal id);

    int updateByPrimaryKeySelective(WanGePipe record);

    int updateByPrimaryKey(WanGePipe record);
}