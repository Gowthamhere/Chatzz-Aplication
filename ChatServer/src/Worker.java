import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class Worker extends Thread {

    private final Socket clientSocket;
    private final Server server;
    private String login = null;
    private OutputStream outputStream;

    public Worker(Server server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            handleClientSocket();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void handleClientSocket() throws IOException, InterruptedException {
        InputStream inputStream = clientSocket.getInputStream();
        this.outputStream = clientSocket.getOutputStream();

        outputStream.write("HellO!!! Please Enter Something: ".getBytes());
        BufferedReader scan = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while((line = scan.readLine()) != null){
            String[] tokens = StringUtils.split(line);
            if (tokens != null && tokens.length > 0) {
                String cmd = tokens[0];
                if (cmd.equalsIgnoreCase("logoff")) {
                    logoffHandler();
                    break;
                } else if (cmd.equalsIgnoreCase("login")) {
                    loginHandler(outputStream, tokens);
                } else{
                    String msg = "You Typed: " + line + "\n";
                    outputStream.write(msg.getBytes());
                }
            }
        }

    }

    private void logoffHandler() throws IOException, InterruptedException {
        outputStream.write("See you Again".getBytes());
        String offlineMsg = "offline " + login + "\n";
        List<Worker> workerList = server.getWorkerList();
        for (Worker worker : workerList) {
            if (!login.equals(worker.getLogin())) {
                worker.send(offlineMsg);
            }
        }
        Worker.sleep(1000);
        clientSocket.close();
        System.out.println("User Logged off: " + login);
        System.out.println("Connection Disconnected");

    }


    public String getLogin() {
        return login;
    }

    private void loginHandler(OutputStream outputStream, String[] tokens) throws IOException {
        if (tokens.length == 3) {
            String login = tokens[1];
            String password = tokens[2];

            if (login.equals("test") && password.equals("test") || (login.equals("test1") && password.equals("test"))) {
                String msg = "Hello Guest!!!!";
                outputStream.write(msg.getBytes());
                this.login = login;
                System.out.println("User Logged in succesfully: " + login);

                String onlineMsg = "online " + login + "\n";
                List<Worker> workerList = server.getWorkerList();

                for (Worker worker : workerList) {
                    if (!login.equals(worker.getLogin())) {
                        worker.send(onlineMsg);
                    }
                }

                for (Worker worker : workerList) {
                    if (worker.getLogin() != null) {
                        if (!login.equals(worker.getLogin())) {
                            String msg2 = "online " + worker.getLogin() + "\n";
                            send(msg2);
                        }
                    }
                }

            } else {
                String msg = "Login Failed";
                outputStream.write(msg.getBytes());
            }
        }
    }

    private void send(String msg) throws IOException {
        if (login != null) {
            outputStream.write(msg.getBytes());
        }

    }
}
