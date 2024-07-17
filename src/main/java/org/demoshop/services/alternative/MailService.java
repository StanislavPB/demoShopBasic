package org.demoshop.services.alternative;

import lombok.RequiredArgsConstructor;
import org.demoshop.models.User;
import org.demoshop.services.mail.MailCreateUtil;
import org.demoshop.services.mail.UserMailSender;

@RequiredArgsConstructor

public class MailService {

    private final UserMailSender userMailSender;
    private final MailCreateUtil mailCreateUtil;

    public void sendConfirmationEmail(User user, String code) {
        String link = code;
        String html = mailCreateUtil.createConfirmationMail(user.getFirstName(), user.getLastName(), link);
        userMailSender.send(user.getEmail(), "Registration", html);
    }
}
