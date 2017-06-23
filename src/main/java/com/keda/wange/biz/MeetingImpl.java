package com.keda.wange.biz;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.keda.wange.dao.SeqMapper;
import com.keda.wange.dao.WanGeGroupNoMapper;
import com.keda.wange.dao.WanGeMsgMapper;
import com.keda.wange.dao.WanGePipeMapper;
import com.keda.wange.model.*;
import com.keda.wange.service.Meeting;
import controller.Application;
import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * Created by liulun on 2016/12/8.
 */
@Service
public class MeetingImpl implements Meeting {

    private Logger logger = LoggerFactory.getLogger(MeetingImpl.class);

    //@Autowired
    private WanGePipeMapper pipeDao = Application.myContext.getBean(WanGePipeMapper.class);

    //@Autowired
    private WanGeMsgMapper msgDao = Application.myContext.getBean(WanGeMsgMapper.class);

    //@Autowired
    private SeqMapper seqMapper = Application.myContext.getBean(SeqMapper.class);

    private WanGeGroupNoMapper groupNoMapper = Application.myContext.getBean(WanGeGroupNoMapper.class);

    private final short STATUS_REQUEST = 1;
    private final short STATUS_RESPONSE = 2;

    private final short SENDER_CONTROL_CENTER = 1;
    private final short SENDER_PDT = 2;

    private final int PROTOCOL_ID_MESSAGE = 1;
    private final int PROTOCOL_ID_ACK = 2;
    private final int PROTOCOL_ID_REGROUP = 3;
    private final int PROTOCOL_ID_REACK = 4;

    @Value("${wanGe.pollingResponseInterval}")
    private long pollingResponseInterval;

    @Value("${wanGe.responseTimeoutBase}")
    private long responseTimeoutBase;

    @Value("${wanGe.responseTimeoutPerDevice}")
    private long responseTimeoutBasePerDevice;

    private String makeRequestContent(String callId, String from, String to, String action){
        ContentXml contentXml = new ContentXml();
        contentXml.getHeader().setCmd("REGROUP");
        contentXml.getHeader().setFrom(from);
        contentXml.getHeader().setTo(to);
        contentXml.getHeader().setCallId(callId);
        contentXml.getHeader().setDataType(action);

        contentXml.getBody().setCallType("0");
        contentXml.getBody().setCallParameter("0");
        String nodePart = ContentXmlConvertor.obj2xml(contentXml);
        String prefix = "<?xml  version=\"1.0\"  encoding=\"UTF-8\" ?> ";
        return prefix + nodePart;
    }

    /**
     * 组会或者解除组会
     * @param from 逗号分隔的设备编号
     * @return 组成功的组号
     */
    public List<WanGeMsg> regroup(String from, String to, String action)throws TimeoutException {
        WanGePipe msg = new WanGePipe();
        msg.setId(seqMapper.getMsgSeq());
        String callId = UUID.randomUUID().toString();
        msg.setSeat("999"); // 万格张扬说坐席随便填一个，IP用本机IP好了
        String seatIp = null;
        try{
            seatIp = InetAddress.getLocalHost().getHostAddress();
        }catch(UnknownHostException e){
            seatIp = "10.192.38.189";
        }
        msg.setSeatIp(seatIp);
        msg.setCallId(callId);
        msg.setStatus(STATUS_REQUEST);
        msg.setSender(SENDER_CONTROL_CENTER);
        msg.setpId(PROTOCOL_ID_REGROUP);
        msg.setTime(new Date());
        msg.setContent(makeRequestContent(callId, from, to, action));
        insertMsg(msg);

        Date startTime = new Date();
        List<WanGeMsg> response;
        int deviceCount = from.split(",").length;
        logger.debug("开始 组会或者解除组会 " + action);
        long timeoutValue = responseTimeoutBase + responseTimeoutBasePerDevice * deviceCount;
        logger.debug("超时时间：" + timeoutValue + " 毫秒");
        do{
            response = getResponse(callId);
            if(response == null || response.size() < deviceCount){
                Date currentTime = new Date();
                long elapsedTime = currentTime.getTime() - startTime.getTime();

                logger.debug("没有获得足够的返回行，已经耗时毫秒数：" + elapsedTime);

                if(elapsedTime > timeoutValue){
                    logger.debug("等待返回超时，放弃等待");
                    deleteCall(callId);
                    if("register".equals(action)){
                        logger.debug("组会超时，通知万格结会");
                        releaseGroupNo(from, to);
                    }
                    throw new TimeoutException();
                }else{
                    try {
                        logger.debug("继续等待 " + pollingResponseInterval + " 毫秒");
                        Thread.sleep(pollingResponseInterval);
                    } catch (InterruptedException e) {
                        logger.error(e.toString());
                    }
                }
            }else{
                break;
            }
        }while(true);

        deleteCall(callId);

        return response;
    }

