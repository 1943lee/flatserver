package commoncache;

import common.ComConvert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hejiangbo on 2016/11/3.
 */
public class ConfigCache {
    /**
     * 配置信息缓存
     */
    private Map m_pzxxMap = new HashMap<String, Map>();

    /**
     * 获取配置信息的方法
     */
    public String getPzxx(String key)
    {
        Object value = null;
        if (m_pzxxMap.containsKey(key))
        {
            value = ((Map)m_pzxxMap.get(key)).get("pzz");
        }

        return ComConvert.toString(value);
    }

    /**
     * 初始化缓存的方法
     */
    public void initCache(Connection connection)
    {
        m_pzxxMap.clear();

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        String sqlStatement = "SELECT "
                + "			A.XH			AS xh, "
                + "			A.BLM			AS blm, "
                + "			A.BLZWM			AS blzwm, "
                + "			A.PZZ			AS pzz "
                + "FROM "
                + "			B_QJ_XTPZ		A";
        try {
             preparedStatement = connection.prepareStatement(sqlStatement);
             resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Map map = new HashMap<String, Object>();
                map.put("xh", resultSet.getObject("xh"));
                map.put("blm", resultSet.getObject("blm"));
                map.put("blzwm", resultSet.getObject("blzwm"));
                map.put("pzz", resultSet.getObject("pzz"));

                m_pzxxMap.put(map.get("blm").toString(), map);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (null != connection)
                    connection.close();
                if(null != resultSet)
                    resultSet.close();
                if(null != preparedStatement)
                    preparedStatement.close();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }

}
