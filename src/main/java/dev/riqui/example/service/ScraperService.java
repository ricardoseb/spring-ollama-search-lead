package dev.riqui.example.service;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class ScraperService {

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";
    private static final Pattern EMAIL_PATTERN = Pattern.compile("\\b[A-Za-z0-9._%+-]+@gmail\\.com\\b");
    private static final long DELAY_BETWEEN_REQUESTS = 1000; // 1 second

    public List<Map<String, String>> scrapeGoogle(String profession, String country, List<String> platforms) {
        List<Map<String, String>> results = new ArrayList<>();
        for (String platform : platforms) {
            String query = String.format("\"%s\" \"%s\" \"@gmail.com\" site:%s.com", profession, country, platform);
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String baseUrl = "https://www.google.com/search?q=" + encodedQuery;
            String url = baseUrl;

            int currentPage = 1;
            int maxPages = 1;

            while (url != null) {
                try {
                    Document doc = Jsoup.connect(url)
                            .userAgent(USER_AGENT)
                            .timeout(10000)
                            .get();

                    results.addAll(extractEmailsAndNames(doc, platform));

                    if (maxPages == 1) {
                        maxPages = getMaxPages(doc);
                        log.info("Max pages to scrape: {}", maxPages);
                    }

                    if (currentPage < maxPages) {
                        url = getNextPageUrl(doc, baseUrl);
                        currentPage++;
                        log.info("Moving to page {}, URL: {}", currentPage, url);
                        Thread.sleep(DELAY_BETWEEN_REQUESTS);
                    } else {
                        log.info("Reached last page, stopping pagination.");
                        url = null;
                    }
                } catch (IOException e) {
                    log.error("Error fetching page: {}", e.getMessage());
                    break;
                } catch (InterruptedException e) {
                    log.error("Thread interrupted: {}", e.getMessage());
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        log.info("Scraped {} unique results", results.size());
        return results;
    }

    private List<Map<String, String>> extractEmailsAndNames(Document doc, String platform) {
        List<Map<String, String>> results = new ArrayList<>();
        Elements resultDivs = doc.select("div.g");
        for (Element div : resultDivs) {
            String text = div.text();
            Matcher matcher = EMAIL_PATTERN.matcher(text);
            while (matcher.find()) {
                String email = matcher.group();
                if (isValidEmail(email)) {
                    Element titleElement = div.selectFirst("h3.LC20lb");
                    String name = titleElement != null ? titleElement.text() : "Unknown";
                    if (platform.equals("linkedin") && name.contains(" - ")) {
                        name = name.substring(0, name.indexOf(" - "));
                    }
                    Map<String, String> result = new HashMap<>();
                    result.put("email", email);
                    result.put("name", name);
                    results.add(result);
                }
            }
        }
        return results;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@gmail\\.com$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    private int getMaxPages(Document doc) {
        Elements pageLinks = doc.select("a[aria-label^=Page]");
        int maxPage = 1;
        for (Element link : pageLinks) {
            String label = link.attr("aria-label");
            if (label.startsWith("Page ")) {
                int pageNum = Integer.parseInt(label.substring(5));
                if (pageNum > maxPage) {
                    maxPage = pageNum;
                }
            }
        }
        return maxPage;
    }

    private String getNextPageUrl(Document doc, String baseUrl) {
        Element nextButton = doc.selectFirst("a#pnnext");
        if (nextButton != null) {
            String nextPageUrl = nextButton.attr("href");
            return "https://www.google.com" + nextPageUrl;
        }
        return null;
    }
}