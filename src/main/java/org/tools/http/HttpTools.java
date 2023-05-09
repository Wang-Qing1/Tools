package org.tools.http;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.cert.X509Certificate;
import java.util.*;

public class HttpTools {
    // 编码格式。发送编码格式统一用UTF-8
    private static final String ENCODING = "UTF-8";

    // 设置连接超时时间，单位毫秒。
    private static final int CONNECT_TIMEOUT = 6000;

    // 请求获取数据的超时时间(即响应时间)，单位毫秒。
    private static final int SOCKET_TIMEOUT = 6000;

    /**
     * 发送post请求
     *
     * @param url     请求地址
     * @param headers 请求头集合
     * @param params  请求参数集合
     * @param body    请求body
     * @return
     * @throws Exception
     */
    public static HttpClientResult doPost(String url, Map<String, String> headers, Map<String, String> params, String body)
            throws Exception {
        // 设置协议http和https对应的处理socket链接工厂的对象
        Registry<ConnectionSocketFactory> registry
                = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", trustHttpsCertificates())
                .build();
        PoolingHttpClientConnectionManager connManager = new
                PoolingHttpClientConnectionManager(registry);
        //配置了HttpClients,创建自定义的httpclient对象
        HttpClientBuilder builder = HttpClients.custom().setConnectionManager(connManager);
        CloseableHttpClient httpClient = builder.build();

        // 创建http对象
        HttpPost httpPost = new HttpPost(url);
        /**
         * setConnectTimeout：设置连接超时时间，单位毫秒。
         * setConnectionRequestTimeout：设置从connect Manager(连接池)获取Connection 超时时间，单位毫秒。这个属性是新加的属性，因为目前版本是可以共享连接池的。
         * setSocketTimeout：请求获取数据的超时时间(即响应时间)，单位毫秒。 如果访问一个接口，多少时间内无法返回数据，就直接放弃此次调用。
         */
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT)
                .setSocketTimeout(SOCKET_TIMEOUT).build();
        httpPost.setConfig(requestConfig);
        // 设置请求头
        /*
         * httpPost.setHeader("Cookie", "");
         * httpPost.setHeader("Connection","keep-alive");
         * httpPost.setHeader("Accept", "application/json");
         * httpPost.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
         * httpPost.setHeader("Accept-Encoding", "gzip, deflate, br");
         * httpPost.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36"
         * );
         */
        packageHeader(headers, httpPost);

        // 封装请求参数
        packageParam(params, httpPost);

        if(body != null) {
            httpPost.setEntity(new StringEntity(body, ENCODING));
        }

        // 创建httpResponse对象
        CloseableHttpResponse httpResponse = null;

