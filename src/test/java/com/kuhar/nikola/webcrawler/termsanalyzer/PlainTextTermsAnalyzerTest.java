package com.kuhar.nikola.webcrawler.termsanalyzer;

import com.kuhar.nikolai.webcrawler.termsanalyzer.PlainTextTermsAnalyzer;
import com.kuhar.nikolai.webcrawler.termsanalyzer.TermsAnalyzer;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;


public class PlainTextTermsAnalyzerTest {

    private TermsAnalyzer termsAnalyzer;

    @BeforeClass
    public void setUp() {
        termsAnalyzer = new PlainTextTermsAnalyzer();
    }

    @DataProvider(name = "occurrencesTestDataSet")
    private Object[][] occurrencesTestDataSet() {
        return new Object[][] {
                {"aa, bb, aa, cc", "aa", 2L},
                {"aa, bb, aa, cc", "test", 0L},
                {"aa, bb, aa, cc", "aa,", 2L},
                {"aa, bb, aa, cc", "cc", 1L},
                {"aa, bb, aa, cc", "aa, bb, aa, cc", 1L}
        };
    }

    @Test(dataProvider = "occurrencesTestDataSet")
    public void testTermAnalyzerShouldFindAllOccurrences(String source, String term, Long expectedOccurrencesCount) {
        Long actualMatchesCount = termsAnalyzer.analyzeMatchesCount(source, term);
        assertEquals(actualMatchesCount, expectedOccurrencesCount);
    }
}
