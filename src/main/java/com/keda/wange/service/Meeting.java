package com.keda.wange.service;

import com.keda.wange.model.WanGeGroupNo;

import java.util.concurrent.TimeoutException;

/**
 * Created by liulun on 2016/12/9.
 */
public interface Meeting {
    public String regroup(String from, String to)throws TimeoutException;

    String getAvailableGroupNo();

    void releaseGroupNo(String from, String wangeGroupNo);

    void recordGroupUsage(WanGeGroupNo wanGeGroupUsage);

    void releaseWanGeGroupIfWanGeInvolved(String csvMeetingId);

    boolean isCvsMeetingAlive(String cvsMeetingId);
}
