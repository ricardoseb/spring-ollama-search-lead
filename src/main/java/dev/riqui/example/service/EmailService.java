package dev.riqui.example.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * @author ricardoquiroga on 11-08-24
 */
@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final Parser parser;
    private final HtmlRenderer renderer;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
        this.parser = Parser.builder().build();
        this.renderer = HtmlRenderer.builder().build();
    }

    @Async
    public CompletableFuture<Void> sendEmail(String to, String subject, String content) {
        return CompletableFuture.runAsync(() -> {
            try {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
                helper.setTo(to);
                helper.setSubject(subject);

                String htmlContent = convertMarkdownToHtml(content);
                helper.setText(htmlContent, true);

                mailSender.send(mimeMessage);
            } catch (MessagingException e) {
                throw new RuntimeException("Failed to send email", e);
            }
        });
    }

    private String convertMarkdownToHtml(String markdown) {
        Node document = parser.parse(markdown);
        return renderer.render(document);
    }
}
