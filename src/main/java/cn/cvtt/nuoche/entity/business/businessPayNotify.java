package cn.cvtt.nuoche.entity.business;


import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="business_pay_notify")
public class businessPayNotify {
    @Id
    @GeneratedValue
    private  Long  id;
    @Column(name="appid")
    private  String appid;
    @Column(name="bank_type")
    private  String bankType;
    @Column(name="cash_fee")
    private  Integer cashFee;
    @Column(name="create_time")
    private  Date  createTime;
    @Column(name="fee_type")
    private  String feeType;
    @Column(name="is_subscribe")
    private  String Subscribed;
    @Column(name="mch_id")
    private  String mchId;
    @Column(name="nonceStr")
    private  String nonce_str;
    @Column(name="openid")
    private  String openid;
    @Column(name="transaction_id")
    private  String transactionId;
    @Column(name="out_trade_no")
    private  String outTradeNo;
    @Column(name="time_end")
    private  String  time_end;
    @Column(name="result_code")
    private  String resultCode;
    @Column(name="return_code")
    private  String returnCode;
    @Column(name="totalFee")
    private  String total_fee;
    @Column(name="tradeType")
    private  String trade_type;
    @Column(name="phone")
    private  String  phone;
    @Column(name="uid_number")
    private  String  uidNumber;
    @Column(name="operate_type")
    private  String  operateType;
    @Column(name="days")
    private  String days;
    @Column(name="business")
    private  String business;
    @Column(name="product_id")
    private  Integer  productId;

    public businessPayNotify() {
    }

    public businessPayNotify(String appid, String bankType, Integer cashFee, Date createTime, String feeType, String subscribed, String mchId, String nonce_str, String openid, String transactionId, String outTradeNo, Date time_end, String resultCode, String returnCode, String sign, String total_fee, String trade_type) {
        this.appid = appid;
        this.bankType = bankType;
        this.cashFee = cashFee;
        this.createTime = createTime;
        this.feeType = feeType;
        this.Subscribed = subscribed;
        this.mchId = mchId;
        this.nonce_str = nonce_str;
        this.openid = openid;
        this.transactionId = transactionId;
        this.outTradeNo = outTradeNo;
        this.resultCode = resultCode;
        this.returnCode = returnCode;
        this.total_fee = total_fee;
        this.trade_type = trade_type;

    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getUidNumber() {
        return uidNumber;
    }

    public void setUidNumber(String uidNumber) {
        this.uidNumber = uidNumber;
    }

    public String getBusiness() {
        return business;
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getBankType() {
        return bankType;
    }

    public void setBankType(String bankType) {
        this.bankType = bankType;
    }

    public Integer getCashFee() {
        return cashFee;
    }

    public void setCashFee(Integer cashFee) {
        this.cashFee = cashFee;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getFeeType() {
        return feeType;
    }

    public void setFeeType(String feeType) {
        this.feeType = feeType;
    }

    public String getSubscribed() {
        return Subscribed;
    }

    public void setSubscribed(String subscribed) {
        Subscribed = subscribed;
    }

    public String getMchId() {
        return mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId;
    }

    public String getNonce_str() {
        return nonce_str;
    }

    public void setNonce_str(String nonce_str) {
        this.nonce_str = nonce_str;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getTime_end() {
        return time_end;
    }

    public void setTime_end(String time_end) {
        this.time_end = time_end;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }


    public String getTotal_fee() {
        return total_fee;
    }

    public void setTotal_fee(String total_fee) {
        this.total_fee = total_fee;
    }

    public String getTrade_type() {
        return trade_type;
    }

    public void setTrade_type(String trade_type) {
        this.trade_type = trade_type;
    }
}
