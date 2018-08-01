package cn.cvtt.nuoche.reponsitory;

import cn.cvtt.nuoche.entity.business.BusinessCustomer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IBusinessCusRepository extends JpaRepository<BusinessCustomer,Long> {


    BusinessCustomer    findByOpenidEquals(String openid);
}
