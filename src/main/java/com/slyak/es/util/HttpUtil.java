package com.slyak.es.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.slyak.es.util.StringUtils.decodeUnicode;

public class HttpUtil {

    static Map<String, String> DEFAULT_HEADERS = new HashMap<>();

    static {
        DEFAULT_HEADERS.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        DEFAULT_HEADERS.put("Accept-Encoding", "gzip, deflate, br");
        DEFAULT_HEADERS.put("Accept-Language", "en-US,en;q=0.5");
        DEFAULT_HEADERS.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
    }


    /**
     * 发送 HTTP GET 请求
     *
     * @param url 请求地址
     * @return 返回响应结果
     */
    public static String doGet(String url) {
        return doGet(url, null);
    }

    /**
     * 发送 HTTP GET 请求
     *
     * @param url     请求地址
     * @param headers 请求头信息
     * @return 返回响应结果
     */
    @SneakyThrows
    public static String doGet(String url, Map<String, String> headers) {
        Connection connection = Jsoup.connect(url).method(Connection.Method.GET);
        addDefaultHeaders(connection);
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                connection.header(entry.getKey(), entry.getValue());
            }
        }
        Document document = connection.get();
        return document.text();
    }

    /**
     * 发送 HTTP POST 请求
     *
     * @param url  请求地址
     * @param data 请求参数
     * @return 返回响应结果
     */
    public static String doPost(String url, Map<String, Object> data) {
        return doPost(url, null, data);
    }

    /**
     * 发送 HTTP POST 请求
     *
     * @param url     请求地址
     * @param headers 请求头信息
     * @param data    请求参数
     * @return 返回响应结果
     */
    @SneakyThrows
    public static String doPost(String url, Map<String, String> headers, Map<String, Object> data) {
        Connection connection = Jsoup.connect(url).method(Connection.Method.POST);
        addDefaultHeaders(connection);
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                connection.header(entry.getKey(), entry.getValue());
            }
        }

        if (data != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(data);
            connection.requestBody(jsonString);
        }

        Document document = connection.post();
        return document.text();
    }

    /**
     * 添加默认请求头
     *
     * @param connection 请求连接
     */
    private static void addDefaultHeaders(Connection connection) {
        for (Map.Entry<String, String> entry : DEFAULT_HEADERS.entrySet()) {
            connection.header(entry.getKey(), entry.getValue());
        }
    }

    public static void main(String[] args) throws IOException {
        String s = HttpUtil.doGet("https://smartbox.gtimg.cn/s3/?v=2&q=hy&t=all&c=1", null);
        System.out.println(decodeUnicode(s));

        String unicodeString = "This is a string containing Unicode: \u4f60\u597d";
        System.out.println("Unicode string: " + unicodeString);

        String decodedString = decodeUnicode(unicodeString);
        System.out.println("Decoded string: " + decodedString);
    }
}

