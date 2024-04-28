package com.tansci.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Objects;

/**
 * https://www.jb51.net/article/255134.htm
 */
@Configuration
public class EsConfig {


    private static final int ADDRESS_LENGTH = 2;

    @Value("${elasticsearch.scheme}")
    private String scheme;

    @Value("${elasticsearch.address}")
    private String address;

    @Value("${elasticsearch.userName}")
    private String userName;

    @Value("${elasticsearch.userPwd}")
    private String userPwd;

    @Value("${elasticsearch.socketTimeout:5000}")
    private Integer socketTimeout;

    @Value("${elasticsearch.connectTimeout:5000}")
    private Integer connectTimeout;

    @Value("${elasticsearch.connectionRequestTimeout:5000}")
    private Integer connectionRequestTimeout;

    /**
     * 初始化客户端
     * @return
     */
//    @Bean(name = "restHighLevelClient")
    @Bean(name = "client")
    public RestHighLevelClient restClientBuilder() {
        HttpHost[] hosts = Arrays.stream(address.split(","))
                .map(this::buildHttpHost)
                .filter(Objects::nonNull)
                .toArray(HttpHost[]::new);
        RestClientBuilder restClientBuilder = RestClient.builder(hosts);
        // 异步参数配置
        restClientBuilder.setHttpClientConfigCallback(httpClientBuilder -> {
            httpClientBuilder.setDefaultCredentialsProvider(buildCredentialsProvider());
            return httpClientBuilder;
        });

        // 异步连接延时配置
        restClientBuilder.setRequestConfigCallback(requestConfigBuilder -> {
            requestConfigBuilder.setConnectionRequestTimeout(connectionRequestTimeout);
            requestConfigBuilder.setSocketTimeout(socketTimeout);
            requestConfigBuilder.setConnectTimeout(connectTimeout);
            return requestConfigBuilder;
        });

        return new RestHighLevelClient(restClientBuilder);
    }


    /**
     * 根据配置创建HttpHost
     * @param s
     * @return
     */
    private HttpHost buildHttpHost(String s) {
        String[] address = s.split(":");
        if (address.length == ADDRESS_LENGTH) {
            String ip = address[0];
            int port = Integer.parseInt(address[1]);
            return new HttpHost(ip, port, scheme);
        } else {
            return null;
        }
    }

    /**
     * 构建认证服务
     * @return
     */
    private CredentialsProvider buildCredentialsProvider(){
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName,
                userPwd));
        return credentialsProvider;
    }

//    @Bean
//    public RestHighLevelClient restHighLevelClient() {
//        return new RestHighLevelClient(
//                RestClient.builder(
//                        //若有多个，可以传一个数组
//                        new HttpHost("101.43.34.244", 9200, "http")));
//
//    }

}
