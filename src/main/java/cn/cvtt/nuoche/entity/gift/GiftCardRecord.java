package cn.cvtt.nuoche.entity.gift;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name="gift_point")
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

    @Column(name="get_time")
    private Date getTime;

    public GiftCardRecord() {
    }

    public GiftCardRecord(Long id, String senderOpenid, String receiverOpenid, String message,Date getTime) {
        this.id = id;
        this.senderOpenid = senderOpenid;
        this.receiverOpenid = receiverOpenid;
        this.message = message;
        this.getTime = getTime;
    }


}
