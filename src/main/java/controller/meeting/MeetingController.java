package controller.meeting;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.keda.wange.model.WanGeGroupNo;
import com.keda.wange.service.Meeting;
import controller.Application;
import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Conditional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * Created by liulun on 2016/12/8.
 */
@RestController
@ComponentScan(basePackages = "com.keda.wange.biz")
@Conditional(Application.WanGeServerEnabled.class)
public class MeetingController {

    private Logger logger = LoggerFactory.getLogger(MeetingController.class);

    private final String Const350MDeviceType = "8";

    private final String ConstIsChairman = "1";
    private final String ConstNotChairman = "0";

    @Autowired
    private Meeting meeting;

    @Value("${vcs.server.url}/meeting/startMonitorMeeting")
    private String vcsCreateMeetingUrl;

    @Value("${vcs.server.url}/meeting/endMeeting")
    private String vcsEndMeetingUrl;

    @Value("${vcs.meeting.monitoringInterval}")
    private long vcsMeetingMonitoringInterval;

    @RequestMapping(value="/meeting/startMonitorMeeting", method= RequestMethod.POST)
    public @ResponseBody CreateMeetingOutput createMeeting(@RequestBody CreateMeetingInput input) {

        CreateMeetingOutput result = new CreateMeetingOutput();

        List<Device> _350MDevices = new ArrayList<>();
        List<Device> not350Devices = new ArrayList<>();
        for(int i = 0;i < input.getParticipant().size();i ++){
            Device device = input.getParticipant().get(i);
            if(is350MDevice(device)){
                _350MDevices.add(device);
            }else{
                not350Devices.add(device);
            }
        }

        boolean wangeInvolved = _350MDevices.size() > 1;
        String from = null;
        String regroupNo = null;
        String wangeGroupNo = null;
        if(wangeInvolved){
            // 先到万格组会再开会
            logger.debug("多于一个350M设备，需要到万格组会");
            List<String> deviceIds = new ArrayList<>();
            for(int i = 0;i < _350MDevices.size();i ++){
                Device device = _350MDevices.get(i);
                deviceIds.add(device.getDeviceID());
            }

            from = StringUtils.arrayToCommaDelimitedString(deviceIds.toArray());

            try {
                wangeGroupNo = meeting.getAvailableGroupNo();
                if(wangeGroupNo != null){
                    regroupNo = meeting.regroup(from, wangeGroupNo);
                }
            } catch (TimeoutException e) {
                logger.debug("万格组会超时");
                result.setSuccess(false);
                result.setMsg("万格组会超时");
                result.setData("万格组会超时");
            }
            if(regroupNo == null){
                logger.debug("万格组会失败");
                result.setSuccess(false);
                result.setMsg("万格组会失败");
                result.setData("万格组会失败");
            }else{
                logger.debug("万格组会成功，组号：" + regroupNo);
                List<Device> newDevices = new ArrayList<>();
                newDevices.addAll(not350Devices);
                Device wanGeGroupDevice = new Device();
                wanGeGroupDevice.setDeviceID(regroupNo);
                boolean groupIsChairman = false;
                for(int i = 0;i < _350MDevices.size();i ++){
                    if(ConstIsChairman.equals(_350MDevices.get(i).getIsChairman())){
                        groupIsChairman = true;
                        break;
                    }
                }
                wanGeGroupDevice.setIsChairman(groupIsChairman ? ConstIsChairman : ConstNotChairman);
                wanGeGroupDevice.setCallMode(_350MDevices.get(0).getCallMode());
                wanGeGroupDevice.setDeviceType(_350MDevices.get(0).getDeviceType());
                newDevices.add(wanGeGroupDevice);
                CreateMeetingInput param = new CreateMeetingInput();
                param.setMeetingName(input.getMeetingName());
                param.setUserName(input.getUserName());
                param.setParticipant(newDevices);
                result = startMeeting(param);
                if(! result.isSuccess()){
                    logger.debug("万格组会成功，但是夜眼组会失败，释放组号资源");
                    meeting.releaseGroupNo(from, wangeGroupNo);
                }
            }

        }else{
            logger.debug("直接开会");
            result = startMeeting(input);
        }

        if(result.isSuccess() && wangeInvolved){
            logger.debug("和万格组会成功，记录组号和会议号对应关系");
            WanGeGroupNo wanGeGroupUsage = new WanGeGroupNo();
            wanGeGroupUsage.setWangeGroupNo(wangeGroupNo);
            String cvsMeetingId = result.getData();
            wanGeGroupUsage.setCvsMeetingId(cvsMeetingId);
            wanGeGroupUsage.setWangeFromDevices(from);
            meeting.recordGroupUsage(wanGeGroupUsage);

            logger.debug("万格组会成功，夜眼也组会成功，开始周期性检查会议状态，及时释放组号资源");
            monitorMeetingStatus(cvsMeetingId);
        }

        return result;
    }

