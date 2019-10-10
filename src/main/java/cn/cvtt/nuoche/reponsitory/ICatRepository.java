package cn.cvtt.nuoche.reponsitory;


import cn.cvtt.nuoche.entity.game.Cat;
import cn.cvtt.nuoche.entity.watch.NameCount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ICatRepository extends JpaRepository<Cat,Long> {
}
