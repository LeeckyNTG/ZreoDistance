package cn.cc.ccu.po;

public class Friend {
    private Integer id;

    private String usernumber;

    private String friendnumber;

    private String groupid;

    private String notes;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsernumber() {
        return usernumber;
    }

    public void setUsernumber(String usernumber) {
        this.usernumber = usernumber == null ? null : usernumber.trim();
    }

    public String getFriendnumber() {
        return friendnumber;
    }

    public void setFriendnumber(String friendnumber) {
        this.friendnumber = friendnumber == null ? null : friendnumber.trim();
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid == null ? null : groupid.trim();
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes == null ? null : notes.trim();
    }
}