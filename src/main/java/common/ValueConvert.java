package common;

import kedanet.telnet.test;

import java.util.List;

/**
 * Created by tanhuizhen on 2016/12/23.
 */
public class ValueConvert {
    public ValueConvert(String p1, String p2) {
        param_1 = p1;
        param_2 = p2;
    }

    private String param_1;
    private String param_2;

    public String getParam_1() {
        return param_1;
    }

    public void setParam_1(String param_1) {
        this.param_1 = param_1;
    }

    public String getParam_2() {
        return param_2;
    }

    public void setParam_2(String param_2) {
        this.param_2 = param_2;
    }

    public static String convertStr(int type, List<ValueConvert> l, String value) {
        if (type == 1) {
            for (int i = 0; i < l.size(); i++) {
                if (l.get(i).param_1.equals(value)) {
                    return l.get(i).param_2;
                }
            }
        } else if (type == 2) {
            for (int i = 0; i < l.size(); i++) {
                if (l.get(i).param_2.equals(value)) {
                    return l.get(i).param_1;
                }
            }
        }
        return null;
    }
}
