package common;
import java.util.Map;
import java.util.UUID;

import commoncache.ComCache;
import com.alibaba.fastjson.JSON;
import model.Device;
import model.UserPosition;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;

import com.kedacom.uc.sdk.api.pojo.UserPositionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by hejiangbo on 2016/11/7.
 * GPS历史数据类
 */
public class EsGpsHisData {

    private static String esGpsAliasDbName = "b_gps_ls";
    private static String esGpsTableName = "b_gps_ls";

    private static String esGpsWriteDbName = "gps_write";

    private static EsGpsHisData esGpsHisData = new EsGpsHisData();
    private Logger logger;


    /**
     * 单例对象
     */
    public static EsGpsHisData getInstance() {
        return esGpsHisData;
    }

    public EsGpsHisData()
    {
        logger = LoggerFactory.getLogger(EsGpsHisData.class);
        intiEsGpsHisConfig();
    }

    private void intiEsGpsHisConfig()
    {
        String gpsIndex = ComCache.getInstance().getConfigCache().getPzxx("esgpslspz");
        String [] temp = gpsIndex.split(";");
        if(temp.length ==3)
        {
            esGpsAliasDbName = temp[0];
            esGpsTableName = temp[1];
            esGpsWriteDbName = temp[2];
        }
    }

    /**
     * 〈es插入历史GPS信息>
     *
     * @author: hejiangbo
     * @since 2016-11-07
     */
    public void insert(Map device) {
        Client client = ESClient.getInstance();
        UserPosition userPosition = new UserPosition();

        userPosition.setSysDateTime(TimeHelper.StringPattern(String.valueOf(device.get("gxsj")),"yyyy-MM-dd HH:mm:ss","yyyy-MM-dd HH:mm:ss.SSS"));

        userPosition.setDeviceId(String.valueOf(device.get("sbbh")));
        userPosition.setLatitude(ComConvert.toDouble(device.get("sbwd"),0));
        userPosition.setLongitude(ComConvert.toDouble(device.get("sbjd"),0));
        userPosition.setLocFlag("");
        userPosition.setDirection(ComConvert.toDouble(device.get("fx"),0));
        userPosition.setUserCode(String.valueOf(device.get("sbsyz")));
        userPosition.setSpeed(ComConvert.toDouble(device.get("sd"),0));
        IndexRequest indexRequest = new IndexRequest(esGpsWriteDbName,
                esGpsTableName, String.valueOf(UUID.randomUUID())).source(JSON
                .toJSONString(userPosition));
        UpdateRequest updateRequest = new UpdateRequest(esGpsWriteDbName,
                esGpsTableName, String.valueOf(UUID.randomUUID())).doc(
                JSON.toJSONString(userPosition)).upsert(indexRequest);
        UpdateResponse response = client.update(updateRequest).actionGet();
        logger.info("插入历史数据：" + response.getId());
    }
}
