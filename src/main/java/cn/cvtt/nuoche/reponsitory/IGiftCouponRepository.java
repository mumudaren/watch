package cn.cvtt.nuoche.reponsitory;


import cn.cvtt.nuoche.entity.gift.GiftCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface IGiftCouponRepository extends JpaRepository<GiftCoupon,Long> {
    GiftCoupon findByIsAbleEquals(int isAble);
    GiftCoupon findByIdEquals(long couponId);
    GiftCoupon findByIdEqualsAndEndTimeGreaterThanEqual(long couponId, Date endTime);
}
