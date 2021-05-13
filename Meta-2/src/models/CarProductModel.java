package models;

import models.interfaces.ProductModel;

/**
 * Model with the information about a car product.
 */
public class CarProductModel implements ProductModel {
    private String carModel;
    private String manufacturer;
    private Integer year;
    private Integer price;

    public CarProductModel(String carModel, String manufacturer, Integer year, Integer price)
    {
        setCarModel(carModel);
        setManufacturer(manufacturer);
        setYear(year);
        setPrice(price);
    }

    public CarProductModel()
    {
        this(null, null, null, null);
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}
