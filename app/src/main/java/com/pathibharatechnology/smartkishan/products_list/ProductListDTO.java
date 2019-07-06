package com.pathibharatechnology.smartkishan.products_list;

import java.util.HashMap;

public class ProductListDTO {

    private String productImageUrl;
    private String productName;
    private String productCategory;
    private Integer productPrice;
    private String productDescription;
    private String productDeliveryLocation;

    private String productUploaderUserId;
    private String productId;

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public Integer getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(Integer productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductDeliveryLocation() {
        return productDeliveryLocation;
    }

    public void setProductDeliveryLocation(String productDeliveryLocation) {
        this.productDeliveryLocation = productDeliveryLocation;
    }

    public String getProductUploaderUserId() {
        return productUploaderUserId;
    }

    public void setProductUploaderUserId(String productUploaderUserId) {
        this.productUploaderUserId = productUploaderUserId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
