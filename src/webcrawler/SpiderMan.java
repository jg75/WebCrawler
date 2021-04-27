package webcrawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveAction;
import java.util.function.Predicate;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * <p>
 * SpiderMan, SpiderMan does whatever a spider can,<br>
 * spins a web any size, catches thieves just like flies.<br>
 * Look out! Here comes the SpiderMan!<br>
 * </p>
 *
 * @author jgarner1
 */
public abstract class SpiderMan extends RecursiveAction implements WebCrawler {

    protected static final Logger LOGGER = Logger.getLogger(SpiderMan.class.getName());

    private String baseUrl;
    private List<String> visitedLinks;
    private List<String> deadLinks;
    private Predicate<Element> predicate;
    private BiConsumer<String, Element> consumer;

    /**
     * Constructs a new SpiderMan WebCrawler.
     * 
     * @param baseUrl the base URL
     */
    protected SpiderMan(String baseUrl) {
        this.baseUrl = baseUrl;
        
        resetVisitedLinks();
        resetDeadLinks();
    }

    @Override
    public String getBaseUrl() {
        return baseUrl;
    }

    @Override
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * Gets the list of visited links.
     *
     * @return the list of visited links
     */
    public List<String> getVisitedLinks() {
        return visitedLinks;
    }

    /**
     * Sets the list of visited links.
     *
     * @param visitedLinks the list of visited links
     */
    public void setVisitedLinks(List<String> visitedLinks) {
        this.visitedLinks = visitedLinks;
    }

    /**
     * Resets the list of visited links to an empty list.
     */
    public final void resetVisitedLinks() {
        visitedLinks = new ArrayList<>();
    }

    /**
     * Gets the list of dead links.
     *
     * @return the list of dead links
     */
    public List<String> getDeadLinks() {
        return deadLinks;
    }

    /**
     * Sets the list of dead links that won't be followed.
     *
     * @param deadLinks the list of dead links
     */
    public void setDeadLinks(List<String> deadLinks) {
        this.deadLinks = deadLinks;
    }

    /**
     * Resets the list of dead links that won't be followed to an empty list.
     */
    public final void resetDeadLinks() {
        deadLinks = new ArrayList<>();
    }
    
    /**
     * Resets both the lists of visited links and dead links to empty lists.
     */
    public void reset() {
        resetVisitedLinks();
        resetDeadLinks();
    }

    @Override
    public void setPredicate(Predicate<Element> predicate) {
        this.predicate = predicate;
    }

    @Override
    public void setConsumer(BiConsumer<String, Element> consumer) {
        this.consumer = consumer;
    }
    
    @Override
    public void crawl(String url, final boolean follow, final int depth) {
        Document document;

        try {
            document = Jsoup.connect(url).get();

            visitedLinks.add(url);
            LOGGER.log(Level.FINE, url);
        } catch (IOException ex) {
            deadLinks.add(url);
            LOGGER.log(Level.SEVERE, url, ex);
            return;
        }

        document.body().getAllElements().forEach((element) -> {
            String tag = element.tagName();
            
            if (follow && tag.equals(A) && element.hasAttr(HREF)) {
                String href = element.attr(HREF);
                String link = WebCrawler
                        .normalizeUrl(element.attr(ABS_HREF), true);

                if (!deadLinks.contains(link) && !visitedLinks.contains(link)) {
                    if (href.matches(LINK_REGEX)) {
                        crawl(link, follow, depth);
                    } else if (depth > 0 && href.matches(EXTERNAL_LINK_REGEX)) {
                        crawl(link, depth > 1, depth - 1);
                    }
                }
            } else if (predicate.test(element)) {
                consumer.accept(url, element);
            }
        });
    }
        
    @Override
    protected void compute() {
        crawl();
    }
}
