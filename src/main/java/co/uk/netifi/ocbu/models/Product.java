package co.uk.netifi.ocbu.models;

import java.util.ArrayList;
import java.util.List;

public class Product {

    private int productId;
    private String name;
    private String model;
    private String sku;
    private String imageLocation;
    private String imageName;
    private String imageExt;
    private double price;

    private List<Category> categories = new ArrayList<>();

    public Product(int productId, String name, String model, String sku, String imageLocation, String imageName, String imageExt, double price) {
        this.productId = productId;
        this.name = name;
        this.model = model;
        this.sku = sku;
        this.imageLocation = imageLocation;
        this.imageName = imageName;
        this.imageExt = imageExt;
        this.price = price;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getImageLocation() {
        return imageLocation;
    }

    public void setImageLocation(String imageLocation) {
        this.imageLocation = imageLocation;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageExt() {
        return imageExt;
    }

    public void setImageExt(String imageExt) {
        this.imageExt = imageExt;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

}
