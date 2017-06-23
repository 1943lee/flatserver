package com.keda.patrol.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.keda.patrol.dao.PatrolAlarmInfoMapper;
import com.keda.patrol.model.PatrolAlarmInfo;
import common.*;
import controller.Application;
import dao.UnitDao;
import dao.UserDao;
import model.Unit;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;

/**
 * Created by hejiangbo on 2017/2/9.
 */
public class PatrolAlarmDetectionTask {

    private static PatrolAlarmDetectionTask tpm = new PatrolAlarmDetectionTask();
    // 线程池维护线程的最少数量
    private final static int CORE_POOL_SIZE = 1;

    // 线程池维护线程的最大数量
    private final static int MAX_POOL_SIZE = 5;

    // 线程池维护线程所允许的空闲时间
    private final static int KEEP_ALIVE_TIME = 0;

    // 线程池所使用的缓冲队列大小
    private final static int WORK_QUEUE_SIZE = 50;

    // 消息缓冲队列
    Queue<Map> msgQueue = new LinkedList<Map>();
    Map map = new HashMap();

    // 访问消息缓存的调度线程
    // 查看是否有待定请求，如果有，则创建一个新的Thread，并添加到线程池中
    final Runnable accessBufferThread = new Runnable() {

        @Override
        public void run() {
            if (hasMoreAcquire()) {
                Map msg = (Map) msgQueue.poll();
                Runnable task = new AlarmDetectionThread(msg);
                threadPool.execute(task);
            }
        }
    };


    final RejectedExecutionHandler handler = new RejectedExecutionHandler() {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            System.out.println(((AlarmDetectionThread) r).getMsg() + "消息放入队列中重新等待执行");
            msgQueue.offer(((AlarmDetectionThread) r).getMsg());
        }
    };

    // 管理数据库访问的线程池

    @SuppressWarnings({"rawtypes", "unchecked"})
    final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME,
            TimeUnit.SECONDS, new ArrayBlockingQueue(WORK_QUEUE_SIZE), this.handler);

    // 调度线程池
    final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


    @SuppressWarnings("rawtypes")
    final ScheduledFuture taskHandler = scheduler.scheduleAtFixedRate(accessBufferThread, 0, 1, TimeUnit.SECONDS);


    public static PatrolAlarmDetectionTask getInstance() {
        return tpm;
    }

    private boolean hasMoreAcquire() {
        return !msgQueue.isEmpty();
    }

    public void addTask(Map msg) {
        Runnable task = new AlarmDetectionThread(msg);
        threadPool.execute(task);
    }

}

//线程池中工作的线程
class AlarmDetectionThread implements Runnable {

    private Map msg;
    private  static final Logger m_Logger = LoggerFactory.getLogger(AlarmDetectionThread.class);  //日志信息

    public AlarmDetectionThread() {
        super();
    }

    public AlarmDetectionThread(Map msg) {
        this.msg = msg;
    }

    public Map getMsg() {
        return msg;
    }

    public void setMsg(Map msg) {
        this.msg = msg;
    }

    @Override
    public void run() {
        String updateSbbh = ComConvert.toString(msg.get("sbbh"));
        //巡逻路线任务列表(按设备分)
        Map<String, Map> patrolTaskList = PatrolLineTask.getIntance().getPatrolTaskList();
        if (null != patrolTaskList && patrolTaskList.containsKey(updateSbbh)) {
            for (Map.Entry<String, Map> entry : patrolTaskList.entrySet()) {
                Map<String, Object> patrolTask = entry.getValue();
                if (entry.getKey().equals(updateSbbh)) {
                    //经度
                    double longitude = ComConvert.toDouble(msg.get("sbjd"), 0);
                    //纬度
                    double latitude = ComConvert.toDouble(msg.get("sbwd"), 0);
                    //巡逻路线
                    String patrolLine = ComConvert.toString(patrolTask.get("xllx"));
                    if(patrolLine.startsWith("\"")) {
                        patrolLine = patrolLine.substring(1,patrolLine.length()-1);
                    }
                    //偏移距离
                    double offsetDistance = ComConvert.toDouble(patrolTask.get("pyjl"), 15);
                    //偏移时长
                    int offsetTime = ComConvert.toInteger(patrolTask.get("pysc"), 300);
                    //更新时间
                    String updateTime = ComConvert.toString(msg.get("gxsj"));
                    //若超出指定区域
                    if (!GisUtils.withinPolylineBuffer(patrolLine, offsetDistance, longitude, latitude)) {
                        String isDeflect = ComConvert.toString(patrolTask.get("sfpl"));
                        String isContinuousAlarm = ComConvert.toString(patrolTask.get("bjzt"));
                        //若已偏离路线且未连续报警(即上一段是正常的,避免若一直处在偏移状态时,会不断的发送报警信息)
                        if (isDeflect.equals("1") && isContinuousAlarm.equals("0")) {
                            String oldTime = ComConvert.toString(patrolTask.get("kssj"));
                            int interval = TimeHelper.getTimeInterval(oldTime, updateTime);
                            if (interval > offsetTime) {
                                Map<String, Object> alarmMap = new HashMap();
                                alarmMap.put("sbbh", entry.getKey());
                                alarmMap.put("sbsyz", msg.get("sbsyz"));
                                alarmMap.put("pyjl", offsetDistance);
                                alarmMap.put("pysc", offsetTime);
                                alarmMap.put("kssj", oldTime);
                                alarmMap.put("jssj", updateTime);
                                alarmMap.put("sjpysc", interval);
                                alarmMap.put("rwbh", patrolTask.get("rwbh"));
                                if(sendPatrolAlarm(alarmMap)) {
                                    //报警状态置为已报警
                                    patrolTask.put("bjzt", "1");
                                    patrolTask.put("kssj", updateTime);
                                }
                            }
                        } else {
                            patrolTask.put("kssj", updateTime);
                            patrolTask.put("sfpl", "1");
                        }
                    } else {
                        //重置偏移标示
                        patrolTask.put("sfpl", "0");
                        //重置报警状态
                        patrolTask.put("bjzt","0");
                        //开始时间
                        patrolTask.put("kssj", updateTime);
                    }
                    //一个设备理论上同时只能执行一个任务
                    break;
                }
            }
        }
    }

