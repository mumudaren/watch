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

    @Column(name="card_regex_id")
    private  Long    cardRegexId;

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


    public GiftCard() {
    }
    public GiftCard(Long id,Integer cardType,String number, Long cardRegexId, String cardName, Integer price,Integer validTimeUnit,Integer validTimeNumber) {
        this.id = id;
        this.cardRegexId = cardRegexId;
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

    public Long getCardRegexId() {
        return cardRegexId;
    }

    public void setCardRegexId(Long cardRegexId) {
        this.cardRegexId = cardRegexId;
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

}
