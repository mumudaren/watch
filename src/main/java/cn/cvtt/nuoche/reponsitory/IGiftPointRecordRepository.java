package cn.cvtt.nuoche.reponsitory;


import cn.cvtt.nuoche.entity.gift.GiftCoupon;
import cn.cvtt.nuoche.entity.gift.GiftPointRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IGiftPointRecordRepository extends JpaRepository<GiftPointRecord,Long> {
}
