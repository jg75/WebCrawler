package webcrawler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

/**
 *
 * @author jgarner1
 */
public class Application {
    public static void main(String... args) {
        ForkJoinPool pool = new ForkJoinPool(4);
        List<SpiderMan> taskList = new ArrayList<>();
        String[] urls = {
            "https://www.amberengine.com"
        };
        String[] terms = { 
            "product data", "furniture", "experts"
        };
        
        for (String url : urls) {
            taskList.add(new TermsCounter(url, terms));
        }
        
        taskList.forEach(pool::invoke);
        taskList.forEach(WebCrawler::printResultSet);
    }
}
