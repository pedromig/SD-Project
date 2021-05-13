package services.interfaces;

import models.interfaces.ProductModel;

import java.util.List;

/* Every service needs to implement this interface and its function */
public interface SearchService {
    List<Object> search(ProductModel query);
}
