package cn.cvtt.nuoche.entity.business;

import javax.persistence.*;
import java.util.Date;


public class wx_product {
    private  Long  id;
    private  String  productName;
    private  Integer validDay;
    private  Integer validHour;
    private  Integer productPrice;
    private  Integer initPrice;
    private  String  productBusiness;
    private  Date    createTime;
    private  String  productInfo;
    private  Integer productLimit;
    private  String  productType;
    private  Integer leaveMessage;
    private  Integer message;

    public wx_product() {
    }

    public wx_product(String productName, Integer validDay, Integer productPrice, Integer initPrice,
                      String productBusiness, Date createTime, String productInfo, Integer validHour) {
        this.productName = productName;
        this.validDay = validDay;
        this.productPrice = productPrice;
        this.initPrice = initPrice;
        this.productBusiness = productBusiness;
        this.createTime = createTime;
        this.productInfo = productInfo;
        this.validHour = validHour;
    }

    public Integer getProductLimit() {
        return productLimit;
    }

    public void setProductLimit(Integer productLimit) {
        this.productLimit = productLimit;
    }

    public Integer getLeaveMessage() {
        return leaveMessage;
    }

    public void setLeaveMessage(Integer leaveMessage) {
        this.leaveMessage = leaveMessage;
    }

    public Integer getMessage() {
        return message;
    }

    public void setMessage(Integer message) {
        this.message = message;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getValidDay() {
        return validDay;
    }

    public void setValidDay(Integer validDay) {
        this.validDay = validDay;
    }

    public Integer getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(Integer productPrice) {
        this.productPrice = productPrice;
    }

    public Integer getInitPrice() {
        return initPrice;
    }

    public void setInitPrice(Integer initPrice) {
        this.initPrice = initPrice;
    }

    public String getProductBusiness() {
        return productBusiness;
    }

    public void setProductBusiness(String productBusiness) {
        this.productBusiness = productBusiness;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getProductInfo() {
        return productInfo;
    }

    public void setProductInfo(String productInfo) {
        this.productInfo = productInfo;
    }

    public Integer getValidHour() {
        return validHour;
    }

    public void setValidHour(Integer validHour) {
        this.validHour = validHour;
    }
}
