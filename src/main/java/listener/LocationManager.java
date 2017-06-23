package listener;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by hejiangbo on 2017/2/8.
 */


 public class LocationManager{
     private Collection m_Listeners;

    /**
     * 增加监听
      * @param listener
     */
    public void addLocationListener(LocationListener listener){
         if(null == m_Listeners){
             m_Listeners = new HashSet();
         }
         m_Listeners.add(listener);
     }

    /**
     * 移除监听
     * @param listener
     */
     public void removeLocationListener(LocationListener listener){
         if(null == m_Listeners)
             return;
         m_Listeners.remove(listener);
     }

    /**
     * 通知监听更新
      * @param msg
     */
    public void noticeListeners(Map msg){
         Iterator ter = m_Listeners.iterator();
         while (ter.hasNext()){
             LocationListener listener = (LocationListener) ter.next();
             listener.onLocationUpdate(msg);
         }
     }

    /**
     * 监听接口
     */
   public interface LocationListener{
        void onLocationUpdate(Map device);
    }

}



