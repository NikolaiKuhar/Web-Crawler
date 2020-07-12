package com.kuhar.nikolai.webcrawler.domain;

import java.util.Collections;
import java.util.Map;

public class CrawlResponse {
    private String url;
    private String pageContentHtml;
    private Map<String, Long> termsMatchCount;

    public CrawlResponse(String url, String pageContentHtml, Map<String, Long> termsMatchCount) {
        this.url = url;
        this.pageContentHtml = pageContentHtml;
        this.termsMatchCount = termsMatchCount;
    }

    public String getUrl() {
        return url;
    }

    public String getPageContentHtml() {
        return pageContentHtml;
    }

    public Map<String, Long> getTermsMatchCount() {
        return Collections.unmodifiableMap(termsMatchCount);
    }

    @Override
    public String toString() {
        return String.format("{\"crawlResponse\":{\"url\":\"%s\",\"termsMatchCount\":\"%s\"}}", url, termsMatchCount);
    }
}
