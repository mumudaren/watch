package cn.cvtt.nuoche.reponsitory;


import cn.cvtt.nuoche.entity.gift.GiftCouponQrcode;
import cn.cvtt.nuoche.entity.gift.GiftNumberQRcode;
import cn.cvtt.nuoche.entity.gift.GiftPoint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IGiftNumberQRcodeRepository extends JpaRepository<GiftNumberQRcode,Long> {
    GiftNumberQRcode findByOpenidEqualsAndNumber95Equals(String openId,String number95);
    GiftNumberQRcode findByQrcodeEquals(String  qrcodeId);
    GiftNumberQRcode findByIdEquals(Long  id);
}
