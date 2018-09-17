package cn.cvtt.nuoche.reponsitory;


import cn.cvtt.nuoche.entity.gift.GiftCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IGiftCouponRepository extends JpaRepository<GiftCoupon,Long> {
    GiftCoupon findByIsAbleEquals(int isAble);
}
