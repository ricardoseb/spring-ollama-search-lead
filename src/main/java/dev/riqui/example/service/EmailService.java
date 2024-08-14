package dev.riqui.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author ricardoquiroga on 11-08-24
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(Map<String, String> emailName, String content) {
        String email = emailName.get("email");
        String name = emailName.get("name");
        sendEmail(email, name, content);
        ;
    }

    public void sendEmail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }


}
