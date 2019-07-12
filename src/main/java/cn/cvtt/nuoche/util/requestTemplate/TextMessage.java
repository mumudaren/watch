package cn.cvtt.nuoche.util.requestTemplate;

/**
 * @decription TextMessage
 * <p>文本类消息</p>
 * @author Yampery
 * @date 2018/3/7 16:56
 */
public class TextMessage extends BaseMessage {

    // 消息内容
    private String Content;
    // 消息id，64位整型
    private long MsgId;

    public long getMsgId() {
        return MsgId;
    }

    public void setMsgId(long msgId) {
        MsgId = msgId;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
