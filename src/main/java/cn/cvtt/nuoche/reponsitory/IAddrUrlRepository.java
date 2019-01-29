package cn.cvtt.nuoche.reponsitory;


import cn.cvtt.nuoche.entity.gift.GiftCard;
import cn.cvtt.nuoche.entity.watch.AddrUrl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IAddrUrlRepository extends JpaRepository<AddrUrl,Long> {
    List<AddrUrl>  findAllByNameEquals(String  name);
    List<AddrUrl>  findAllByNameLike(String  name);
    AddrUrl findFirstByNameEquals(String  name);
}
