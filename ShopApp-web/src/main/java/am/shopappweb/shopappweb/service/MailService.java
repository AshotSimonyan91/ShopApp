package am.shopappweb.shopappweb.service;

public interface MailService {

    public void sendMail(String to, String subject, String text);

}