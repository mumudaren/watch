package cn.cvtt.nuoche.reponsitory;


import cn.cvtt.nuoche.entity.gift.GiftAwardsRules;
import cn.cvtt.nuoche.entity.gift.GiftPoint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IGiftAwardsRulesRepository extends JpaRepository<GiftAwardsRules,Long> {
    GiftAwardsRules findByIsAbleEquals(Integer isAble);
}
