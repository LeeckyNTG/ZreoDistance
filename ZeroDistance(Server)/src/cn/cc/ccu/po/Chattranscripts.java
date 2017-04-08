package cn.cc.ccu.po;

public class Chattranscripts {
    private Integer id;

    private String sendnumber;

    private String receivenumber;

    private String sendtime;

    private String message;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSendnumber() {
        return sendnumber;
    }

    public void setSendnumber(String sendnumber) {
        this.sendnumber = sendnumber == null ? null : sendnumber.trim();
    }

    public String getReceivenumber() {
        return receivenumber;
    }

    public void setReceivenumber(String receivenumber) {
        this.receivenumber = receivenumber == null ? null : receivenumber.trim();
    }

    public String getSendtime() {
        return sendtime;
    }

    public void setSendtime(String sendtime) {
        this.sendtime = sendtime == null ? null : sendtime.trim();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message == null ? null : message.trim();
    }
}