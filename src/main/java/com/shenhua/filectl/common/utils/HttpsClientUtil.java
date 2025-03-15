package com.shenhua.filectl.common.utils;


import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;

import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

public class HttpsClientUtil {
    public static final String GET = "GET";
    public static final String POST = "POST";

    public static String sendHttpsGet(String requestUrl, String param) {
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");
        header.put("Accept", "*/*");
        return httpsRequestV2(requestUrl, GET, param, header);
    }

    public static String sendHttpsPost(String requestUrl, String param) {
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");
        header.put("Accept", "*/*");
        return httpsRequestV2(requestUrl, POST, param, header);
    }


    /**
     * HTTPS
     * requestUrl ： 请求路径
     * requestMethod ： GET/POST请求方式，大写
     * param ：请求参数（可为空）
     * header：请求头部数据(可为空)
     */
    public static String httpsRequestV2(String requestUrl, String requestMethod, String param, Map<String, String> header) {
        StringBuffer buffer = new StringBuffer();
        try {
            //创建SSLContext
            SSLContext sslContext = SSLContext.getInstance("SSL");
            TrustManager[] tm = {new MyX509TrustManager()};//用自定义认证
            //初始化
            sslContext.init(null, tm, new java.security.SecureRandom());
            //获取SSLSocketFactory对象
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            URL url = new URL(requestUrl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();//打开连接
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
            conn.setSSLSocketFactory(ssf);
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
            return e.toString();
        }
        return buffer.toString();//返回响应数据
    }





    /**
     * HTTPS
     * requestUrl ： 请求路径
     * requestMethod ： GET/POST请求方式，大写
     * param ：请求参数（可为空）
     * header：请求头部数据(可为空)
     */
    public static String httpsRequest(String requestUrl, String requestMethod, Map<String, String> param, Map<String, String> header) {
        StringBuffer buffer = new StringBuffer();
        try {
            //创建SSLContext
            SSLContext sslContext = SSLContext.getInstance("SSL");
            TrustManager[] tm = {new MyX509TrustManager()};//用自定义认证
            //初始化
            sslContext.init(null, tm, new java.security.SecureRandom());
            //获取SSLSocketFactory对象
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            URL url = new URL(requestUrl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();//打开连接
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
            conn.setSSLSocketFactory(ssf);
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
        }
        return buffer.toString();//返回响应数据
    }

    /**
     * HTTPS 固定POST请求
     * requestUrl ： 请求路径
     * File ： 请求文件(可为空)
     * param ：请求参数（可为空）
     * header：请求头部数据(可为空)
     */
    public static String httpsPostWithFile(String requestUrl, File file, Map<String, String> param, Map<String, String> header) {
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
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();//打开连接
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
            conn.setSSLSocketFactory(ssf);
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
        }
        return buffer.toString();//返回响应数据
    }

    /**
     * https://www.cnblogs.com/interdrp/p/17068518.html
     * @param fileUrl
     * @return
     */
    public static String getBase64FromUrl(String fileUrl) throws IOException {
        InputStream inputStream = null;
        byte[] data = null;
        ByteArrayOutputStream swapStream = null;
        HttpsURLConnection conn = null;
        try {
            URL url = new URL(fileUrl);
            //判断当前文件url是否是https
            if (fileUrl.contains("https:")){
                //是https
                //绕过证书
                SSLContext context = createIgnoreVerifySSL();
                createIgnoreVerifySSL();
                conn = (HttpsURLConnection) url.openConnection();
                conn.setSSLSocketFactory(context.getSocketFactory());
                inputStream = conn.getInputStream();
            } else {
                //当前链接是http
                inputStream =  url.openConnection().getInputStream();
            }

            swapStream = new ByteArrayOutputStream();
            byte[] buff = new byte[100];
            int rc = 0;
            while ((rc = inputStream.read(buff, 0, 100)) > 0) {
                swapStream.write(buff, 0, rc);
            }
            data = swapStream.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            inputStream.close();
            swapStream.close();
        }
        return new String(Base64.encodeBase64(data));
    }

    //绕过SSL、TLS证书
    public static SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sc = SSLContext.getInstance("TLS");
        // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        sc.init(null, new TrustManager[]{trustManager}, null);
        return sc;
    }


}