    @Override
    public String regroup(String from, String to) throws TimeoutException {
        List<WanGeMsg> msgs = regroup(from, to, "register");

        List<WanGeMsg> successfulMessages = new ArrayList<>();
        List<WanGeMsg> failedMessages = new ArrayList<>();
        for(int i = 0;i < msgs.size();i ++){
            WanGeMsg wanGeMsg = msgs.get(i);
            String content = wanGeMsg.getContent();
            ContentXml contentXml = ContentXmlConvertor.xml2obj(content);
            if("200".equals(contentXml.getBody().getResponse())){
                successfulMessages.add(wanGeMsg);
            }else{
                failedMessages.add(wanGeMsg);
            }
        }
        logger.debug("呼叫成功的对讲机数量：" + successfulMessages.size());
        logger.debug("呼叫失败的对讲机数量：" + failedMessages.size());
        if(successfulMessages.size() > 0){
            logger.debug("万格组号：" + to);
            return to;
        }
        return null;
    }

    @Override
    public String getAvailableGroupNo() {
        // 便利组号数据表，释放关闭的组号，获得一个可用组号
        logger.debug("开始查找可用的组号");
        List<WanGeGroupNo> wanGeGroupNos = groupNoMapper.selectByExample(new WanGeGroupNoExample());
        if(wanGeGroupNos == null || wanGeGroupNos.size() ==0){
            logger.error("没有配置组号，请在万格数据库 t_GROUP_NO 里面配置");
            return null;
        }

        for(int i = 0; i < wanGeGroupNos.size();i ++){
            WanGeGroupNo wanGeGroupNo = wanGeGroupNos.get(i);
            logger.debug("检查组号：" + wanGeGroupNo);
            String cvsMeetingId = wanGeGroupNo.getCvsMeetingId();
            logger.debug("对应会议号：" + cvsMeetingId);
            if(cvsMeetingId == null || cvsMeetingId.length() == 0){
                logger.debug("找到未被使用组号：" + wanGeGroupNo.getWangeGroupNo());
                return wanGeGroupNo.getWangeGroupNo();
            }else{
                boolean isMeetingAlive = isMeetingAlive(wanGeGroupNo);
                if(! isMeetingAlive){
                    logger.debug("会议已关闭");

                    logger.debug("正在释放万格组号资源");
                    releaseGroupNo(wanGeGroupNo.getWangeFromDevices(), wanGeGroupNo.getWangeGroupNo());

                    logger.debug("正在清空组号使用记录");
                    wanGeGroupNo.setCvsMeetingId(null);
                    wanGeGroupNo.setWangeFromDevices(null);
                    groupNoMapper.updateByPrimaryKey(wanGeGroupNo);

                    logger.debug("找到未被使用组号：" + wanGeGroupNo.getWangeGroupNo());
                    return wanGeGroupNo.getWangeGroupNo();
                }
            }
        }
        logger.error("没有找到可用的组号");
        return null;
    }

    @Override
    public void releaseGroupNo(String from, String wangeGroupNo) {
        logger.debug("释放万格组号：" + wangeGroupNo);
        try {
            regroup(from, wangeGroupNo, "unregister");
        } catch (TimeoutException e) {
            logger.error("释放万格组号超时");
            e.printStackTrace();
        }
    }



