package common;

import java.io.Reader;
import java.sql.Clob;

/**
 * Created by hejiangbo on 2016/11/3.
 */
public class ComConvert {

    /**
     * object转换int的函数
     */
    public static int toInteger(Object value, int defaultValue)
    {
        try
        {
            return (null != value) ? Integer.parseInt(value.toString()) : defaultValue;
        }
        catch (Exception e)
        {
            return defaultValue;
        }
    }

    /**
     * object转换long的函数
     */
    public static long toLong(Object value, long defaultValue)
    {
        try
        {
            return (null != value) ? Long.parseLong(value.toString()) : defaultValue;
        }
        catch (Exception e)
        {
            return defaultValue;
        }
    }

    /**
     * object转换double的函数
     */
    public static double toDouble(Object value, double defaultValue)
    {
        try
        {
            return (null != value) ? Double.parseDouble(value.toString()) : defaultValue;
        }
        catch (Exception e)
        {
            return defaultValue;
        }
    }

    /**
     * object转换string的函数
     */
    public static String toString(Object value)
    {
        return (null != value) ? value.toString() : "";
    }

    /**
     * 将Clob转成String ,静态方法
     *
     * @param clob
     *            字段
     * @return 内容字串，如果出现错误，返回 null
     */
    public static String clobToString(Clob clob) {
        if (clob == null)
            return null;
        StringBuffer sb = new StringBuffer();
        Reader clobStream = null;
        try {
            clobStream = clob.getCharacterStream();
            char[] b = new char[60000];// 每次获取60K
            int i = 0;
            while ((i = clobStream.read(b)) != -1) {
                sb.append(b, 0, i);
            }
        } catch (Exception ex) {
            sb = null;
        } finally {
            try {
                if (clobStream != null) {
                    clobStream.close();
                }
            } catch (Exception e) {
            }
        }
        if (sb == null)
            return null;
        else
            return sb.toString();
    }

    public static boolean IsValidGeometryData(double lon, double lat)
    {
        return ((((lon > 70.0) && (lon < 140.0)) && (lat > 15.0)) && (lat < 60.0));
    }
}
