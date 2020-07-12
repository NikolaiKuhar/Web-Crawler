package com.kuhar.nikolai.webcrawler.termsanalyzer;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class HTMLBodyTermsAnalyzer implements TermsAnalyzer {
    private static final Logger LOG = LoggerFactory.getLogger(HTMLBodyTermsAnalyzer.class);
    private TermsAnalyzer plainTextTermsAnalyzer;

    @Autowired
    @Qualifier("plainTextTermsAnalyzer")
    public void setPlainTextTermsAnalyzer(TermsAnalyzer plainTextTermsAnalyzer) {
        this.plainTextTermsAnalyzer = plainTextTermsAnalyzer;
    }

    @Override
    public Long analyzeMatchesCount(String source, String term) {
        if (StringUtils.isAnyBlank(source, term)) {
            return 0L;
        }
        return getBody(source)
                .map(bodyElement -> plainTextTermsAnalyzer.analyzeMatchesCount(bodyElement.text(), term))
                .orElse(0L);
    }

    private Optional<Element> getBody(String htmlString) {
        Element body = null;
        try {
            body = Jsoup.parse(htmlString).body();
        } catch (Exception e) {
            LOG.error("exception while parsing html body. ", e);
        }
        return Optional.ofNullable(body);
    }
}
