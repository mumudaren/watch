package cn.cvtt.nuoche.reponsitory;


import cn.cvtt.nuoche.entity.gift.GiftCardRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface IGiftCardRecordRepository extends JpaRepository<GiftCardRecord,Long> {
//    List<GiftCard>  findAllByCardTypeEquals(int type);
//    GiftCard findByIdEquals(long id);
    GiftCardRecord   findByQrcodeEquals(String  qrcodeId);
    GiftCardRecord   findByIdEquals(Long  cardRecordId);

    List<GiftCardRecord> findByGetStatusEqualsAndSenderOpenidEqualsOrderByGetTimeDesc(Integer  getStatus,String senderOpenid);
    List<GiftCardRecord> findByGetStatusEqualsAndReceiverOpenidEqualsOrderByGetTimeDesc(Integer  getStatus,String receiver);

    GiftCardRecord   findBySenderOpenidEquals(String  senderOpenid);
}
