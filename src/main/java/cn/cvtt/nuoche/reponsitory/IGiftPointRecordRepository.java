package cn.cvtt.nuoche.reponsitory;


import cn.cvtt.nuoche.entity.gift.GiftCoupon;
import cn.cvtt.nuoche.entity.gift.GiftPointRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface IGiftPointRecordRepository extends JpaRepository<GiftPointRecord,Long> {
    GiftPointRecord findByResourceEqualsAndUpdateTimeGreaterThanEqualAndUpdateTimeLessThanOrderByUpdateTimeDesc(int resource, Date startTime, Date endTime);
    List<GiftPointRecord> findByOpenidEqualsAndChangePointLessThanOrderByUpdateTimeDesc(String openid,int lessThanVal);
    List<GiftPointRecord> findByOpenidEqualsAndChangePointGreaterThanOrderByUpdateTimeDesc(String openid,int MoreThanVal);
}
