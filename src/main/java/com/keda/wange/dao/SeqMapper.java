package com.keda.wange.dao;

import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

/**
 * Created by liulun on 2016/12/8.
 */
public interface SeqMapper {
    BigDecimal getMsgSeq();
}
