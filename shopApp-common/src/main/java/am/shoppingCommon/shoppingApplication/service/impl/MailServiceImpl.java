package am.shoppingCommon.shoppingApplication.service.impl;


import am.shoppingCommon.shoppingApplication.entity.User;
import am.shoppingCommon.shoppingApplication.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
    private final MailSender mailSender;

    @Value("${site.url.web}")
    private String siteUrl;

    @Async
    public void sendMailForAuth(User user) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(user.getEmail());
        simpleMailMessage.setSubject("ShopApp Auth");
        String text = "Welcome " + user.getName() + ". Please verify your account by clicking " + siteUrl + "/user/verify?email=" + user.getEmail() + "&token=" + user.getToken();
        simpleMailMessage.setText(text);
        mailSender.send(simpleMailMessage);

    }

    @Override
    public void sendMailForForgotPassword(User user) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(user.getEmail());
        simpleMailMessage.setSubject("ShopApp Forgot Password");
        String text = "Welcome " + user.getName() + ". Please verify your account by clicking " + siteUrl + "/user/changePassword?email=" + user.getEmail() + "&token=" + user.getToken();
        simpleMailMessage.setText(text);
        mailSender.send(simpleMailMessage);
    }
}
