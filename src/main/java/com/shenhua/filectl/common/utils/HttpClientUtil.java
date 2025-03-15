package com.shenhua.filectl.common.utils;


import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class HttpClientUtil {
    private static final Logger LOG = LoggerFactory.getLogger(HttpClientUtil.class);

    public static final String GET = "GET";
    public static final String POST = "POST";

    public static String sendHttpGet(String requestUrl, String param) {
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");
        header.put("Accept", "*/*");
        return httpRequestV2(requestUrl, GET, param, header);
    }

    public static String sendHttpPost(String requestUrl, String param) {
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");
        header.put("Accept", "*/*");
        return httpRequestV2(requestUrl, POST, param, header);
    }


    /**
     * HTTP
     * requestUrl ： 请求路径
     * requestMethod ： GET/POST请求方式，大写
     * param ：请求参数（可为空）
     * header：请求头部数据(可为空)
     */
    public static String httpRequestV2(String requestUrl, String requestMethod, String param, Map<String, String> header) {
        StringBuffer buffer = null;
        try {
            //创建SSLContext
            SSLContext sslContext = SSLContext.getInstance("SSL");
            TrustManager[] tm = {new MyX509TrustManager()};//用自定义认证
            //初始化
            sslContext.init(null, tm, new java.security.SecureRandom());
            //获取SSLSocketFactory对象
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();//打开连接
            conn.setDoOutput(true);//开启输出
            conn.setDoInput(true);//开启输入
            conn.setUseCaches(false);//开启缓存
            conn.setRequestMethod(requestMethod);//设置访问方式
            /*设置头部参数*/
            //设置请求头部信息
            for (Map.Entry<String, String> entry : header.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
            //设置当前实例使用的SSLSoctetFactory
//            conn.setSSLSocketFactory(ssf);
            conn.setConnectTimeout(60000); // 设置连接超时时间为60秒
            conn.setReadTimeout(60000); // 设置读取超时时间为60秒
            conn.connect();//连接
            //param请求参数转为json格式
            String outputStr = param;
            //往服务器端写入请求参数
            if (StringUtils.isNotEmpty(outputStr)) {
                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(outputStr.getBytes("utf-8"));
                outputStream.flush();//刷新
                outputStream.close();
            }
            //读取服务器端返回的内容
            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader br = new BufferedReader(inputStreamReader);
            buffer = new StringBuffer();
            String line = null;
            while ((line = br.readLine()) != null) {
                buffer.append(line);
            }
            inputStream.close();
            inputStreamReader.close();
            br.close();
            conn.disconnect();//关闭连接
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("httpRequestV2 for requestUrl {} happen error {}", requestUrl, e.toString());
        }
        return buffer == null ? null : buffer.toString();//返回响应数据
    }





    /**
     * HTTP
     * requestUrl ： 请求路径
     * requestMethod ： GET/POST请求方式，大写
     * param ：请求参数（可为空）
     * header：请求头部数据(可为空)
     */
    public static String httpRequest(String requestUrl, String requestMethod, Map<String, String> param, Map<String, String> header) {
        StringBuffer buffer = null;
        try {
            //创建SSLContext
            SSLContext sslContext = SSLContext.getInstance("SSL");
            TrustManager[] tm = {new MyX509TrustManager()};//用自定义认证
            //初始化
            sslContext.init(null, tm, new java.security.SecureRandom());
            //获取SSLSocketFactory对象
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();//打开连接
            conn.setDoOutput(true);//开启输出
            conn.setDoInput(true);//开启输入
            conn.setUseCaches(false);//开启缓存
            conn.setRequestMethod(requestMethod);//设置访问方式
            /*设置头部参数*/
            //设置请求头部信息
            for (Map.Entry<String, String> entry : header.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
            //设置当前实例使用的SSLSoctetFactory
//            conn.setSSLSocketFactory(ssf);
            conn.connect();//连接
            //param请求参数转为json格式
            String outputStr = null;
            if (param.size() > 0) {
                JSON json = (JSON) JSON.toJSON(param);
                outputStr = json.toJSONString();
            }
            //往服务器端写入请求参数
            if (null != outputStr) {
                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(outputStr.getBytes("utf-8"));
                outputStream.flush();//刷新
                outputStream.close();
            }
            //读取服务器端返回的内容
            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader br = new BufferedReader(inputStreamReader);
            buffer = new StringBuffer();
            String line = null;
            while ((line = br.readLine()) != null) {
                buffer.append(line);
            }
            inputStream.close();
            inputStreamReader.close();
            br.close();
            conn.disconnect();//关闭连接
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("httpRequest for requestUrl {} happen error {}", requestUrl, e.toString());
        }
        return buffer == null ? null : buffer.toString();
    }

    /**
     * HTTP 固定POST请求
     * requestUrl ： 请求路径
     * File ： 请求文件(可为空)
     * param ：请求参数（可为空）
     * header：请求头部数据(可为空)
     */
    public static String httpPostWithFile(String requestUrl, File file, Map<String, String> param, Map<String, String> header) {
        StringBuffer buffer = null;
        try {
            //创建SSLContext
            SSLContext sslContext = SSLContext.getInstance("SSL");
            TrustManager[] tm = {new MyX509TrustManager()};//用自定义认证
            //初始化
            sslContext.init(null, tm, new java.security.SecureRandom());
            ;
            //获取SSLSocketFactory对象
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();//打开连接
            conn.setDoOutput(true);//开启输出
            conn.setDoInput(true);//开启输入
            conn.setUseCaches(false);//开启缓存
            conn.setRequestMethod("POST");//设置访问方式
            /*设置头部参数*/
            //设置请求头部信息
            for (Map.Entry<String, String> entry : header.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
            //设置当前实例使用的SSLSoctetFactory
//            conn.setSSLSocketFactory(ssf);
            conn.connect();//连接
            //param请求参数转为json格式
            String outputStr = null;
            if (param.size() > 0) {
                JSON json = (JSON) JSON.toJSON(param);
                outputStr = json.toJSONString();
            }
            //往服务器端写入请求参数
            if (null != outputStr) {
                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(outputStr.getBytes("utf-8"));//param请求参数写入
                //file写入
                FileInputStream fileInputStream = new FileInputStream(file);
                byte[] data = new byte[2048];
                int len = 0;
                while ((len = fileInputStream.read(data)) != -1) {
                    outputStream.write(data, 0, len);
                }
                outputStream.flush();//刷新
                fileInputStream.close();
                outputStream.close();
            }
            //读取服务器端返回的内容
            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader br = new BufferedReader(inputStreamReader);
            buffer = new StringBuffer();
            String line = null;
            while ((line = br.readLine()) != null) {
                buffer.append(line);
            }
            inputStream.close();
            inputStreamReader.close();
            br.close();
            conn.disconnect();//关闭连接
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("httpPostWithFile for requestUrl {} happen error {}", requestUrl, e.toString());
        }
        return buffer == null ? null : buffer.toString();
    }
}

