package common;

import commoncache.ComCache;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hejiangbo on 2016/11/7.
 */
public class TimeHelper {

    /**
     * 获取当前系统时间
     * @param format
     * @return
     */
    public static String getCurrentTime(String format){
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(new Date());
    }

    /**
     * 格式化时间
     * @param date
     * @param format
     * @return
     */
    public static String formatTime(Date date,String format){
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }

    /**
     * String时间格式转换为Date
     * @param time
     * @return
     */
    public static Date StringToDate(String time){
        Date date = null;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
           date = df.parse(time);
        }

        catch (Exception e){
            e.printStackTrace();
        }
        return date;
    }

    /**
     *获取时间差(秒)
     * @param time1
     * @param time2
     * @return
     */
    public static int getTimeInterval(String time1,String time2){
        int interval = -1;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try{
            Date date1 = df.parse(time1);
            Date date2 = df.parse(time2);
            interval = (int) ((date2.getTime()-date1.getTime())/1000);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return  interval;
    }

    /**
     * 〈Unix时间戳格式化>
     * @author: hejiangbo
     */
    public static String timeStamp2DateFormat(Long timeStamp,String format){
        return new java.text.SimpleDateFormat(format).format(new java.util.Date(timeStamp));
    }

    /**
     * 〈Unix时间戳转换为date>
     * @author: hejiangbo
     */
    public static Date timeStamp2Date(Long timeStamp){
        return new java.util.Date(timeStamp);
    }

    /**
     * 将String型格式化
     * @param date String 想要格式化的日期
     * @param oldPattern String 想要格式化的日期的现有格式
     * @param newPattern String 想要格式化成什么格式
     * @return String
     */
    public static String StringPattern(String date, String oldPattern, String newPattern) {
        if (date == null || oldPattern == null || newPattern == null)
            return "";
        SimpleDateFormat sdf1 = new SimpleDateFormat(oldPattern) ;        // 实例化模板对象
        SimpleDateFormat sdf2 = new SimpleDateFormat(newPattern) ;        // 实例化模板对象
        Date d = null ;
        try{
            d = sdf1.parse(date) ;   // 将给定的字符串中的日期提取出来
        }catch(Exception e){            // 如果提供的字符串格式有错误，则进行异常处理
            e.printStackTrace() ;       // 打印异常信息
        }
        return sdf2.format(d);
    }

    /**
     * 获取移动设备设定的有效状态时间
     * @param format
     * @return
     */
    public static String getGpsValidTime(String format){
        String interval = ComCache.getInstance().getConfigCache().getPzxx("ydsbgxsj");
        if(interval.isEmpty())
            interval = "300";
        int m_interval = Integer.parseInt(interval);
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(new Date().getTime()- m_interval * 1000);
    }

    /**
     * 判断移动设备是否在有效的时间内
     * @param date
     * @return
     */
    public static boolean isValidGpsTime(Date date){
        String interval = ComCache.getInstance().getConfigCache().getPzxx("ydsbgxsj");
        if(interval.isEmpty())
            interval = "300";
        long m_interval = Long.parseLong(interval);
        Date now = new Date();
        long stamp = now.getTime()-date.getTime();
        if(stamp <= m_interval * 1000)
            return true;
        else
            return false;

    }

    /**
     * 判断时间是否大于当前时间
     * @param date
     * @return
     */
    public static boolean isBackwardTime(Date date){
        Date now = new Date();
        long stamp = now.getTime()-date.getTime();
        if(stamp < 0){
            return true;
        }
        return  false;
    }

    /**
     * 获取时间
     * @return
     */
    public static String getMatchUnitTime(int totalSec){
        String time = "小时";
        int hour, min, sec;
        hour = totalSec / 3600;
        min = (totalSec % 3600) / 60;
        sec = totalSec % 60;
        time = hour == 0 ? "": hour + "小时";
        time += min == 0 ? "" : min + "分钟";
        time += sec == 0 ? "" : sec + "秒";
        if (time.isEmpty())
            time = "0秒";
        return time;
    }
}
