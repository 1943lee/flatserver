package com.keda.patrol.model;

public class PatrolAlarmInfo {
    private String bjbh;

    private String bjnr;

    private String bjsj;

    private String gjr;

    private String gjrdw;

    private String ylzd1;

    private String ylzd2;

    private String ylzd3;

    public String getBjbh() {
        return bjbh;
    }

    public void setBjbh(String bjbh) {
        this.bjbh = bjbh == null ? null : bjbh.trim();
    }

    public String getBjnr() {
        return bjnr;
    }

    public void setBjnr(String bjnr) {
        this.bjnr = bjnr == null ? null : bjnr.trim();
    }

    public String getBjsj() {
        return bjsj;
    }

    public void setBjsj(String bjsj) {
        this.bjsj = bjsj == null ? null : bjsj.trim();
    }

    public String getGjr() {
        return gjr;
    }

    public void setGjr(String gjr) {
        this.gjr = gjr == null ? null : gjr.trim();
    }

    public String getGjrdw() {
        return gjrdw;
    }

    public void setGjrdw(String gjrdw) {
        this.gjrdw = gjrdw == null ? null : gjrdw.trim();
    }

    public String getYlzd1() {
        return ylzd1;
    }

    public void setYlzd1(String ylzd1) {
        this.ylzd1 = ylzd1 == null ? null : ylzd1.trim();
    }

    public String getYlzd2() {
        return ylzd2;
    }

    public void setYlzd2(String ylzd2) {
        this.ylzd2 = ylzd2 == null ? null : ylzd2.trim();
    }

    public String getYlzd3() {
        return ylzd3;
    }

    public void setYlzd3(String ylzd3) {
        this.ylzd3 = ylzd3 == null ? null : ylzd3.trim();
    }
}