package models;

import java.util.Random;

/**
 * Created by viper on 17/09/16.
 */
public class ChatMessage {

    public String body, sender, receiver;
    public String Date, Time;
    public String msgid;

    public ChatMessage()
    {
        //empty constructor for firebase
    }

    public ChatMessage(String Sender, String Receiver, String messageString,
                       String ID) {
        body = messageString;
        sender = Sender;
        msgid = ID;
        receiver = Receiver;
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
}