package am.shopappweb.shopappweb.service;


import am.shoppingCommon.shoppingApplication.entity.Product;

import java.io.IOException;
import java.util.List;

public interface MainService {
    byte[] getImage(String imageName) throws IOException;

    List<Product> search(String value);
}
