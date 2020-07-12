package com.kuhar.nikolai.webcrawler.application;

import com.kuhar.nikolai.webcrawler.domain.CrawlResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class WebCrawlerApplication {

    private String seedUrl;

    private String terms;

    private Integer linkDepth;

    private Integer pagesLimit;

    private WebCrawler webCrawler;

    public void setSeedUrl(String seedUrl) {
        this.seedUrl = seedUrl;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }

    public void setLinkDepth(Integer linkDepth) {
        this.linkDepth = linkDepth;
    }

    public void setPagesLimit(Integer pagesLimit) {
        this.pagesLimit = pagesLimit;
    }

    @Autowired
    public void setWebCrawler(WebCrawler webCrawler) {
        this.webCrawler = webCrawler;
    }

    public void execute() {
        List<String> termsList = Arrays.asList(StringUtils.split(terms, ";"));
        List<CrawlResponse> termsMatchCount = webCrawler.crawlWebSiteRecursively(seedUrl, termsList, linkDepth, pagesLimit);
        System.out.println(termsMatchCount); //TODO add save to csv file
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("webcrawler-spring-context.xml");

        WebCrawlerApplication webCrawlerApplication = applicationContext.getBean(WebCrawlerApplication.class);
        webCrawlerApplication.execute();

        applicationContext.close();
    }
}
