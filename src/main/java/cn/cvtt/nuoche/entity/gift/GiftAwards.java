package cn.cvtt.nuoche.entity.gift;


import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="gift_awards")
public class GiftAwards {

    @Id
    @GeneratedValue
    private  Long    id;
    //活动名称
    @Column(name="rules_id")
    private  Long  rulesId;
    //创建者
    @Column(name="imgurl")
    private  String  imgurl;
    //描述
    @Column(name="gift_name")
    private  String  giftName;
    //概率
    @Column(name="probability")
    private  Double  probability;
    //图片位置索引
    @Column(name="indexOrder")
    private  Integer  indexOrder;
    //库存
    @Column(name="stock")
    private  Integer  stock;
    @Column(name="business_id")
    private  String  businessID;

    public GiftAwards() {
    }

    public GiftAwards(Long id, Long rulesId, String imgurl, String giftName,
                      Double probability, Integer indexOrder,Integer stock,String businessID) {
        this.id = id;
        this.rulesId = rulesId;
        this.imgurl = imgurl;
        this.giftName = giftName;
        this.probability = probability;
        this.indexOrder = indexOrder;
        this.stock = stock;
        this.businessID = businessID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRulesId() {
        return rulesId;
    }

    public void setRulesId(Long rulesId) {
        this.rulesId = rulesId;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getGiftName() {
        return giftName;
    }

    public void setGiftName(String giftName) {
        this.giftName = giftName;
    }

    public Double getProbability() {
        return probability;
    }

    public void setProbability(Double probability) {
        this.probability = probability;
    }

    public Integer getIndexOrder() {
        return indexOrder;
    }

    public void setIndexOrder(Integer indexOrder) {
        this.indexOrder = indexOrder;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getBusinessID() {
        return businessID;
    }

    public void setBusinessID(String businessID) {
        this.businessID = businessID;
    }
}
