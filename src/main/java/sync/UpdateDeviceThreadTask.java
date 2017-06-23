package sync;

import commoncache.ComCache;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by hejiangbo on 2016/11/11.
 */
public class UpdateDeviceThreadTask {

    private static UpdateDeviceThreadTask tpm = new UpdateDeviceThreadTask();

    // 线程池维护线程的最少数量
    private final static int CORE_POOL_SIZE = 2;

    // 线程池维护线程的最大数量
    private final static int MAX_POOL_SIZE = 20;

    // 线程池维护线程所允许的空闲时间
    private final static int KEEP_ALIVE_TIME = 0;

    // 线程池所使用的缓冲队列大小
    private final static int WORK_QUEUE_SIZE = 50;

    //更新设备方式类型
    private String m_UpdateType;

    // 消息缓冲队列
    Queue<Map> msgQueue = new LinkedList<Map>();

    // 访问消息缓存的调度线程
    // 查看是否有待定请求，如果有，则创建一个新的AccessDBThread，并添加到线程池中
    final Runnable accessBufferThread = new Runnable() {

        @Override
        public void run() {
            if(hasMoreAcquire()){
                Map msg = ( Map ) msgQueue.poll();
                Runnable task = new AccessDBPositionThread( msg,m_UpdateType );
                threadPool.execute( task );
            }
        }
    };


    final RejectedExecutionHandler handler = new RejectedExecutionHandler(){

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            System.out.println(((AccessDBPositionThread )r).getMsg()+"消息放入队列中重新等待执行");
            msgQueue.offer((( AccessDBPositionThread ) r ).getMsg() );

}
};

    // 管理数据库访问的线程池

    @SuppressWarnings({ "rawtypes", "unchecked" })
    final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME,
            TimeUnit.SECONDS,new ArrayBlockingQueue(WORK_QUEUE_SIZE), this.handler);

    // 调度线程池
    final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);



    @SuppressWarnings("rawtypes")
    final ScheduledFuture taskHandler = scheduler.scheduleAtFixedRate(accessBufferThread, 0, 200, TimeUnit.MILLISECONDS);

    public static UpdateDeviceThreadTask newInstance() {

        return tpm;
    }

    private UpdateDeviceThreadTask(){}

    private boolean hasMoreAcquire(){
        return !msgQueue.isEmpty();
    }

    public void addTask( Map msg,String updateType) {
        m_UpdateType = updateType;
        Runnable task = new AccessDBPositionThread( msg,m_UpdateType );
        threadPool.execute( task );
    }

}

//线程池中工作的线程
class AccessDBPositionThread implements Runnable {

    private Map msg;
    private String m_UpdateType;
    public AccessDBPositionThread() {
        super();
    }

    public AccessDBPositionThread(Map msg,String updateType) {
        this.msg = msg;
        m_UpdateType = updateType;
    }

    public Map getMsg() {
        return msg;
    }

    public void setMsg(Map msg) {
        this.msg = msg;
    }

    @Override
    public void run() {
        ComCache.getInstance().updateDevice(msg,m_UpdateType);
    }

}
