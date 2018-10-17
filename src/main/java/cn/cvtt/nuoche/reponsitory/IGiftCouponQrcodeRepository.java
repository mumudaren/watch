package cn.cvtt.nuoche.reponsitory;


import cn.cvtt.nuoche.entity.gift.GiftCouponQrcode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IGiftCouponQrcodeRepository extends JpaRepository<GiftCouponQrcode,Long> {
    GiftCouponQrcode findByIdEquals(Long id);
    GiftCouponQrcode   findByQrcodeEquals(String  qrcodeId);
    GiftCouponQrcode findByCouponIdEquals(Long couponId);
    GiftCouponQrcode findByCouponIdEqualsAndCreatorOpenidEquals(Long couponId,String creator);
}
