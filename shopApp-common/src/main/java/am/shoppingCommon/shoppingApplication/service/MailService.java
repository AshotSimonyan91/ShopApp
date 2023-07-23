package am.shoppingCommon.shoppingApplication.service;

import am.shoppingCommon.shoppingApplication.entity.User;

public interface MailService {

    void sendMailForAuth(User user);

    void sendMailForForgotPassword(User user);
}