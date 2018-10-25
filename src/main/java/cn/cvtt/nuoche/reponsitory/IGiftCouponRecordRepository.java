package cn.cvtt.nuoche.reponsitory;


import cn.cvtt.nuoche.entity.gift.GiftCoupon;
import cn.cvtt.nuoche.entity.gift.GiftCouponRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface IGiftCouponRecordRepository extends JpaRepository<GiftCouponRecord,Long> {
    GiftCouponRecord findGiftCouponRecordByIdEquals(long id);
    GiftCouponRecord findGiftCouponRecordBySenderOpenidEqualsAndReceiverOpenidEquals(String sender,String receiver);
    List<GiftCouponRecord> findAllByReceiverOpenidEquals(String receiverOpenid);
    List<GiftCouponRecord> findAllByReceiverOpenidEqualsAndIsUsedEqualsOrderByGetTimeDesc(String receiverOpenid,int isUsed);

    @Query(value = "select record.id,record.senderOpenid,record.receiverOpenid,record.couponId,record.getTime,record.isUsed " +
            "from GiftCouponRecord record,GiftCoupon coupon where record.receiverOpenid = :receiverOpenid AND record.isUsed=:isUsed AND coupon.effectiveTime>:validDate")
    List<GiftCouponRecord> findByNamedParam(@Param("receiverOpenid") String receiverOpenid,
                                            @Param("isUsed") int isUsed,
                                            @Param("validDate") Date validDate);
}
