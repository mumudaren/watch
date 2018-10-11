package cn.cvtt.nuoche.reponsitory;


import cn.cvtt.nuoche.entity.gift.GiftCardRules;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IGiftCardRulesRepository extends JpaRepository<GiftCardRules,Long> {
    List<GiftCardRules>  findAllByCardTypeEquals(int type);
    GiftCardRules findByIdEquals(long id);
}
