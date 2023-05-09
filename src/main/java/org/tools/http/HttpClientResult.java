package org.tools.http;

import java.io.Serializable;

/**
 * Http Client 响应对象
 */
public class HttpClientResult implements Serializable {
    public HttpClientResult(int code, String content) {
        this.code = code;
        this.content = content;
    }

    public HttpClientResult(int code) {
        this.code = code;
    }

    /**
     * 响应状态码
     */
    private int code;

    /**
     * 响应数据
     */
    private String content;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

