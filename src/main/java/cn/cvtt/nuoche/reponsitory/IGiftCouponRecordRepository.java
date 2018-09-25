package cn.cvtt.nuoche.reponsitory;


import cn.cvtt.nuoche.entity.gift.GiftCoupon;
import cn.cvtt.nuoche.entity.gift.GiftCouponRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IGiftCouponRecordRepository extends JpaRepository<GiftCouponRecord,Long> {
    GiftCouponRecord findGiftCouponRecordBySenderOpenidEqualsAndReceiverOpenidEquals(String sender,String receiver);
    List<GiftCouponRecord> findAllByReceiverOpenidEquals(String receiverOpenid);
}
