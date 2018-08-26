package cn.cvtt.nuoche.reponsitory;

import cn.cvtt.nuoche.entity.business.BusinessNumberRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Date;
import java.util.List;

public interface IBusinessNumberRecordRepository extends JpaRepository<BusinessNumberRecord,Long>,JpaSpecificationExecutor {

    List<BusinessNumberRecord>  findAllByUserPhoneEqualsAndBusinessIdEquals(String phone, String business);
    List<BusinessNumberRecord>  findAllByPrtmsEqualsAndBusinessIdEqualsOrderBySubtsDesc(String phone, String business);
    List<BusinessNumberRecord>  findAllByPrtmsNotAndUserPhoneEqualsAndBusinessIdEqualsOrderBySubtsDesc(String prtms,String UserPhone, String business);
    //60天以内数据
    List<BusinessNumberRecord>  findAllByPrtmsEqualsAndBusinessIdEqualsAndValidTimeGreaterThanEqualOrderByValidTimeDesc(String phone, String business,Date now);
    List<BusinessNumberRecord>  findAllByPrtmsNotAndUserPhoneEqualsAndBusinessIdEqualsAndValidTimeGreaterThanEqualOrderByValidTimeDesc(String prtms,String UserPhone, String business,Date now);
    //BusinessNumberRecord  findBusinessNumberRecordByPrtmsEqualsAndSmbms(String phone, String smbms);
    //List<BusinessNumberRecord>  findBusinessNumberRecordByPrtmsEqualsAndSmbms(String smbms);
    BusinessNumberRecord  findBySmbmsEqualsAndBusinessIdEquals(String smbms,String business);





}
