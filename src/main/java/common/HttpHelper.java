package common;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpHelper {
    /**
     * @param url
     * @param Params
     * @return
     * @throws IOException
     */

    private static final Logger m_Logger = LoggerFactory.getLogger(HttpHelper.class);

    public static String sendPost(String url, List Params) {
        return sendPost(url, JSONArray.fromObject(Params).toString());
    }

    public static String sendPost(String url, Map Params) {
        return sendPost(url, JSONObject.fromObject(Params).toString());
    }

    public static String sendPost(String url, String Params) {
        return doRequest(url, "POST", Params);
    }

    public static String sendGet(String url, Map Params) {
        String param = "";
        for (Map.Entry m : (Set<Map.Entry>) Params.entrySet()) {
            param += m.getKey() + "=" + m.getValue() + "&";
        }
        param = param.substring(0, param.length() - 1);
        return sendGet(url, param);
    }

    private static String sendGet(String url, String Params) {
        return doRequest(url + "?" + Params, "GET", "");
    }

    private static String doRequest(String url, String method, String Params) {
        OutputStreamWriter out = null;
        BufferedReader reader = null;
        String response = "";

        int tryTime = 0;
        while (tryTime < 3) {
            try {
                URL httpUrl = null; // HTTP URL类 用这个类来创建连接
                // 创建URL
                httpUrl = new URL(url);
                // 建立连接
                HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
                conn.setRequestMethod(method);
                conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                conn.setRequestProperty("connection", "keep-alive");
                conn.setUseCaches(false);// 设置不要缓存
                conn.setInstanceFollowRedirects(true);
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.connect();
                if (method.equals("POST")) {
                    // POST请求
                    out = new OutputStreamWriter(conn.getOutputStream(), "utf-8");
                    out.write(Params);
                    out.flush();
                }
                // 读取响应

                reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                String lines;
                while ((lines = reader.readLine()) != null) {
                    response += lines;
                }

                m_Logger.info(method + " 返回值:" + response);
                reader.close();
                // 断开连接
                conn.disconnect();
                tryTime = 3;
            } catch (Exception e) {
                m_Logger.info("发送" + method + " 请求出现异常！" + e);
                e.printStackTrace();
                tryTime++;
            }
            // 使用finally块来关闭输出流、输入流
            finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        return response;
    }

    /**
     * 发送http delete请求
     */

    public static String sendDelete(String url, String Params) {
        DefaultHttpClient client = new DefaultHttpClient();
        HttpEntityEnclosingRequestBase method = new HttpEntityEnclosingRequestBase() {
            public static final String METHOD_NAME = "DELETE";

            public String getMethod() {
                return METHOD_NAME;
            }
        };
        method.setURI(URI.create(url));
        try {
            if (null != Params) {
                // 解决中文乱码问题
                StringEntity entity = new StringEntity(Params, "utf-8");
                entity.setContentEncoding("UTF-8");
                entity.setContentType("application/json");
                method.setEntity(entity);
            }
            HttpResponse result = client.execute(method);
            /** 请求发送成功，并得到响应 **/
            if (result.getStatusLine().getStatusCode() == 200) {
                String str = EntityUtils.toString(result.getEntity(), "utf-8");
                return str;
            }
        } catch (IOException e) {
            System.err.println("delete请求提交失败:" + url);
        }
        return "";
    }
}
