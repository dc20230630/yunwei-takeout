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

/**
 * 发送 HTTP 请求的工具类。
 *
 * 当前只封装微信登录需要的 GET 请求。
 */
public final class HttpClientUtil {
    /**
     * 工具类不需要创建对象。
     */
    private HttpClientUtil() {
    }


    /**
     * 发送带查询参数的 GET 请求。
     *
     * @param url 请求地址，例如微信 code2Session 地址
     * @param params 查询参数，例如 appid、secret、js_code
     * @return 第三方接口返回的 JSON 字符串
     * @throws IOException 请求失败或第三方接口返回非成功状态码时抛出
     */
    public static String doGet(String url,Map<String,String> params) throws IOException {
        URI uri;
        try{
            //URIBuilder自动处理查询参数的拼接和编码
            URIBuilder uriBuilder = new URIBuilder(url);
            for(Map.Entry<String,String> entry:params.entrySet()){
                uriBuilder.addParameter(entry.getKey(),entry.getValue());
            }
            uri = uriBuilder.build();
        }catch (URISyntaxException e){
            // 地址格式错误属于请求构建失败，交给调用方统一处理
            throw new IOException("请求地址格式错误", e);
        }
        HttpGet httpGet = new HttpGet(uri);
        // 请求结束后关闭客户端和响应对象，避免连接资源泄漏
        try(CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse response = httpClient.execute(httpGet)){
            int statusCode  = response.getStatusLine().getStatusCode();
            // 微信接口正常应返回 200，其他状态直接视为请求失败
            if(statusCode < 200 || statusCode > 300){
                throw new IOException("HTTP 请求失败，状态码：" + statusCode);
            }
            // 按 UTF-8 读取微信返回的 JSON 内容
            return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        }

    }
}
