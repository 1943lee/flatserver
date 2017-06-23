package common;

import com.esri.core.geometry.*;
import org.codehaus.jackson.JsonParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by hejiangbo on 2017/2/13.
 * Gis工具类
 */
public class GisUtils {

    private  static final Logger m_Logger = LoggerFactory.getLogger(GisUtils.class);  //日志信息

    /**
     * 判断指定点是否在线缓冲区间内
     * @param polylineJson
     * @param distance
     * @param longtitude
     * @param latitude
     * @return
     */
    public static boolean withinPolylineBuffer(String polylineJson,double distance,double longtitude,double latitude) {
        try
        {
            MapGeometry geometry = GeometryEngine.jsonToGeometry(polylineJson);
            Polyline line = (Polyline)geometry.getGeometry();
            SpatialReference spatialRef = SpatialReference.create(102100);
            Geometry outputGeom = OperatorBuffer.local().execute(line, spatialRef, distance, null);
            Point withPoint = new Point(longtitude,latitude);
            withPoint = GeographicToWebMercator(withPoint);
            return GeometryEngine.within(withPoint,outputGeom,spatialRef);

        } catch (JsonParseException ex) {
            ex.printStackTrace();
            return false;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

    }


    /**
     * 经纬度坐标转换墨卡托坐标
     * @param geographicPoint
     * @return
     */
    private static Point GeographicToWebMercator(Point geographicPoint)
    {
        if (geographicPoint == null)
        {
            return null;
        }
        double y = 0.0;
        if (geographicPoint.getX() <= -90.0)
        {
            y = Double.NEGATIVE_INFINITY;
        }
        else if (geographicPoint.getY() > 90.0)
        {
            y = Double.POSITIVE_INFINITY;
        }
        else
        {
            double a = geographicPoint.getY() * 0.017453292519943295;
            y = 3189068.5 * Math.log((1.0 + Math.sin(a)) / (1.0 - Math.sin(a)));
        }
        return new Point(6378137.0 * (geographicPoint.getX() * 0.017453292519943295), y, geographicPoint.getZ());
    }
}
