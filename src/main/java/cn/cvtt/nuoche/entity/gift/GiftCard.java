package cn.cvtt.nuoche.entity.gift;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name="gift_card")
public class GiftCard {

    @Id
    @GeneratedValue
    private  Long    id;

    @Column(name="regex_id")
    private  Long    regexId;

    @Column(name="card_name")
    private  String  cardName;

    @Column(name="number")
    private  Integer    number;

    @Column(name="card_type")
    private  Integer  cardType;

    @Column(name="price")
    private  Integer  price;

    @Column(name="valid_time_unit")
    private  Integer  validTimeUnit;

    @Column(name="valid_time_number")
    private  Integer  validTimeNumber;

    @Column(name="buyer_openid")
    private  String  openid;

    @Column(name="buy_time")
    private Date buyTime;

    public GiftCard() {
    }
    public GiftCard(Long id,Integer cardType,Integer number, Long regexId, String cardName, Integer price,Integer validTimeUnit,Integer validTimeNumber,String openid,Date buyTime ) {
        this.id = id;
        this.regexId = regexId;
        this.cardType = cardType;
        this.cardName = cardName;
        this.price = price;
        this.validTimeUnit = validTimeUnit;
        this.validTimeNumber = validTimeNumber;
        this.openid = openid;
        this.buyTime = buyTime;
        this.number = number;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRegexId() {
        return regexId;
    }

    public void setRegexId(Long regexId) {
        this.regexId = regexId;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getValidTimeUnit() {
        return validTimeUnit;
    }

    public void setValidTimeUnit(Integer validTimeUnit) {
        this.validTimeUnit = validTimeUnit;
    }

    public Integer getValidTimeNumber() {
        return validTimeNumber;
    }

    public void setValidTimeNumber(Integer validTimeNumber) {
        this.validTimeNumber = validTimeNumber;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public Date getBuyTime() {
        return buyTime;
    }

    public void setBuyTime(Date buyTime) {
        this.buyTime = buyTime;
    }

    public Integer getCardType() {
        return cardType;
    }

    public void setCardType(Integer cardType) {
        this.cardType = cardType;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

}
