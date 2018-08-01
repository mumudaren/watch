package cn.cvtt.nuoche.util.responseTemplate;


/** 链接信息
 *   @author   mingxingchen  3-09
 * */
public class LinkMessage   extends  BaseMessage {

    private  String  Title;
    private  String  Description;
    private  String  Url;
    private  String  MsgId;

    public LinkMessage(){

    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getMsgId() {
        return MsgId;
    }

    public void setMsgId(String msgId) {
        MsgId = msgId;
    }
}
