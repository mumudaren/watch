package cn.cvtt.nuoche.reponsitory;


import cn.cvtt.nuoche.entity.gift.GiftAwards;
import cn.cvtt.nuoche.entity.gift.GiftAwardsRecords;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IGiftAwardsRecordRepository extends JpaRepository<GiftAwardsRecords,Long> {
    //List<GiftAwardsRecords> findByRulesIdOrderByIndexOrder(Long rulesId);
}
