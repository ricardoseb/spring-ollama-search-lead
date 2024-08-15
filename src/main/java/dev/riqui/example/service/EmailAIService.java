package dev.riqui.example.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ricardoquiroga on 01-08-24
 */
@Service
@Slf4j
public class EmailAIService {

    @Value("classpath:/prompts/email.st")
    private Resource emailPrompt;

    private final ChatClient chatClient;


    public EmailAIService(ChatClient.Builder chatClient) {
        this.chatClient = chatClient.build();
    }

    public Map<String, String> getMailContent(String name, String product, String sender) {
        String content = this.chatClient.prompt()
                .user(u -> u.text(emailPrompt).params(createParams(name, product, sender)))
                .call()
                .content();

        return extractSubjectAndBody(cleanContent(content));
    }

    private Map<String, Object> createParams(String name, String product, String sender) {
        Map<String, Object> params = new HashMap<>();
        params.put("product", product);
        params.put("sender", sender);
        params.put("name", name);

        return params;
    }

    private String cleanContent(String content) {
        content = content.replaceAll("\\*\\*", "");
        content = content.replaceAll("<[/]?(strong|b|em|i|u|br)[^>]*>", "");
        content = content.replaceAll("<br\\s*/?>", "\n");

        return content;
    }

    private Map<String, String> extractSubjectAndBody(String content) {
        Map<String, String> result = new HashMap<>();
        Pattern pattern = Pattern.compile("Asunto: (.*?)\\n(.*)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);

        if (matcher.find()) {
            result.put("subject", matcher.group(1).trim());
            result.put("body", matcher.group(2).trim());
        } else {
            result.put("subject", "");
            result.put("body", content.trim());
        }

        return result;
    }
}