/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;

/**
 *
 * @author Abdias
 */
public class VideoThread implements Runnable {

    private final Socket clientSocket;

    public VideoThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = clientSocket.getInputStream();
            OutputStream outputStream = clientSocket.getOutputStream();

            // Read the video file from disk
            File videoFile = new File("prueba2.mp4");
            System.out.println(videoFile.getAbsolutePath());
            FileInputStream fileInputStream = new FileInputStream(videoFile);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                try {
                    outputStream.write(buffer, 0, bytesRead);
                } catch (IOException e) {
                    // Error de escritura en el socket, intentar retransmitir el fragmento
                    boolean success = false;
                    while (!success) {
                        try {
                            outputStream.write(buffer, 0, bytesRead);
                            success = true;
                        } catch (IOException ex) {
                            // Error de escritura nuevamente, esperar un breve período de tiempo antes de reintentar
                            Thread.sleep(1000);
                        }
                    }
                }
            }

            // Clean up resources
            fileInputStream.close();
            outputStream.close();

            System.out.println("Video enviado al cliente: " + clientSocket.getInetAddress().getHostAddress());
            clientSocket.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
