/*
 * To change this license header, choose License Headers in 
 * Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.utilities;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import video.Videos;

/**
 *
 * @author Abdias
 */
public class ServerThread implements Runnable {

    private final Socket clientSocket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    public ArrayList<Videos> videos = new ArrayList();

    public ServerThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
        try {
            this.inputStream = new DataInputStream(new BufferedInputStream(
                    clientSocket.getInputStream()));
            this.outputStream = new DataOutputStream(new BufferedOutputStream(
                    clientSocket.getOutputStream()));
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
    }

    public void fillVideos() {
        String currentWorkingDir = System.getProperty("user.dir");
        File folder = new File(currentWorkingDir);
        if (folder.isDirectory()) {
            File[] videoFiles = folder.listFiles();
            if (videoFiles != null) {
                for (File vid : videoFiles) {
                    if (vid.isFile()) {
                        videos.add(new Videos(vid.getAbsolutePath()));
                    }
                }
            }
        }
    }

    public void videoList() {
        String container = "";
        for (Videos video : videos) {
            if (video.getType().equals("mp4")) {
                container += video.toString() + "\n";
            }
        }
        try {
            this.outputStream.writeUTF(container);
            this.outputStream.flush();
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(
                    Level.SEVERE, null, ex);
        }

    }

    @Override
    public void run() {
        fillVideos();
        videoList();
        try {
            String receiveClient = inputStream.readUTF();
            System.out.println(receiveClient);
            if (!receiveClient.isEmpty()) {
                try {
                    // Read the video file from disk
                    File videoFile = new File(receiveClient + ".mp4");
                    System.out.println(videoFile.getAbsolutePath());
                    FileInputStream fileInputStream = 
                            new FileInputStream(videoFile);
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        try {
                            outputStream.write(buffer, 0, bytesRead);
                        } catch (IOException e) {
                            // Error de escritura en el socket, intentar 
                            // retransmitir el fragmento
                            boolean success = false;
                            while (!success) {
                                try {
                                    outputStream.write(buffer, 0, bytesRead);
                                    success = true;
                                } catch (IOException ex) {
                                    // Error de escritura nuevamente, 
                                    //esperar un breve período de tiempo
                                    //antes de reintentar
                                    Thread.sleep(1000);
                                }
                            }
                        }
                    }

                    // Clean up resources
                    fileInputStream.close();
                    outputStream.close();

                    System.out.println("Video enviado al cliente: " + 
                            clientSocket.getInetAddress().getHostAddress());
                    clientSocket.close();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
    }

}
