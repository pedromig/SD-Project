package models;

import models.interfaces.ProductModel;

/**
 * Model with the information about a shoe product.
 */
public class ShoeProductModel implements ProductModel {
    private Integer size;
    private String color;
    private String brand;

    public ShoeProductModel(Integer size, String color, String brand)
    {
        setSize(size);
        setBrand(color);
        setColor(brand);
    }

    public ShoeProductModel()
    {
        this(null, null, null);
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }
}
