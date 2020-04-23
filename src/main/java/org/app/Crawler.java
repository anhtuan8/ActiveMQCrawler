package org.app;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Crawler crawl links being attached on the Web page by the URL given
 */

public class Crawler {
    private static final int MAX_LINKS=50;

    private HashSet<String> links;

    public Crawler(){
        links = new HashSet<String>();
    }

    public HashMap<String,String> crawlPageLinks(String URL){
        HashMap<String,String> pageLinks = new HashMap<>();
        if(!links.contains(URL)){
            try {
                links.add(URL);
                System.out.println("Crawling from " + URL);

                //Get HTML by jsoup
                Document doc = Jsoup.connect(URL).get();
                //Extract anchor href to get page links
                Elements pageLinksEls = doc.select("a[href]");
                int counter = 0;
                for(Element link: pageLinksEls){
                    if(counter >= MAX_LINKS) break;
                    String linkValue = link.attr("abs:href");
                    String linkName = link.text();
                    pageLinks.put(linkName,linkValue);
                    counter++;
                }
                return pageLinks;
            }
            catch (Exception e) {
                System.out.println("Invalid URL.");
//                e.printStackTrace();
                return pageLinks;
            }
//            } catch (IOException e) {
//                e.printStackTrace();
//                return pageLinks;
//            }
        }
        else{
            System.out.println("URL had been crawled.");
        }
        return pageLinks;
    }
}
