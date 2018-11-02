package cn.cvtt.nuoche.entity.gift;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="gift_card_rules")
public class GiftCardRules {

    @Id
    @GeneratedValue
    private  Long    id;

    @Column(name="regex_id")
    private  String    regexId;

    @Column(name="card_name")
    private  String  cardName;

    //1是套餐卡，2是号码卡
    @Column(name="card_type")
    private  Integer  cardType=1;

    @Column(name="price")
    private  Integer  price;
    @Column(name="business_id")
    private  String  businessID;

    @Transient
    private String regexName;

    public GiftCardRules() {
    }
    public GiftCardRules(Long id, Integer cardType, String regexId, String cardName, Integer price,String businessID) {
        this.id = id;
        this.regexId = regexId;
        this.cardType = cardType;
        this.cardName = cardName;
        this.price = price;
        this.businessID = businessID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRegexId() {
        return regexId;
    }

    public void setRegexId(String regexId) {
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

    public Integer getCardType() {
        return cardType;
    }

    public void setCardType(Integer cardType) {
        this.cardType = cardType;
    }


    public String getRegexName() {
        return regexName;
    }

    public void setRegexName(String regexName) {
        this.regexName = regexName;
    }

    public String getBusinessID() {
        return businessID;
    }

    public void setBusinessID(String businessID) {
        this.businessID = businessID;
    }
}
