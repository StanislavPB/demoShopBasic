package org.demoshop.services.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserMailSender {

    private final JavaMailSender javaMailSender;

    public void send(String email, String subject, String text) {
        MimeMessage message = javaMailSender.createMimeMessage(); // создаем сообщение
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8"); // создали некую "обертку" из spring "для удобства"

        try {
            // прописать данные для письма
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(text, true);
        } catch (MessagingException e) {
            throw new IllegalStateException(e);
        }

        javaMailSender.send(message);

    }
}
