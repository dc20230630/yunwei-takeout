package com.yunwei.utils;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public final class HttpClientUtil {

    private HttpClientUtil() {
    }

    public static String doGet(String url, Map<String, String> params) throws IOException {
        URI uri;
        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            for (Map.Entry<String, String> entry : params.entrySet()) {
                uriBuilder.addParameter(entry.getKey(), entry.getValue());
            }
            uri = uriBuilder.build();
        } catch (URISyntaxException e) {
            throw new IOException("请求地址格式错误", e);
        }

        HttpGet httpGet = new HttpGet(uri);
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(httpGet)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode < 200 || statusCode >= 300) {
                throw new IOException("HTTP 请求失败，状态码：" + statusCode);
            }
            return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        }
    }
}
