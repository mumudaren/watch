package cn.cvtt.nuoche.reponsitory;

import cn.cvtt.nuoche.entity.business.businessPayNotify;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface IBusinessPayRepository extends JpaRepository<businessPayNotify,Long> {

      businessPayNotify  findByOpenidEqualsAndOutTradeNo(String openid,String out_trade_no);

      businessPayNotify  findByOpenidEqualsAndId(String openid,Long id);

      List<businessPayNotify>  findAllByOpenidEqualsAndPhoneEqualsAndProductIdEquals(String openid,String phone,Integer productId);

      List<businessPayNotify>  findAllByBusinessEqualsAndPhoneEqualsAndProductIdEqualsAndCreateTimeGreaterThanEqualAndCreateTimeLessThan(String business, String phone, Integer productId,  Date today,Date tomorrow);

}
