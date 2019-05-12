/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.unideb.inf.chatserver;

/**
 *
 * @author Gowtam
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {

    private final int serverPort;
    private volatile boolean flag = true;
    ServerSocket serverSocket;
    Socket clientSocket;


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
            serverSocket = new ServerSocket(serverPort);
            while (flag) {
                System.out.println("Waiting for Connection...");
                try {
                    clientSocket = serverSocket.accept();
                    System.out.println("Connected");
                }catch(SocketException se){
                }
                if (clientSocket != null) {
                    Worker worker = new Worker(this, clientSocket);
                    workerList.add(worker);
                    worker.start();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void kill() throws IOException, SocketException {
        flag = false;
        serverSocket.close();
        System.out.println("Server Stopped Successfully");
    }

    public void removeWorker(Worker worker) {
        workerList.remove(worker);
    }
        
}
