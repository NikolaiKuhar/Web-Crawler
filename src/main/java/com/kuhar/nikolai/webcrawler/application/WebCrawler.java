package com.kuhar.nikolai.webcrawler.application;

import com.kuhar.nikolai.webcrawler.domain.CrawlResponse;

import java.util.List;

public interface WebCrawler {
    CrawlResponse crawlWebSite(String url, List<String> termsList);

    List<CrawlResponse> crawlWebSiteRecursively(String url, List<String> termsList, Integer linkDepth, Integer pagesLimit);
}
