package webcrawler;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.nodes.Element;

/**
 * Uses SpiderMan WebcCrawler to get term counts from a web site.
 *
 * @author jgarner1
 */
public final class TermsCounter extends SpiderMan {

    public static final String PRINT_FORMAT = "%s %d%n";

    private Map<String, Pattern> terms;
    private Map<String, Integer> resultSet;

    /**
     * Creates a new TermsCounter.
     *
     * @param baseUrl the base URL.
     */
    public TermsCounter(String baseUrl) {
        super(baseUrl);
        setTerms(new String[0]);

        resultSet = new TreeMap<>();

    }

    /**
     * Creates a new TermsCounter with terms.
     *
     * @param baseUrl the base URL
     * @param terms the terms
     */
    public TermsCounter(String baseUrl, String[] terms) {
        super(baseUrl);
        setTerms(terms);

        resultSet = new TreeMap<>();
    }

    /**
     * Gets the terms.
     *
     * @return the terms
     */
    public Set<String> getTerms() {
        return terms.keySet();
    }

    /**
     * Sets regular expression patterns for search terms.
     *
     * @param terms the search terms
     */
    public void setTerms(String[] terms) {
        this.terms = new HashMap<>();
        String boundry = "\\b";

        for (String term : terms) {
            StringBuilder sb = new StringBuilder();

            sb.append(boundry).append(term).append(boundry);

            this.terms.put(term, Pattern.compile(sb.toString(),
                                                 Pattern.CASE_INSENSITIVE));
        }

        setPredicate();
        setConsumer();
    }

    /**
     * Gets the current result set.
     *
     * @return the result set
     */
    public Map<String, Integer> getResultSet() {
        return resultSet;
    }

    /**
     * Search for the terms in the element text.
     */
    private void setPredicate() {
        super.setPredicate((Element element) -> {
            String text = element.ownText().toLowerCase();

            return getTerms().stream().anyMatch((term)
                -> (text.contains(term)));
        });
    }

    /**
     * Count the terms in the element text and add the count to the result set
     * using a key containing the URL and the term.
     */
    private void setConsumer() {
        super.setConsumer((String uri, Element element) -> {
            String text = element.ownText();

            getTerms().stream().forEach((term) -> {
                StringBuilder sb = new StringBuilder();
                Matcher matcher = terms.get(term).matcher(text);
                int count = 0;

                while (matcher.find()) {
                    count += 1;
                }

                sb.append('[').append(uri).append(']').append(' ').append(term);
                resultSet.put(sb.toString(),
                              count + (resultSet.containsKey(sb.toString())
                                       ? resultSet.get(sb.toString()) : 0));
            });
        });
    }

    @Override
    public void reset() {
        resultSet = new TreeMap<>();

        super.resetVisitedLinks();
    }

    @Override
    public void printResultSet() {
        getResultSet().keySet().stream().forEach((key)
            -> System.out.format(PRINT_FORMAT, key, getResultSet().get(key))
        );
    }
}
