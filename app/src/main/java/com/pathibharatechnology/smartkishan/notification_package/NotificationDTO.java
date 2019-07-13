package com.pathibharatechnology.smartkishan.notification_package;

public class NotificationDTO {

    String notificationId;
    String productId;
    String productName;
    String notificationSenderId;
    String notificationSenderName;
    Boolean statusRead;
    String createdDate;
    String productUploaderUserId;

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getNotificationSenderId() {
        return notificationSenderId;
    }

    public void setNotificationSenderId(String notificationSenderId) {
        this.notificationSenderId = notificationSenderId;
    }

    public Boolean getStatusRead() {
        return statusRead;
    }

    public void setStatusRead(Boolean statusRead) {
        this.statusRead = statusRead;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getProductUploaderUserId() {
        return productUploaderUserId;
    }

    public void setProductUploaderUserId(String productUploaderUserId) {
        this.productUploaderUserId = productUploaderUserId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getNotificationSenderName() {
        return notificationSenderName;
    }

    public void setNotificationSenderName(String notificationSenderName) {
        this.notificationSenderName = notificationSenderName;
    }
}
