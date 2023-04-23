package com.trader.api.utils;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import javax.net.ssl.SSLContext;

@Component
public class RestTemplateConf {

    public RestTemplate getRestTemplateApi(String baseUrl) throws Exception{
        DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory(baseUrl);
        RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(getInsecureRequestFactory()));
        restTemplate.setUriTemplateHandler(uriBuilderFactory);
        return  restTemplate;
    }

    private HttpComponentsClientHttpRequestFactory getInsecureRequestFactory() throws Exception{
        TrustStrategy acceptingTrustStrategy = (x509Certificates, s) -> true;
        SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);

        return requestFactory;
    }
}
