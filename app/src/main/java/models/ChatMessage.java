package models;

import java.util.Random;

/**
 * Created by viper on 17/09/16.
 */
public class ChatMessage {

    public String body, sender, receiver;
    public String msgid;
    public int type;

    public ChatMessage()
    {
        //empty constructor for firebase
    }

    public ChatMessage(String Sender, String Receiver, String messageString,
                       String ID, MessageType msgType) {
        body = messageString;
        sender = Sender;
        msgid = ID;
        receiver = Receiver;
        type = (msgType == MessageType.TEXT) ? 0 : 1;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setMsgID()
    {
        msgid += "-" + String.format("%02d", new Random().nextInt(100));
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ChatMessage)) {
            return false;
        }

        ChatMessage that = (ChatMessage) other;

        return this.body.equals(that.body) && this.sender.equals(that.sender) && this.receiver.equals(that.receiver) && this.msgid.equals(that.msgid) ;
    }

    @Override
    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 37 + this.body.hashCode();
        hashCode = hashCode * 37 + this.sender.hashCode();
        hashCode = hashCode * 37 + this.receiver.hashCode();
        return hashCode;
    }

    public enum MessageType {
        TEXT,
        IMAGE
    }
}