        try {
            // 执行请求并获得响应结果
            return getHttpClientResult(httpResponse, httpClient, httpPost);
        } finally {
            // 释放资源
            release(httpResponse, httpClient);
        }
    }

    /**
     * 发送post请求
     *
     * @param url     请求地址
     * @param mapData x-www-form-urlencoded
     * @return
     * @throws Exception
     */
    public static HttpClientResult doPost(String url, Map<String, String> mapData) throws Exception {
        // 设置协议http和https对应的处理socket链接工厂的对象
        Registry<ConnectionSocketFactory> registry
                = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", trustHttpsCertificates())
                .build();
        PoolingHttpClientConnectionManager connManager = new
                PoolingHttpClientConnectionManager(registry);
        //配置了HttpClients,创建自定义的httpclient对象
        HttpClientBuilder builder = HttpClients.custom().setConnectionManager(connManager);
        CloseableHttpClient httpClient = builder.build();

        // 创建http对象
        HttpPost httpPost = new HttpPost(url);
        /**
         * setConnectTimeout：设置连接超时时间，单位毫秒。
         * setConnectionRequestTimeout：设置从connect Manager(连接池)获取Connection 超时时间，单位毫秒。这个属性是新加的属性，因为目前版本是可以共享连接池的。
         * setSocketTimeout：请求获取数据的超时时间(即响应时间)，单位毫秒。 如果访问一个接口，多少时间内无法返回数据，就直接放弃此次调用。
         */
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT)
                .setSocketTimeout(SOCKET_TIMEOUT).build();
        httpPost.setConfig(requestConfig);
        // 设置请求头
        /*
         * httpPost.setHeader("Cookie", "");
         * httpPost.setHeader("Connection","keep-alive");
         * httpPost.setHeader("Accept", "application/json");
         * httpPost.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
         * httpPost.setHeader("Accept-Encoding", "gzip, deflate, br");
         * httpPost.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36"
         * );
         */
        httpPost.addHeader("Content-type", "application/x-www-form-urlencoded; charset=utf-8");

        // 添加参数
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        if (mapData.size() != 0) {
            // 将mapdata中的key存在set集合中，通过迭代器取出所有的key，再获取每一个键对应的值
            Set keySet = mapData.keySet();
            Iterator it = keySet.iterator();
            while (it.hasNext()) {
                String k =  it.next().toString();// key
                String v = mapData.get(k);// value
                nameValuePairs.add(new BasicNameValuePair(k, v));
            }
        }
        httpPost.setEntity( new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));

        // 创建httpResponse对象
        CloseableHttpResponse httpResponse = null;

        try {
            // 执行请求并获得响应结果
            return getHttpClientResult(httpResponse, httpClient, httpPost);
        } finally {
            // 释放资源
            release(httpResponse, httpClient);
        }
    }

    /**
     * 发送get请求
     *
     * @param url     请求地址
     * @param headers 请求头集合
     * @param params  请求参数集合
     * @return
     * @throws Exception
     */
    public static HttpClientResult doGet(String url, Map<String, String> headers, Map<String, String> params)
            throws Exception {
        // 设置协议http和https对应的处理socket链接工厂的对象
        Registry<ConnectionSocketFactory> registry
                = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", trustHttpsCertificates())
                .build();
        PoolingHttpClientConnectionManager connManager = new
                PoolingHttpClientConnectionManager(registry);
        //配置了HttpClients,创建自定义的httpclient对象
        HttpClientBuilder builder = HttpClients.custom().setConnectionManager(connManager);
        CloseableHttpClient httpClient = builder.build();

        // 创建访问的地址
        URIBuilder uriBuilder = new URIBuilder(url);
        if (params != null) {
            Set<Map.Entry<String, String>> entrySet = params.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                uriBuilder.setParameter(entry.getKey(), entry.getValue());
            }
        }

        // 创建http对象
        HttpGet httpGet = new HttpGet(uriBuilder.build());
        /**
         * setConnectTimeout：设置连接超时时间，单位毫秒。 setConnectionRequestTimeout：设置从connect
         * Manager(连接池)获取Connection 超时时间，单位毫秒。这个属性是新加的属性，因为目前版本是可以共享连接池的。
         * setSocketTimeout：请求获取数据的超时时间(即响应时间)，单位毫秒。 如果访问一个接口，多少时间内无法返回数据，就直接放弃此次调用。
         */
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT)
                .setSocketTimeout(SOCKET_TIMEOUT).build();
        httpGet.setConfig(requestConfig);

        // 设置请求头
        packageHeader(headers, httpGet);

        // 创建httpResponse对象
        CloseableHttpResponse httpResponse = null;

        try {
            // 执行请求并获得响应结果
            return getHttpClientResult(httpResponse, httpClient, httpGet);
        } finally {
            // 释放资源
            release(httpResponse, httpClient);
        }
    }

    /**
     * Description: 封装请求头
     *
     * @param params
     * @param httpMethod
     */
    private static void packageHeader(Map<String, String> params, HttpRequestBase httpMethod) {
        // 封装请求头
        if (params != null) {
            Set<Map.Entry<String, String>> entrySet = params.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                // 设置到请求头到HttpRequestBase对象中
                httpMethod.setHeader(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Description: 封装请求参数
     *
     * @param params
     * @param httpMethod
     * @throws UnsupportedEncodingException
     */
    private static void packageParam(Map<String, String> params, HttpEntityEnclosingRequestBase httpMethod)
            throws UnsupportedEncodingException {
        // 封装请求参数
        if (params != null) {
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            Set<Map.Entry<String, String>> entrySet = params.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }

            // 设置到请求的http对象中
            httpMethod.setEntity(new UrlEncodedFormEntity(nvps, ENCODING));
        }
    }


    /**
     * Description: 获得响应结果
     *
     * @param httpResponse
     * @param httpClient
     * @param httpMethod
     * @return
     * @throws Exception
     */
    private static HttpClientResult getHttpClientResult(CloseableHttpResponse httpResponse,
                                                        CloseableHttpClient httpClient, HttpRequestBase httpMethod) throws Exception {
        // 执行请求
        httpResponse = httpClient.execute(httpMethod);

        // 获取返回结果
        if (httpResponse != null && httpResponse.getStatusLine() != null) {
            String content = "";
            if (httpResponse.getEntity() != null) {
                content = EntityUtils.toString(httpResponse.getEntity(), ENCODING);
            }
            return new HttpClientResult(httpResponse.getStatusLine().getStatusCode(), content);
        }
        return new HttpClientResult(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    /**
     * Description: 释放资源
     *
     * @param httpResponse
     * @param httpClient
     * @throws IOException
     */
    private static void release(CloseableHttpResponse httpResponse, CloseableHttpClient httpClient) throws IOException {
        // 释放资源
        if (httpResponse != null) {
            httpResponse.close();
        }
        if (httpClient != null) {
            httpClient.close();
        }
    }

    /**
     * 创建并返回SSLConnectionSocketFactory对象
     * @return
     * @throws Exception
     */
    private static SSLConnectionSocketFactory trustHttpsCertificates() throws Exception {
        SSLConnectionSocketFactory socketFactory = null;
        TrustManager[] trustAllCerts = new TrustManager[1];
        TrustManager tm = new myTM();
        trustAllCerts[0] = tm;
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, null);
            socketFactory = new SSLConnectionSocketFactory(sc, NoopHostnameVerifier.INSTANCE);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return socketFactory;
    }
    private static class myTM implements TrustManager, X509TrustManager {

        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public void checkServerTrusted(X509Certificate[] certs, String authType) {

        }

        public void checkClientTrusted(X509Certificate[] certs, String authType) {

        }
    }
}
