package com.keda.wange.dao;

import com.keda.wange.model.WanGeGroupNo;
import com.keda.wange.model.WanGeGroupNoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface WanGeGroupNoMapper {
    long countByExample(WanGeGroupNoExample example);

    int deleteByExample(WanGeGroupNoExample example);

    int deleteByPrimaryKey(String wangeGroupNo);

    int insert(WanGeGroupNo record);

    int insertSelective(WanGeGroupNo record);

    List<WanGeGroupNo> selectByExample(WanGeGroupNoExample example);

    WanGeGroupNo selectByPrimaryKey(String wangeGroupNo);

    int updateByExampleSelective(@Param("record") WanGeGroupNo record, @Param("example") WanGeGroupNoExample example);

    int updateByExample(@Param("record") WanGeGroupNo record, @Param("example") WanGeGroupNoExample example);

    int updateByPrimaryKeySelective(WanGeGroupNo record);

    int updateByPrimaryKey(WanGeGroupNo record);
}