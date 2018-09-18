package cn.cvtt.nuoche.reponsitory;


import cn.cvtt.nuoche.entity.gift.GiftPoint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IGiftPointRepository extends JpaRepository<GiftPoint,Long> {
    GiftPoint findByOpenidEquals(String openId);
}
