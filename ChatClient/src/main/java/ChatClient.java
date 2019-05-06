package main.java;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ChatClient {
    private final int serverPort;
    private final String serverName;
    private Socket socket;
    private InputStream serverIn;
    private OutputStream serverOut;
    private BufferedReader bufferedIn;

    private ArrayList<UserStatusListener> userStatusListeners = new ArrayList<>();
    private ArrayList<MessageListener> messageListeners = new ArrayList<>();

    public ChatClient(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
    }

    public static void main(String[] args) throws IOException {
        ChatClient client = new ChatClient("localhost", 8080);
        client.addUserStatusListener(new UserStatusListener() {
            @Override
            public void online(String login) {
                System.out.println("ONLINE: " + login);
            }

            @Override
            public void offline(String login) {
                System.out.println("OFFLINE: " + login);
            }
        });

        client.addMessageListener((fromUser, messageBody) -> System.out.println("You got a Message from " + fromUser + "---> " + messageBody));


        if (!client.connect()) {
            System.err.println("Connect failed");
        } else {
            System.out.println("Connect Successful");


            if (client.login("test", "test")) {
                System.out.println("Login Successful");

                client.msg("test1", "Hello Test1");
            } else {
                System.err.println("Login Failed");
            }

            //           client.logoff();

        }
    }

    public void msg(String sendTo, String messageBody) throws IOException {
        String cmd = "msg " + sendTo + " " + messageBody + "\n";
        serverOut.write(cmd.getBytes());
    }

    public void logoff() throws IOException {
        String cmd = "logoff\n";
        serverOut.write(cmd.getBytes());
        serverOut.flush();
    }

    public boolean login(String login, String password) throws IOException {
        String cmd = "login " + login + " " + password + "\n";
        serverOut.write(cmd.getBytes());
        String response = bufferedIn.readLine();
        System.out.println("Response Line: " + response);

        if (response.equalsIgnoreCase("login successful")) {
            startMessageReader();
//                socket.close();
            return true;
        } else {
            socket.close();
            return false;
        }
    }

    private void startMessageReader() {
        Thread t = new Thread() {
            @Override
            public void run() {
                readMessageLoop();
            }
        };
        t.start();
    }

    private void readMessageLoop() {
        try {
            String line;
            while ((line = bufferedIn.readLine()) != null) {
                String[] tokens = StringUtils.split(line);
                if (tokens != null && tokens.length > 0) {
                    String cmd = tokens[0];
                    if ("online".equalsIgnoreCase(cmd)) {
                        onlineHandler(tokens);
                    } else if ("offline".equalsIgnoreCase(cmd)) {
                        offlineHandler(tokens);
                    } else if ("msg".equalsIgnoreCase(cmd)) {
                        String[] tokenMsg = StringUtils.split(line, null, 3);
                        messageHandler(tokenMsg);
                    }


                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void messageHandler(String[] tokensMsg) {
        String login = tokensMsg[1];
        String messageBody = tokensMsg[2];
        for (MessageListener mlistener : messageListeners) {
            mlistener.onMessage(login, messageBody);
        }


    }

    private void offlineHandler(String[] tokens) {
        String login = tokens[1];
        for (UserStatusListener listener : userStatusListeners) {
            listener.offline(login);
        }
    }

    private void onlineHandler(String[] tokens) {
        String login = tokens[1];
        for (UserStatusListener listener : userStatusListeners) {
            listener.online(login);
        }

    }

    public boolean connect() {
        try {
            this.socket = new Socket(serverName, serverPort);
            this.serverOut = socket.getOutputStream();
            this.serverIn = socket.getInputStream();
            this.bufferedIn = new BufferedReader(new InputStreamReader(serverIn));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void addUserStatusListener(UserStatusListener listener) {
        userStatusListeners.add(listener);
    }

    public void removeUserStatusListener(UserStatusListener listener) {
        userStatusListeners.remove(listener);
    }

    public void addMessageListener(MessageListener mlistener) {
        messageListeners.add(mlistener);
    }

    public void removeMessageListener(MessageListener mlistener) {
        messageListeners.remove(mlistener);
    }
}
