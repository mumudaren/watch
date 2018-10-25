package cn.cvtt.nuoche.service.impl;

import cn.cvtt.nuoche.common.Constant;
import cn.cvtt.nuoche.entity.gift.GiftCardRecord;
import cn.cvtt.nuoche.entity.gift.GiftCouponQrcode;
import cn.cvtt.nuoche.entity.gift.GiftCouponRecord;
import cn.cvtt.nuoche.entity.gift.GiftNumberQRcode;
import cn.cvtt.nuoche.reponsitory.IGiftCardRecordRepository;
import cn.cvtt.nuoche.reponsitory.IGiftCouponQrcodeRepository;
import cn.cvtt.nuoche.reponsitory.IGiftCouponRecordRepository;
import cn.cvtt.nuoche.reponsitory.IGiftNumberQRcodeRepository;
import cn.cvtt.nuoche.service.QrcodeService;

import cn.cvtt.nuoche.util.DateUtils;
import cn.cvtt.nuoche.util.QRCodeUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.UUID;

/**
 * @decription QrcodeServiceImpl
 * <p></p>
 * @author Yampery
 * @date 2018/3/15 13:28
 */
@Service
public class QrcodeServiceImpl implements QrcodeService {

    @Resource
    private IGiftCardRecordRepository qrcodeDao;
    @Value("${wx.qrcode.url}")
    private String WX_QRCODE_URL;
    @Value(("${wx.qrcode.path}"))
    private String QRCODE_PATH;
    @Value("${wx.qrcode.download}")
    private String QRCODE_DOWNLOAD;
    @Value("${wx.qrcode.logo.path}")
    private String LOGO_PATH;

    @Autowired
    IGiftCardRecordRepository giftCardRecordRepository;

    @Autowired
    IGiftCouponQrcodeRepository giftCouponQrcodeRepository;

    @Autowired
    IGiftNumberQRcodeRepository giftNumberQRcodeRepository;


    @Override
    public void save(GiftCardRecord q) {
        qrcodeDao.save(q);
    }


    @Override
    public void update(GiftCardRecord q) {

    }

    @Override
    public GiftCardRecord queryObject(String qrcodeId) {
        return null;
    }

    @Override
    public String generatorQrcode(Long cardId,String type) {
        try {
            String qrcodeId =null;
            String now= DateUtils.orderFormat(new Date());
            //保存路径
            String destPath=QRCODE_PATH.substring(QRCODE_PATH.indexOf(":") + 1)+type+"/"+now+"/";
            String href;
            if(StringUtils.equals(type,"card")) {
                //号码卡、套餐卡
                //根据senderOpenid查找record。
                GiftCardRecord qrcode = giftCardRecordRepository.findByIdEquals(cardId);
                qrcodeId=QRCodeUtil.encode(UUID.randomUUID().toString(), type, Constant.APP_SECRET,
                        destPath, WX_QRCODE_URL, LOGO_PATH, "");
                qrcode.setQrcode(qrcodeId);
                //可读取路径
                 href=QRCODE_DOWNLOAD+type+"/"+now+"/" + qrcodeId + ".jpg";
                qrcode.setQrcodeUrl(href);
                // 存入数据库
                save(qrcode);
            }else if(StringUtils.equals(type,"call")){
                //识别二维码呼叫海牛助手类型
                GiftNumberQRcode qrcode=giftNumberQRcodeRepository.findByIdEquals(cardId);
                qrcodeId=QRCodeUtil.encode(UUID.randomUUID().toString(), type, Constant.APP_SECRET,
                        destPath, WX_QRCODE_URL, LOGO_PATH, "");
                qrcode.setQrcode(qrcodeId);
                //可读取路径
                 href=QRCODE_DOWNLOAD+type+"/"+now+"/" + qrcodeId + ".jpg";
                qrcode.setQrcodeUrl(href);
                giftNumberQRcodeRepository.saveAndFlush(qrcode);
            } else{
                //优惠券二维码
                GiftCouponQrcode qrcode = giftCouponQrcodeRepository.findByIdEquals(cardId);
                qrcodeId = QRCodeUtil.encode(UUID.randomUUID().toString(), type, Constant.APP_SECRET,
                        destPath, WX_QRCODE_URL, LOGO_PATH, "");
                qrcode.setQrcode(qrcodeId);
                //可读取路径
                 href=QRCODE_DOWNLOAD+type+"/"+now+"/" + qrcodeId + ".jpg";
                qrcode.setQrcodeUrl(href);
                giftCouponQrcodeRepository.saveAndFlush(qrcode);
            }
            // 返回二维码下载地址
            return href;
        } catch (Exception e) {
             e.printStackTrace();
             return "";
        }

    }
}
