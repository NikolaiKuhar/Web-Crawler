package com.kuhar.nikolai.webcrawler.webpageresolver;

import com.kuhar.nikolai.webcrawler.exception.WebPageResolverException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ApacheHttpComponentsWebPageResolver implements WebPageResolver {
    private Logger LOG = LoggerFactory.getLogger(ApacheHttpComponentsWebPageResolver.class);

    private HttpClient httpClient;

    public ApacheHttpComponentsWebPageResolver() {
        int timeout = 5;
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(timeout * 1000)
                .setConnectionRequestTimeout(timeout * 1000)
                .setSocketTimeout(timeout * 1000).build();
        this.httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
    }

    @Override
    public String getPageContent(String uri) {
        try {

            HttpGet getPageRequest = new HttpGet(uri);
            HttpResponse response = httpClient.execute(getPageRequest);
            return EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            throw new WebPageResolverException(e);
        }

    }
}