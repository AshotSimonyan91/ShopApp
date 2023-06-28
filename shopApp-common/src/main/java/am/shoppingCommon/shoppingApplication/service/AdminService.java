package am.shoppingCommon.shoppingApplication.service;

import am.shoppingCommon.shoppingApplication.entity.User;

public interface AdminService {

    void block(int id, User user);
    void unBlock(int id, User user);

}
