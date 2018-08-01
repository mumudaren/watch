package cn.cvtt.nuoche.util.requestTemplate;

/**
 * @decription ImageMessage
 * <p>图片类消息</p>
 * @author Yampery
 * @date 2018/3/7 16:58
 */
public class ImageMessage extends BaseMessage {

    // 图片链接
    private String PicUrl;
    private String MediaId;

    public String getPicUrl() {
        return PicUrl;
    }

    public void setPicUrl(String picUrl) {
        PicUrl = picUrl;
    }

    public String getMediaId() {
        return MediaId;
    }

    public void setMediaId(String mediaId) {
        MediaId = mediaId;
    }

}
