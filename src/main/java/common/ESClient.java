package common;

import static org.elasticsearch.common.settings.ImmutableSettings.settingsBuilder;

import commoncache.ComCache;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ESClient {
    private static Logger _logger = LoggerFactory.getLogger(ESClient.class);
    private static TransportClient _client = null;

    public static Client getInstance(){
        try {
            if (_client == null) {

                String clusterName = ComCache.getInstance().getConfigCache().getPzxx("esclustername");
                if (clusterName.length() < 1){
                    _logger.error("ES clustername 未设置");
                    return _client;
                }
                Settings settings = settingsBuilder().put("cluster.name", clusterName)
                        .put("client.transport.sniff", Boolean.TRUE)
                        .put("client.transport.ignore_cluster_name", Boolean.FALSE)
                        .put("client.transport.ping_timeout", 1000)
                        .put("client.transport.nodes_sampler_interval","")
                        .build();
                _client = new TransportClient(settings);

                String esSvrIp = ComCache.getInstance().getConfigCache().getPzxx("esfwqip");
                if (esSvrIp.length() < 1){
                    _logger.error("ES IP及端口未设置");
                    return _client;
                }
                String host = "";
                String port = "";

                if (esSvrIp.length() > 0) {
                    String[] temp = esSvrIp.split(":");
                    if (temp.length == 2) {
                        host = temp[0];
                        port = temp[1];
                    }
                }
                _client.addTransportAddress(new InetSocketTransportAddress(
                        host, Integer.valueOf(port)));
                _client.connectedNodes();
            }
        } catch (Exception e) {
            _logger.error("连接ES异常");
        }

        return _client;
    }
	
}