    private void monitorMeetingStatus(final String cvsMeetingId) {

        Thread monitoringThread = new Thread(){
            public void run(){
                try{
                    logger.debug("[监控会议状态线程] 开始：" + cvsMeetingId);
                    do{
                        if(! meeting.isCvsMeetingAlive(cvsMeetingId)){
                            logger.debug("[监控会议状态线程] 会议已结束：" + cvsMeetingId);
                            meeting.releaseWanGeGroupIfWanGeInvolved(cvsMeetingId);
                            break;
                        }else{
                            logger.debug("[监控会议状态线程] 会议还在进行：" + cvsMeetingId);
                            Thread.sleep(vcsMeetingMonitoringInterval);
                        }
                    }while(true);
                }catch (Exception e){
                    logger.debug("[监控会议状态线程] 遇到异常！！！：" + cvsMeetingId);
                    logger.error(e.toString());
                }
            }
        };
        monitoringThread.start();
    }

    @RequestMapping(value="/meeting/endMeeting", method = RequestMethod.POST)
    public @ResponseBody JsonNode endMeeting(@RequestBody JsonNode input){
        ObjectNode r = JsonNodeFactory.instance.objectNode();
        ObjectMapper om = new ObjectMapper();

        HttpPost httpPost = new HttpPost(vcsEndMeetingUrl);
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        String preCaseId = input.get("preCaseId").asText();

        meeting.releaseWanGeGroupIfWanGeInvolved(preCaseId);

        formparams.add(new BasicNameValuePair("preCaseId", preCaseId));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
        httpPost.setEntity(entity);

        CloseableHttpClient httpclient = HttpClients.createDefault();

        try {
            logger.debug("发起结会请求");
            logger.debug("地址：" + vcsEndMeetingUrl);
            logger.debug("参数：" + formparams.toString());
            CloseableHttpResponse response = httpclient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if(statusCode == 200){
                String result = EntityUtils.toString(response.getEntity());
                logger.debug("夜眼返回：" + result);
                r = (ObjectNode) om.readTree(result);
            }else{
                logger.debug("结会失败，夜眼返回状态：" + statusCode);
                r.put("success", false);
                r.put("msg", String.valueOf(statusCode));
            }
        } catch (IOException e) {
            r.put("success", false);
            r.put("msg", e.getMessage());
            logger.error(e.toString());
        }
        return r;
    }

    private CreateMeetingOutput startMeeting(CreateMeetingInput input) {
        CreateMeetingOutput r = new CreateMeetingOutput();
        ObjectMapper om = new ObjectMapper();
        StringWriter sw = new StringWriter();
        try {
            om.writeValue(sw, input);
        } catch (IOException e) {
            logger.error(e.toString());
        }
        HttpPost httpPost = new HttpPost(vcsCreateMeetingUrl);
        String requestPayload = sw.toString();
        httpPost.setEntity(new StringEntity(requestPayload, ContentType.APPLICATION_JSON));
        CloseableHttpClient httpclient = HttpClients.createDefault();

        try {
            logger.debug("发起开会请求");

            logger.debug("地址：" + vcsCreateMeetingUrl);
            logger.debug("参数：" + requestPayload);
            CloseableHttpResponse response = httpclient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if(statusCode == 200){
                String result = EntityUtils.toString(response.getEntity());
                logger.debug("夜眼返回：" + result);
                JsonNode jsonNode = om.readTree(result);
                r.setSuccess(jsonNode.get("success").asBoolean());
                r.setMsg(jsonNode.get("msg").asText());
                r.setData(jsonNode.get("data").asText());
            }else{
                logger.debug("开会失败，夜眼返回状态：" + statusCode);
                r.setSuccess(false);
                r.setMsg("VCSError");
                r.setData("VCS returned error: " + statusCode);
            }
        } catch (IOException e) {
            r.setSuccess(false);
            r.setMsg("Meeting middleware InternalError");
            r.setData(e.getMessage());
            logger.error(e.toString());
        }
        return r;
    }

    private boolean is350MDevice(Device device) {
        String deviceType = device.getDeviceType();
        if(deviceType == null){
            return false;
        }
        String[] types = deviceType.split(",");
        if(types.length < 2){
            return false;
        }
        boolean is350MDevice = Const350MDeviceType.equals(types[1]);
        return is350MDevice;
    }

    @RequestMapping(value="/meeting/startMonitorMeetingMock", method= RequestMethod.POST)
    public @ResponseBody CreateMeetingOutput createMeetingVCSMock(@RequestBody CreateMeetingInput input) throws IOException {
        logger.debug("createMeetingVCSMock invoked");
        ObjectMapper om = new ObjectMapper();
        StringWriter sw = new StringWriter();
        try {
            om.writeValue(sw, input);
            logger.debug("Request content: " + sw.toString());
        } catch (IOException e) {
            logger.error(e.toString());
        }
        CreateMeetingOutput r = new CreateMeetingOutput();
        r.setSuccess(true);
        r.setData("f5b08d3f418243faba2fcd93a938f701");
        return r;
    }

    @RequestMapping(value="/meeting/endMeetingMock", method= RequestMethod.POST)
    public @ResponseBody CreateMeetingOutput endMeetingVCSMock() throws IOException {
        logger.debug("endMeetingVCSMock invoked");

        CreateMeetingOutput r = new CreateMeetingOutput();
        r.setSuccess(true);
        r.setData("");
        return r;
    }

    @RequestMapping(value="/meeting/getMock", method= RequestMethod.POST)
    public @ResponseBody CreateMeetingOutput getMeetingVCSMock() throws IOException {
        logger.debug("createMeetingVCSMock invoked");

        CreateMeetingOutput r = new CreateMeetingOutput();
        r.setSuccess(true);
        r.setData("Mock response");
        return r;
    }
}
