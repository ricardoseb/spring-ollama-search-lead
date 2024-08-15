package dev.riqui.example.controller;

import dev.riqui.example.service.EmailAIService;
import dev.riqui.example.service.EmailService;
import dev.riqui.example.service.ScraperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author ricardoquiroga on 01-08-24
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class LeadController {

    private final ScraperService scraperService;
    private final EmailService emailService;
    private final EmailAIService emailAIService;
    private List<Map<String, String>> results = new ArrayList<>();


    @GetMapping("/")
    public String index() {
        return "redirect:/scraper";
    }

    @GetMapping("/scraper")
    public String showScraper() {
        return "scraper";
    }

    @PostMapping("/scraper")
    public String scrape(@RequestParam("profession") String profession,
                         @RequestParam("country") String country,
                         @RequestParam("platforms") List<String> platforms) {
        results = scraperService.scrapeGoogle(profession, country, platforms);
        return "mail";
    }

    @GetMapping("/mail")
    public String mail() {
        return "mail";
    }

    @PostMapping("/mail")
    public String mail(@RequestParam("product") String product,
                       @RequestParam("sender") String sender,
                       Model model) {
        log.info("results size: {}", results.size());
        int emailCount = 0;
        for (Map<String, String> result : results) {
            Map<String, String> mailContent = emailAIService.getMailContent(result.get("name"), product, sender);
            log.info("Body: {}", mailContent.get("body"));
            emailService.sendEmail(result.get("email"), mailContent.get("subject"), mailContent.get("body"));
            emailCount++;
        }
        model.addAttribute("emailCount", emailCount);
        return "result";
    }

    @PostMapping("/mail/test")
    public String mailTest(@RequestParam("to") String to,
                           @RequestParam("product2") String product2, @RequestParam("senderName") String senderName,
                           Model model) {
        Map<String, String> mailContent = emailAIService.getMailContent("Test", product2, senderName);
        emailService.sendEmail(to, mailContent.get("subject"), mailContent.get("body"));
        model.addAttribute("mailContent", mailContent.get("body"));
        return "modal";
    }

}
