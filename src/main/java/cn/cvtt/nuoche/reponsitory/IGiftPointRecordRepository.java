package cn.cvtt.nuoche.reponsitory;


import cn.cvtt.nuoche.entity.gift.GiftCoupon;
import cn.cvtt.nuoche.entity.gift.GiftPointRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface IGiftPointRecordRepository extends JpaRepository<GiftPointRecord,Long> {
    GiftPointRecord findByResourceEqualsAndUpdateTimeGreaterThanEqualAndUpdateTimeLessThanAndOpenidEqualsOrderByUpdateTimeDesc(int resource, Date startTime, Date endTime,String openid);
    List<GiftPointRecord> findByOpenidEqualsAndChangePointLessThanAndUpdateTimeGreaterThanOrderByUpdateTimeDesc(String openid,int lessThanVal,Date nowBefore30);
    List<GiftPointRecord> findByOpenidEqualsAndChangePointGreaterThanAndUpdateTimeGreaterThanOrderByUpdateTimeDesc(String openid,int MoreThanVal,Date nowBefore30);
}
