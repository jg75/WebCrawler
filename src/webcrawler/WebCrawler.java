package webcrawler;

import java.util.function.BiConsumer;
import java.util.function.Predicate;
import org.jsoup.nodes.Element;

/**
 * A web crawler.
 * 
 * @author jgarner1
 */
public interface WebCrawler {
    
    public static final String A = "a";
    public static final String HREF = "href";
    public static final String ABS_HREF = "abs:href";
    public static final String LINK_REGEX = "^/[a-z].*";
    public static final String EXTERNAL_LINK_REGEX = "^http(s)?://.*";
    public static final String HTTP_SCHEMA = "http:";
    public static final String HTTPS_SCHEMA = "https:";
    public static final String TRAILING = "/$";
    public static final String EMPTY = "";
    
    /**
     * Normalizes the URL.
     *
     * @param url the URL
     * @param useHttps set to true if the URL should use HTTPS only
     * @return the normalized URL
     */
    public static String normalizeUrl(String url, boolean useHttps) {
        String normalizedUrl = url.replaceFirst(TRAILING, EMPTY);
        
        if (useHttps) {
            return normalizedUrl.replaceFirst(HTTP_SCHEMA, HTTPS_SCHEMA);
        }
        
        return normalizedUrl;
    }
    
    /**
     * Gets the base URL.
     * 
     * @return the base URL
     */
    public String getBaseUrl();

    /**
     * Sets the base URL.
     * 
     * @param baseUrl the base URL
     */
    public void setBaseUrl(String baseUrl);
    
    /**
     * Sets the predicate.
     * 
     * @param predicate the predicate
     */
    public void setPredicate(Predicate<Element> predicate);
    
    /**
     * Sets the consumer.
     * 
     * @param consumer the consumer
     */
    public void setConsumer(BiConsumer<String, Element> consumer);
    
    /**
     * Recursively crawl the web site at the provided URL, filter the results
     * using a {@code predicate interface}, and apply the
     * {@code consumer interface} to the results.
     */
    default public void crawl() {
        crawl(getBaseUrl(), true, 0);
    }
    
    /**
     * Recursively crawl the web site at the provided URL, filter the results
     * using a {@code predicate interface}, and apply the
     * {@code consumer interface} to the results.
     *
     * @param url the URL
     * @param follow true if the crawler should recursively follow links
     * @param depth the depth of external link recursion
     */
    public void crawl(String url, final boolean follow, final int depth);
    
    /**
     * Print the current result set.
     */
    public void printResultSet();
}
