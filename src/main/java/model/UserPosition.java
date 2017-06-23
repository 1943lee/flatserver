package model;

/**
 * Created by hejiangbo on 2016/11/7.
 */
public class UserPosition {
    private String userCode;  //用户名
    private String deviceId; // 终端id
    private int deviceType; // 终端类型
    private String locDatetime; // 定位时间
    private String sysDateTime; // 系统插入时间
    private double latitude; // 纬度
    private double longitude; // 经度
    private double speed; // 速度(公里/小时)
    private double direction; // 方向(度)
    private String locFlag; // 定位标示,A/V,A为GPS,V为基站

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public String getLocDatetime() {
        return locDatetime;
    }

    public void setLocDateTime(String locDatetime) {
        this.locDatetime = locDatetime;
    }

    public String getSysDateTime() {
        return sysDateTime;
    }

    public void setSysDateTime(String sysDateTime) {
        this.sysDateTime = sysDateTime;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getDirection() {
        return direction;
    }

    public void setDirection(double direction) {
        this.direction = direction;
    }

    public String getLocFlag() {
        return locFlag;
    }

    public void setLocFlag(String locFlag) {
        this.locFlag = locFlag;
    }
}
