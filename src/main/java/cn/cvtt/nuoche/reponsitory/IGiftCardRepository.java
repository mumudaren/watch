package cn.cvtt.nuoche.reponsitory;


import cn.cvtt.nuoche.entity.gift.GiftCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IGiftCardRepository extends JpaRepository<GiftCard,Long> {
    List<GiftCard>  findAllByCardTypeEquals(int type);
}
