/*
 * To change this license header, choose License Headers in 
 * Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.utilities.*;
/**
 *
 * @author Abdias
 */
public class Server extends Thread {

    private ServerSocket serv = null;

    public Server() {
        try {
            this.serv = new ServerSocket(8000);
            System.out.println("Server opened");
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket client = this.serv.accept();
                System.out.println("User connected: " + 
                        client.getInetAddress());
                Thread t = new Thread(new ServerThread(client));
                t.start();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(
                        Level.SEVERE, null, ex);
            }

        }

    }
}