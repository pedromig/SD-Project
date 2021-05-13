package models;

import models.interfaces.ProductModel;

/**
 * Model with the information about a laptop product.
 */
public class LaptopProductModel implements ProductModel {
    private String model;
    private String brand;
    private Integer price;
    private Integer screenSize;

    public LaptopProductModel(String model, String brand, Integer price, Integer screenSize)
    {
        setModel(model);
        setBrand(brand);
        setPrice(price);
        setScreenSize(screenSize);
    }

    public LaptopProductModel()
    {
        this(null, null, null, null);
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getScreenSize() {
        return screenSize;
    }

    public void setScreenSize(Integer screenSize) {
        this.screenSize = screenSize;
    }
}
