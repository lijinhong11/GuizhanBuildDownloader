package me.mmmjjkx.gbd.http;

import me.mmmjjkx.gbd.Main;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.net.URIBuilder;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

public class APIGetter {
    private static final CloseableHttpClient client;
    public static final String uri;

    static {
        client = HttpClients.createDefault();
        uri = "https://builds.guizhanss.com/api/";
    }

    public static String get(String endpoint, NameValuePair... args) {
        try {
            URIBuilder builder = new URIBuilder(uri + endpoint);
            builder.addParameters(Arrays.asList(args));
            HttpGet get = new HttpGet(builder.build());
            CloseableHttpResponse response = client.execute(get);
            int status = response.getCode();
            Main.LOGGER.warn("返回代码：{}", status);

            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity, "utf-8");
            Main.LOGGER.info("返回结果：{}", content);

            return content;
        } catch (IOException | ParseException | URISyntaxException e) {
            Main.LOGGER.error("获取失败");
            return "";
        }
    }

    public static String getFileName(String url) {
        if (url == null) {
            return "";
        }

        try {
            URL urlObj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
            connection.setInstanceFollowRedirects(true);
            connection.setConnectTimeout(5000);

            return connection.getHeaderField("Content-Disposition").split("filename=")[1].replace("\"", "");
        } catch (Exception e) {
            return "";
        }
    }
}
