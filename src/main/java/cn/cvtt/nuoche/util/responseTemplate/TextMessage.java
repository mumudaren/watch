package cn.cvtt.nuoche.util.responseTemplate;

/**
 * @decription TextMessage
 * <p>文本消息</p>
 * @author Yampery
 * @date 2018/3/8 13:27
 */
public class TextMessage extends BaseMessage {

    // 回复的消息内容
    private String Content;

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }
}
