package cn.cvtt.nuoche.reponsitory;


import cn.cvtt.nuoche.entity.gift.GiftCouponQrcode;
import cn.cvtt.nuoche.entity.gift.GiftNumberQRcode;
import cn.cvtt.nuoche.entity.gift.GiftPoint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IGiftNumberQRcodeRepository extends JpaRepository<GiftNumberQRcode,Long> {
    GiftNumberQRcode findByOpenidEquals(String openId);
    GiftNumberQRcode findByQrcodeEquals(String  qrcodeId);
}
