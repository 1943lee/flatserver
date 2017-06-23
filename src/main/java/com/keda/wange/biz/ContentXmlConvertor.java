package com.keda.wange.biz;

import com.thoughtworks.xstream.XStream;

/**
 * Created by liulun on 2016/12/8.
 */
public class ContentXmlConvertor {
    public static String obj2xml(ContentXml obj){
        XStream xs = new XStream();
        xs.autodetectAnnotations(true);
        String s = xs.toXML(obj);
        return s;
    }

    public static ContentXml xml2obj(String s){
        XStream xs = new XStream();
        xs.autodetectAnnotations(true);
        xs.alias("mpgw", ContentXml.class);
        ContentXml contentXml = (ContentXml) xs.fromXML(s);
        return contentXml;
    }
}
