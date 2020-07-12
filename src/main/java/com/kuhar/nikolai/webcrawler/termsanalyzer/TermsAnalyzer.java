package com.kuhar.nikolai.webcrawler.termsanalyzer;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface TermsAnalyzer {
    Long analyzeMatchesCount(String source, String term);

    default Map<String, Long> analyzeMatchesCount(String source, List<String> terms) {
        return terms.stream().collect(Collectors.toMap(
                term -> term,
                term -> analyzeMatchesCount(source, term)));
    }
}
