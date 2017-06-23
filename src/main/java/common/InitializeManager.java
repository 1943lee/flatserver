package common;

import com.keda.patrol.service.PatrolLineManager;
import commoncache.ComCache;
import controller.Application;
import sync.SyncTask;


/**
 * Created by hejiangbo on 2016/11/3.
 */
public class InitializeManager {
    public InitializeManager() {

    }

    /**
     * 初始化各项数据
     */
    public void init(){
        ComCache m_ComCache =  ComCache.getInstance();
        // 先初始化换配置信息缓存
        m_ComCache.initCache(ComDefine.INIT_CONFIG);
        // redis初始化
        RedisUtil.getInstance();
        // 位置相关服务开关
        String enableLocationServer = Application.getAppConfig().getProperty("enableLocationServer");
        if(null!=enableLocationServer && "true".equalsIgnoreCase(enableLocationServer)) {
            // ES初始化
            EsGpsHisData.getInstance();
        }

        // 同步PTT开关
        String enablePttSync =  Application.getAppConfig().getProperty("enablePttSync");
        if(null!=enablePttSync && "true".equalsIgnoreCase(enablePttSync)) {
            PttUtils pttUtils = new PttUtils();
            pttUtils.initialize();

            // 定时同步人员单位信息
            SyncTask syncTask = new SyncTask();
            syncTask.StartTask();
        }

        // 初始化设备缓存信息
        m_ComCache.initCache(ComDefine.INIT_DEVICE);

        // 同步夜眼开关
        String enableVcsSync = Application.getAppConfig().getProperty("enableVcsSync");
        if(null != enableVcsSync && "true".equalsIgnoreCase(enableVcsSync)) {
            VcsUtils vcsUtils = new VcsUtils();
            vcsUtils.initialize();
        }

        //巡逻任务服务开关
        String enablePatrolServer = Application.getAppConfig().getProperty("enablePatrolServer");
        if(null != enablePatrolServer && "true".equalsIgnoreCase(enablePatrolServer)) {
            PatrolLineManager patrolLineManager = new PatrolLineManager();
            patrolLineManager.initialize();
        }
    }
}
