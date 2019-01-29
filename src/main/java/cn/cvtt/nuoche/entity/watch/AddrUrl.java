package cn.cvtt.nuoche.entity.watch;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="address_url")
public class AddrUrl {

    @Id
    @GeneratedValue
    private  Long    id;
    //创建者
    @Column(name="creator")
    private  String  creator;

    //name
    @Column(name="name")
    private  String  name ;

    //url
    @Column(name="url")
    private  String  url;

    //备注
    @Column(name="ps")
    private  String  ps;

    //order
    @Column(name="orderValue")
    private  Integer  orderValue;

    public AddrUrl() {
    }

    public AddrUrl(Long id,  String creator,String name,String url,String ps,Integer orderValue) {
        this.id = id;
        this.creator = creator;
        this.name = name;
        this.url = url;
        this.ps = ps;
        this.orderValue = orderValue;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public Integer getOrderValue() {
        return orderValue;
    }

    public void setOrderValue(Integer orderValue) {
        this.orderValue = orderValue;
    }
}
