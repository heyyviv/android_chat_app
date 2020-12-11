package User;

import java.io.Serializable;

public class UserObject  implements Serializable {
    String name,phone,uid,notificationKey;
    Boolean isSelected=false;
    public  UserObject(String uid,String name,String phone){
        this.uid=uid;
        this.name=name;
        this.phone=phone;
    }

    public  UserObject(String uid){
        this.uid=uid;

    }

    public String getNotificationKey() {
        return notificationKey;
    }

    public void setNotificationKey(String notificationKey) {
        this.notificationKey = notificationKey;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }
}
