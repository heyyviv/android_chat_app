package Chat;

import java.io.Serializable;
import java.util.ArrayList;

import User.UserObject;

public class ChatObject implements Serializable {
    private String chatID;

    private ArrayList<UserObject> userObjectArrayList=new ArrayList<>();

    public ChatObject(String chatID){
        this.chatID=chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public String getChatID() {
        return chatID;
    }

    public ArrayList<UserObject> getUserObjectArrayList() {
        return userObjectArrayList;
    }

    public void  addtoArrayList(UserObject temp){
        userObjectArrayList.add(temp);
    }
}
