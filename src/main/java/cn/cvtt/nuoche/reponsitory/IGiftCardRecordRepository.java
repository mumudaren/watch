package cn.cvtt.nuoche.reponsitory;


import cn.cvtt.nuoche.entity.gift.GiftCardRecord;
import org.springframework.data.jpa.repository.JpaRepository;


public interface IGiftCardRecordRepository extends JpaRepository<GiftCardRecord,Long> {
//    List<GiftCard>  findAllByCardTypeEquals(int type);
//    GiftCard findByIdEquals(long id);
}
