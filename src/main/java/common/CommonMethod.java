package common;

import commoncache.ComCache;

/**
 * Created by hejiangbo on 2016/12/29.
 */
public class CommonMethod {

    /**
     * 判断是否加载ptt相关接口及其数据
     */
    public static boolean isLoadPtt(){
        String pttServer = ComCache.getInstance().getConfigCache().getPzxx("pttserver");
        if(pttServer.isEmpty()){
            return false;
        }
        return  true;
    }
}
