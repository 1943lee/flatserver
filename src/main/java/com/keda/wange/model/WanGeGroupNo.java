package com.keda.wange.model;

public class WanGeGroupNo {
    private String wangeGroupNo;

    private String cvsMeetingId;

    private String wangeFromDevices;

    public String getWangeGroupNo() {
        return wangeGroupNo;
    }

    public void setWangeGroupNo(String wangeGroupNo) {
        this.wangeGroupNo = wangeGroupNo == null ? null : wangeGroupNo.trim();
    }

    public String getCvsMeetingId() {
        return cvsMeetingId;
    }

    public void setCvsMeetingId(String cvsMeetingId) {
        this.cvsMeetingId = cvsMeetingId == null ? null : cvsMeetingId.trim();
    }

    public String getWangeFromDevices() {
        return wangeFromDevices;
    }

    public void setWangeFromDevices(String wangeFromDevices) {
        this.wangeFromDevices = wangeFromDevices == null ? null : wangeFromDevices.trim();
    }
}