    @Override
    public void recordGroupUsage(WanGeGroupNo wanGeGroupUsage) {
        groupNoMapper.updateByPrimaryKey(wanGeGroupUsage);
    }

    @Override
    public void releaseWanGeGroupIfWanGeInvolved(String csvMeetingId) {
        WanGeGroupNoExample c = new WanGeGroupNoExample();
        c.createCriteria().andCvsMeetingIdEqualTo(csvMeetingId);
        List<WanGeGroupNo> wanGeGroupNos = groupNoMapper.selectByExample(c);
        logger.debug("CVS 结会：" + csvMeetingId);
        if(wanGeGroupNos == null || wanGeGroupNos.size() == 0){
            logger.debug("该会议没有占用万格组号：" + csvMeetingId);
        }else{
            WanGeGroupNo wanGeGroupNoUsage = wanGeGroupNos.get(0);

            releaseGroupNo(wanGeGroupNoUsage.getWangeFromDevices(), wanGeGroupNoUsage.getWangeGroupNo());

            logger.debug("正在清空组号使用记录");
            wanGeGroupNoUsage.setCvsMeetingId(null);
            wanGeGroupNoUsage.setWangeFromDevices(null);
            groupNoMapper.updateByPrimaryKey(wanGeGroupNoUsage);
        }
    }

    @Value("${vcs.server.url}/meeting/get")
    private String vcsGetMeetingUrl;

    private boolean isMeetingAlive(WanGeGroupNo wanGeGroupNo) {
        String cvsMeetingId = wanGeGroupNo.getCvsMeetingId();

        return isCvsMeetingAlive(cvsMeetingId);
    }

    public boolean isCvsMeetingAlive(String cvsMeetingId) {
        ObjectNode r = JsonNodeFactory.instance.objectNode();
        ObjectMapper om = new ObjectMapper();

        HttpPost httpPost = new HttpPost(vcsGetMeetingUrl);
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();

        formparams.add(new BasicNameValuePair("preCaseId", cvsMeetingId));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
        httpPost.setEntity(entity);

        CloseableHttpClient httpclient = HttpClients.createDefault();

        try {
            logger.debug("查询会议状态：" + cvsMeetingId);
            logger.debug("地址：" + vcsGetMeetingUrl);
            logger.debug("参数：" + formparams.toString());
            CloseableHttpResponse response = httpclient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if(statusCode == 200){
                String result = EntityUtils.toString(response.getEntity());
                logger.debug("夜眼返回：" + result);
                r = (ObjectNode) om.readTree(result);
            }else{
                logger.error("查询状态失败，夜眼返回状态：" + statusCode);
                r.put("success", false);
                r.put("msg", String.valueOf(statusCode));
            }
        } catch (IOException e) {
            r.put("success", false);
            r.put("msg", e.getMessage());
            e.printStackTrace();
        }
        return r.get("success").asBoolean();
    }

    @Transactional
    public void insertMsg(WanGePipe msg){
        logger.debug("向万格发起请求，callId：" + msg.getCallId());
        pipeDao.insert(msg);
    }

    @Transactional
    public List<WanGeMsg> getResponse(String callId){
        logger.debug("尝试获取万格返回值，callId：" + callId);
        WanGeMsgExample c = new WanGeMsgExample();
        c.createCriteria()
                .andCallIdEqualTo(callId)
                .andStatusEqualTo(STATUS_RESPONSE);
        List<WanGeMsg> wanGeMsgs = msgDao.selectByExample(c);
        if(wanGeMsgs != null && wanGeMsgs.size() > 0){
            logger.debug("获取到万格返回行数：" + wanGeMsgs.size());
        }else{
            logger.debug("没有收到万格返回");
        }
        return wanGeMsgs;
    }

    @Transactional
    public void deleteCall(String callId){
        logger.debug("删除会话，callId：" + callId);
        WanGePipeExample deleteCriteria = new WanGePipeExample();
        deleteCriteria.createCriteria().andCallIdEqualTo(callId);
        pipeDao.deleteByExample(deleteCriteria);
    }
}
