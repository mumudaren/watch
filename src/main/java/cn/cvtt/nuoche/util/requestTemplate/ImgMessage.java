package cn.cvtt.nuoche.util.requestTemplate;

/**
 * @decription TextMessage
 * <p>文本类消息</p>
 * @author Yampery
 * @date 2018/3/7 16:56
 */
public class ImgMessage extends BaseMessage {

    // 消息类型
    private ImageMessage2 Image;


    public ImageMessage2 getImage() {
        return Image;
    }

    public void setImage(ImageMessage2 image) {
        Image = image;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
