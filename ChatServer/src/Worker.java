import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;

public class Worker extends Thread {

    private final Socket clientSocket;

    public Worker(Socket clientSocket) {
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
        OutputStream outputStream = clientSocket.getOutputStream();

        outputStream.write("HellO!!! Please Enter Something: ".getBytes());
        BufferedReader scan = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while((line = scan.readLine()) != null){
            String[] tokens = StringUtils.split(line);
            if (tokens != null && tokens.length > 0) {
                String cmd = tokens[0];
                if (cmd.equalsIgnoreCase("exit")) {
                    outputStream.write("See you Again".getBytes());
                    break;
                }
                else{
                    String msg = "You Typed: " + line + "\n";
                    outputStream.write(msg.getBytes());
                }
            }
        }
            Worker.sleep(1000);
        clientSocket.close();
        System.out.println("Connection Disconnected");
    }
}
