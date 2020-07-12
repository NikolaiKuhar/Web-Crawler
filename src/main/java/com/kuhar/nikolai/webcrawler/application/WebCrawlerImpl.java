package com.kuhar.nikolai.webcrawler.application;

import com.kuhar.nikolai.webcrawler.domain.CrawlResponse;
import com.kuhar.nikolai.webcrawler.exception.WebPageResolverException;
import com.kuhar.nikolai.webcrawler.termsanalyzer.TermsAnalyzer;
import com.kuhar.nikolai.webcrawler.webpageresolver.WebPageResolver;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component("webCrawler")
public class WebCrawlerImpl implements WebCrawler {
    private Logger LOG = LoggerFactory.getLogger(WebCrawlerImpl.class);

    private WebPageResolver webPageResolver;

    private TermsAnalyzer termsAnalyzer;

    private ForkJoinPool forkJoinPool = new ForkJoinPool();

    @Autowired
    public void setWebPageResolver(WebPageResolver webPageResolver) {
        this.webPageResolver = webPageResolver;
    }

    @Autowired
    @Qualifier("HTMLBodyTermsAnalyzer")
    public void setTermsAnalyzer(TermsAnalyzer termsAnalyzer) {
        this.termsAnalyzer = termsAnalyzer;
    }

    @Override
    public CrawlResponse crawlWebSite(String url, List<String> termsList) {
        try {
            String pageContentHtml = webPageResolver.getPageContent(url);
            Map<String, Long> termsMatchCount = termsAnalyzer.analyzeMatchesCount(pageContentHtml, termsList);
            return new CrawlResponse(url, pageContentHtml, termsMatchCount);
        } catch (WebPageResolverException wpre) {
            LOG.error("error while getting page: `{}`, skipping page.", url, wpre);
            return new CrawlResponse(url, null, Collections.emptyMap());
        }
    }

    @Override
    public List<CrawlResponse> crawlWebSiteRecursively(String url, List<String> termsList, Integer linkDepth,
                                                       Integer pagesLimit) {

        LOG.info("submit url: `{}`, currentDepth : `{}`, pagesLeft : `{}`", url, 0, pagesLimit);
        AtomicInteger pagesToScanLeft = new AtomicInteger(pagesLimit);
        return forkJoinPool.invoke(new RecursiveWebSiteCrawTask(url, termsList,
                0, linkDepth, pagesToScanLeft));
    }

    private class RecursiveWebSiteCrawTask extends RecursiveTask<List<CrawlResponse>> {
        private String url;
        private List<String> termsList;
        private Integer currentDepth;
        private Integer maxLinkDepth;
        private AtomicInteger pagesToScanLeft;

        public RecursiveWebSiteCrawTask(String url, List<String> termsList,
                                        Integer currentDepth,
                                        Integer maxLinkDepth, AtomicInteger pagesToScanLeft) {
            this.url = url;
            this.termsList = termsList;
            this.currentDepth = currentDepth;
            this.maxLinkDepth = maxLinkDepth;
            this.pagesToScanLeft = pagesToScanLeft;
        }

        @Override
        protected List<CrawlResponse> compute() {
            CrawlResponse crawlResponse = crawlWebSite(url, termsList);

            if (currentDepth >= maxLinkDepth || pagesToScanLeft.get() <= 0) {
                return Collections.singletonList(crawlResponse);
            }

            //TODO focus on breadth rather than depth.
            List<String> nestedUrls = getAllLinks(crawlResponse.getPageContentHtml());

            List<RecursiveWebSiteCrawTask> tasks = new ArrayList<>();
            for (String nestedUrl : nestedUrls) {
                if (pagesToScanLeft.decrementAndGet() <= 0) {
                    break;
                }

                LOG.info("submit url: `{}`, currentDepth : `{}`, pagesLeft : `{}`", nestedUrl, currentDepth + 1, pagesToScanLeft);
                tasks.add(new RecursiveWebSiteCrawTask(nestedUrl, termsList,
                        currentDepth + 1,
                        maxLinkDepth, pagesToScanLeft));
            }

            return ForkJoinTask.invokeAll(tasks).stream()
                    .map(ForkJoinTask::join)
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
        }
    }

    private List<String> getAllLinks(String pageContentHtml) {
        if (StringUtils.isBlank(pageContentHtml)) {
            return Collections.emptyList();
        }
        List<String> resultLinks = new ArrayList<>();
        Elements linkTags = Jsoup.parse(pageContentHtml).body().getElementsByTag("a");
        for (Element linkTag : linkTags) {
            String href = linkTag.attr("abs:href");
            if(StringUtils.isBlank(href) || StringUtils.startsWith(href, "#")) {
                continue;
            }
            resultLinks.add(href);
        }
        return resultLinks;
    }
}
