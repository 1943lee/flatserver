package com.keda.wange.model;

import java.math.BigDecimal;
import java.util.Date;

public class WanGeMsg {
    private BigDecimal id;

    private String callId;

    private Short status;

    private Short sender;

    private String seat;

    private String seatIp;

    private Integer pId;

    private Date time;

    private String content;

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId == null ? null : callId.trim();
    }

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }

    public Short getSender() {
        return sender;
    }

    public void setSender(Short sender) {
        this.sender = sender;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat == null ? null : seat.trim();
    }

    public String getSeatIp() {
        return seatIp;
    }

    public void setSeatIp(String seatIp) {
        this.seatIp = seatIp == null ? null : seatIp.trim();
    }

    public Integer getpId() {
        return pId;
    }

    public void setpId(Integer pId) {
        this.pId = pId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }
}