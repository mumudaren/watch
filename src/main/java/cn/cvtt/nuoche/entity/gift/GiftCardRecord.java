package cn.cvtt.nuoche.entity.gift;


import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="gift_card_record")
public class GiftCardRecord {

    @Id
    @GeneratedValue
    private  Long    id;

    @Column(name="card_id")
    private  Long    cardId;

    @Column(name="sender_openid")
    private  String  senderOpenid;

    @Column(name="receiver_openid")
    private   String  receiverOpenid;

    @Column(name="message")
    private   String  message;

    @Column(name="qrcode")
    private   String  qrcode;

    @Column(name="qrcode_url")
    private   String  qrcodeUrl;

    @Column(name="get_time")
    private Date getTime;

    @Column(name="buy_time")
    private Date buyTime;

    @Column(name="get_status")
    private Integer getStatus;

    @Transient
    private GiftCard giftCard;


    public GiftCardRecord() {
    }

    public GiftCardRecord(Long id, Long cardId, String senderOpenid, String receiverOpenid, String message,Date getTime,Integer getStatus,String qrcode,Date buyTime) {
        this.id = id;
        this.cardId = cardId;
        this.senderOpenid = senderOpenid;
        this.receiverOpenid = receiverOpenid;
        this.message = message;
        this.getTime = getTime;
        this.getStatus = getStatus;
        this.qrcode = qrcode;
        this.buyTime = buyTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public String getSenderOpenid() {
        return senderOpenid;
    }

    public void setSenderOpenid(String senderOpenid) {
        this.senderOpenid = senderOpenid;
    }

    public String getReceiverOpenid() {
        return receiverOpenid;
    }

    public void setReceiverOpenid(String receiverOpenid) {
        this.receiverOpenid = receiverOpenid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getGetTime() {
        return getTime;
    }

    public void setGetTime(Date getTime) {
        this.getTime = getTime;
    }

    public Integer getGetStatus() {
        return getStatus;
    }

    public void setGetStatus(Integer getStatus) {
        this.getStatus = getStatus;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public String getQrcodeUrl() {
        return qrcodeUrl;
    }

    public void setQrcodeUrl(String qrcodeUrl) {
        this.qrcodeUrl = qrcodeUrl;
    }

    public GiftCard getGiftCard() {
        return giftCard;
    }

    public void setGiftCard(GiftCard giftCard) {
        this.giftCard = giftCard;
    }

    public Date getBuyTime() {
        return buyTime;
    }

    public void setBuyTime(Date buyTime) {
        this.buyTime = buyTime;
    }
}
