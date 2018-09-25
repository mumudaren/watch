package cn.cvtt.nuoche.entity.gift;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="gift_card")
public class GiftCard {

    @Id
    @GeneratedValue
    private  Long    id;

    @Column(name="regex_id")
    private  String    regexId;

    @Column(name="card_name")
    private  String  cardName;

    @Column(name="number")
    private  String    number;

    @Column(name="card_type")
    private  Integer  cardType;

    @Column(name="price")
    private  Integer  price;

    @Column(name="valid_time_unit")
    private  Integer  validTimeUnit;

    @Column(name="valid_time_number")
    private  Integer  validTimeNumber;

    @Transient
    private String validTimeUnitName;

    @Transient
    private String regexName;

    public GiftCard() {
    }
    public GiftCard(Long id,Integer cardType,String number, String regexId, String cardName, Integer price,Integer validTimeUnit,Integer validTimeNumber,String regexName) {
        this.id = id;
        this.regexId = regexId;
        this.cardType = cardType;
        this.cardName = cardName;
        this.price = price;
        this.validTimeUnit = validTimeUnit;
        this.validTimeNumber = validTimeNumber;
        this.number = number;
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


    public Integer getCardType() {
        return cardType;
    }

    public void setCardType(Integer cardType) {
        this.cardType = cardType;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getRegexName() {
        return regexName;
    }

    public void setRegexName(String regexName) {
        this.regexName = regexName;
    }

    public String getValidTimeUnitName() {
        return validTimeUnitName;
    }

    public void setValidTimeUnitName(String validTimeUnitName) {
        this.validTimeUnitName = validTimeUnitName;
    }
}
