package cn.cvtt.nuoche.entity.gift;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="gift_number_qrcode")
public class GiftNumberQRcode {

    @Id
    @GeneratedValue
    private  Long    id;
    @Column(name="openid")
    private  String  openid;
    @Column(name="number95")
    private  String  number95;
    @Column(name="qrcode")
    private   String  qrcode;

    @Column(name="qrcode_url")
    private   String  qrcodeUrl;


    public GiftNumberQRcode() {
    }

    public GiftNumberQRcode(Long id, String openid, String number95,String qrcode,String qrcodeUrl) {
        this.id = id;
        this.openid = openid;
        this.number95 = number95;
        this.qrcode = qrcode;
        this.qrcodeUrl = qrcodeUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getNumber95() {
        return number95;
    }

    public void setNumber95(String number95) {
        this.number95 = number95;
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
}
