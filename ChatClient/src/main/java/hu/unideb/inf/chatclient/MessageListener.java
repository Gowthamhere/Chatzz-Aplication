package hu.unideb.inf.chatclient;

public interface MessageListener {
    public void onMessage(String fromUser, String messageBody);
}
