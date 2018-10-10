package cn.cvtt.nuoche.server;

import cn.cvtt.nuoche.common.result.Result;
import cn.cvtt.nuoche.entity.business.BindVo;

import java.io.IOException;

/**
 * @decription NumberService
 * <p>号码相关服务</p>
 * @author Yampery
 * @date 2018/3/12 9:13
 */
public interface NumberService {

    /**
     * 单独一个对象绑定
     * @param param
     * @return
     * @throws IOException
     */
     Result bind(BindVo param) throws  IOException;

    Result bindZhiZun(BindVo param)  throws IOException;

    Result unbind(String uidnumber) throws IOException;

    Result extend(BindVo param) throws IOException;

    Result extendZhiZun(BindVo param)  throws IOException;

    Result recoverRelation(BindVo param) throws IOException;

    Result recoverRelationZZ(BindVo param)  throws IOException;

   //靓号查询接口
   Result queryRelation(BindVo param)  throws IOException;
    //查询接口
    Result queryNormalRelation(BindVo param)  throws IOException;
    //更改接口
    Result changeBindNew(BindVo bindVo) throws IOException;
}
