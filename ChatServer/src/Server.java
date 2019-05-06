import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {

    private final int serverPort;


    private ArrayList<Worker> workerList = new ArrayList<>();

    public Server(int serverPort) {
        this.serverPort = serverPort;
    }

    public List<Worker> getWorkerList() {
        return workerList;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(serverPort);
            while (true) {
                System.out.println("Waiting for Connection...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connected");
                Worker worker = new Worker(this, clientSocket);
                workerList.add(worker);
                worker.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeWorker(Worker worker) {
        workerList.remove(worker);
    }
}
