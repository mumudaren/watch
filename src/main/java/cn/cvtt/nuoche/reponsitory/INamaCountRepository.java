package cn.cvtt.nuoche.reponsitory;


import cn.cvtt.nuoche.entity.watch.NameCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface INamaCountRepository extends JpaRepository<NameCount,Long> {
    List<NameCount>  findAllByNameEquals(String name);
    List<NameCount>  findAllByNameLike(String name);

    @Query("select count(t.name) as num,t.name as name from NameCount t group by t.name")
    List<NameCount> findGroupByName();

    @Query("select count(t.name) as num,t.name as name from NameCount t where  t.createTime>=?1 group by t.name")
    List<NameCount> findGroupByNameAndTime(Date endTime);

    NameCount findFirstByNameEquals(String name);
}