    /**
     * 发送巡逻报警信息
     */
    private boolean sendPatrolAlarm(Map alarmMap) {
        //设备使用者
        String deviceUser = ComConvert.toString(alarmMap.get("sbsyz"));
        //获取人员名称
        String userName = DeviceDbOperate.getUserName(deviceUser);
        //偏移距离配置
        String offsetDistance = ComConvert.toString(alarmMap.get("pyjl"));
        //偏移时长配置
        int offsetTime = ComConvert.toInteger(alarmMap.get("pysc"),300);
        //报警时间
        String endTime = ComConvert.toString(alarmMap.get("jssj"));
        //开始偏离轨迹时间
        String startTime = ComConvert.toString(alarmMap.get("kssj"));
        //获取人员单位编号
        UserDao userDao = Application.myContext.getBean("userDao", UserDao.class);
        User queryUser = new User();
        queryUser.setDlm(deviceUser);
        User userResult = userDao.getUserByDlm(queryUser);
        if(null == userResult){
            m_Logger.debug("巡逻报警:查询人员信息为空,查询用户:" + deviceUser);
            return false;
        }
        String userUnit = ComConvert.toString(userResult.getLsdw());

        //获取单位名称
        UnitDao unitDao = Application.myContext.getBean("unitDao", UnitDao.class);
        Unit unitQuery = new Unit();
        unitQuery.setDwbh(userUnit);
        Unit unitResult = unitDao.getUnitByDwbh(unitQuery);
        if(null == unitResult){
            m_Logger.debug("巡逻报警:查询人员单位信息为空,单位编号:" + userUnit);
            return false;
        }
        String unitName = ComConvert.toString(unitResult.getDwmc());
        String taskId = ComConvert.toString(alarmMap.get("rwbh"));
        int interval = ComConvert.toInteger(alarmMap.get("sjpysc"), 0);
        String setDuration = TimeHelper.getMatchUnitTime(offsetTime);
        String actualDuration = TimeHelper.getMatchUnitTime(interval);
        String alarmInfo = "巡逻人:" + userName + "所属单位(" + unitName + ")于" + startTime + " 开始偏离预设置的轨迹范围(最大偏移距离" + offsetDistance + "米," +
                "最大偏移时长" + setDuration + ")" + " 持续" + actualDuration;

        //巡逻报警
        PatrolAlarmInfoMapper patrolAlarmInfoMapper = Application.myContext.getBean(PatrolAlarmInfoMapper.class);
        PatrolAlarmInfo patrolAlarmInfo = new PatrolAlarmInfo();
        patrolAlarmInfo.setBjbh(taskId);
        patrolAlarmInfo.setBjnr(alarmInfo);
        patrolAlarmInfo.setBjsj(endTime);
        patrolAlarmInfo.setGjr(deviceUser);
        patrolAlarmInfo.setGjrdw(userUnit);
        patrolAlarmInfoMapper.insert(patrolAlarmInfo);

        //redis通知任务报警信息
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode root = mapper.createObjectNode();
            ObjectNode operaInfo = mapper.createObjectNode();
            String json = mapper.writeValueAsString(alarmMap);
            operaInfo.set("content", mapper.readTree(json));
            root.put("operaType", 1);
            root.set("operaInfo", operaInfo);
            RedisUtil redisUtil = RedisUtil.getInstance();
            redisUtil.publish(ComDefine.PATROL_ALARM, ComConvert.toString(root));
            m_Logger.debug(ComDefine.PATROL_ALARM + ComConvert.toString(root));
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
