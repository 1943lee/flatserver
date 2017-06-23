package com.keda.wange.biz;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by liulun on 2016/12/8.
 */
@XStreamAlias("mpgw")
public class ContentXml {

    @XStreamAlias("header")
    private Header header = new Header();

    @XStreamAlias("body")
    private Body body = new Body();

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }
}

@XStreamAlias("header")
class Header {
    @XStreamAlias("cmd")
    private String cmd;

    @XStreamAlias("from")
    private String from;

    @XStreamAlias("to")
    private String to;

    @XStreamAlias("call-ID")
    private String callId;

    @XStreamAlias("data-Type")
    private String dataType;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}

@XStreamAlias("body")
class Body {
    @XStreamAlias("call-type")
    private String callType;

    @XStreamAlias("call-parameter")
    private String callParameter;

    @XStreamAlias("response")
    private String response;

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getCallParameter() {
        return callParameter;
    }

    public void setCallParameter(String callParameter) {
        this.callParameter = callParameter;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}

