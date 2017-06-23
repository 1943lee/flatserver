package controller.meeting;

import java.util.List;

/**
 * Created by liulun on 2016/12/8.
 */
public class CreateMeetingInput {

    private String meetingName;

    private String userName;

    private List<Device> participant;

    public String getMeetingName() {
        return meetingName;
    }

    public void setMeetingName(String meetingName) {
        this.meetingName = meetingName;
    }

    public List<Device> getParticipant() {
        return participant;
    }

    public void setParticipant(List<Device> participant) {
        this.participant = participant;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}

class Device {
    private String deviceID;

    private String deviceType;

    private String callMode;

    private String isChairman;

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getCallMode() {
        return callMode;
    }

    public void setCallMode(String callMode) {
        this.callMode = callMode;
    }

    public String getIsChairman() {
        return isChairman;
    }

    public void setIsChairman(String isChairman) {
        this.isChairman = isChairman;
    }
}