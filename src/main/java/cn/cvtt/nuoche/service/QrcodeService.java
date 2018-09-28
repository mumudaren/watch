package cn.cvtt.nuoche.service;

import cn.cvtt.nuoche.entity.gift.GiftCardRecord;

public interface QrcodeService {

    void save(GiftCardRecord q);
    void update(GiftCardRecord q);
    GiftCardRecord queryObject(String qrcodeId);

    /**
     * 用户注册生成二维码
     * @param cardRecordId 支付用户
     * @return 返回二维码地址
     */
    String generatorQrcode(Long cardRecordId);




}
