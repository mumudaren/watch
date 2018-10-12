package cn.cvtt.nuoche.reponsitory;


import cn.cvtt.nuoche.entity.gift.GiftAwards;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IGiftAwardsRepository extends JpaRepository<GiftAwards,Long> {
    List<GiftAwards> findByRulesIdOrderByIndexOrder(Long rulesId);
}
