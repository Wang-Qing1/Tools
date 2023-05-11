package org.tools.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.tools.common.MD5Tools;

import java.util.HashMap;
import java.util.Map;

public class HTTPDemo {
    private static final String JYJG_URL = "https://jyjg.gzdata.com.cn:8080/openapi/asset/receive/queryDataProviderAndLaw";
    private static final String APP_ID = "1001";
    private static final String KEY = "lkGs*eQ$4JgjrR2Px#NyYw%y";
    private static final String IV = "$Y0D9@8T";

    public static JSONArray getAllOrg() throws Exception {
        // 头部参数设定
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        // 内容参数设定
        JSONObject body = new JSONObject();
        // 公共参数
        JSONObject pubParam = new JSONObject();
        pubParam.put("appId", APP_ID);
        long timestamp = System.currentTimeMillis();
        pubParam.put("authCode", MD5Tools.encrypt(APP_ID+timestamp+KEY+IV));
        pubParam.put("timestamp", timestamp);
        body.put("pubParam", pubParam);
        HttpClientResult httpClientResult = HttpTools.doPost(JYJG_URL, headers, null, body.toJSONString());
        if (200 != httpClientResult.getCode()) {
            throw new Exception("HTTP请求访问失败！");
        }
        JSONObject resultObj = JSON.parseObject(httpClientResult.getContent());
        if (resultObj == null || resultObj.isEmpty()) {
            throw new Exception("HTTP请求无响应内容！");
        }
        if (resultObj.getString("code") == null || !resultObj.getString("code").equals("0")) {
            throw new Exception("请求失败！" + resultObj.getString("errmsg"));
        }
        JSONArray dataProviderAndLaw = resultObj.getJSONArray("dataProviderAndLaw");
        if (dataProviderAndLaw == null || dataProviderAndLaw.isEmpty()) {
            return new JSONArray();
        }
        System.out.println("JYJG认证组织总数：" + dataProviderAndLaw.size());
        return dataProviderAndLaw;
    }

    public static void main(String[] args) throws Exception {
        getAllOrg();
    }
}