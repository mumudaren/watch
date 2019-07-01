package cn.cvtt.nuoche.reponsitory;


import cn.cvtt.nuoche.entity.watch.NameCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface INamaCountRepository extends JpaRepository<NameCount,Long> {
    List<NameCount>  findAllByNameEquals(String name);
    List<NameCount>  findAllByNameLike(String name);

    @Query("select sum(t.name) as num,t.goods as goods from name_count t group by t.name")
    List findGroupByName();

    NameCount findFirstByNameEquals(String name);
}
