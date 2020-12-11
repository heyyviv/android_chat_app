package Message;

import java.util.ArrayList;

public class MessageObject {
    String message,
    messageID,senderID;
    ArrayList<String> mediaUrlList;
    public MessageObject(String messageID,String message,String senderID,ArrayList<String> mediaUrlList){
        this.message=message;
        this.messageID=messageID;
        this.senderID=senderID;
        this.mediaUrlList=mediaUrlList;
    }

    public String getMessage() {
        return message;
    }

    public String getMessageID() {
        return messageID;
    }

    public String getSenderID() {
        return senderID;
    }

    public ArrayList<String> getMediaUrlList() {
        return mediaUrlList;
    }
}
