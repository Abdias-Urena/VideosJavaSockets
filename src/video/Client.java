/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package video;

/**
 *
 * @author Abdias
 */
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class Client {

    static Socket sfd = null;

    public static void main(String[] args) throws IOException {
        sfd = new Socket("192.168.100.14", 8000);
        InputStream inputStream = sfd.getInputStream();

        // Create a Swing video player
        JFrame frame = new JFrame("Video Player");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(640, 480);
        frame.setVisible(true);

        // Create a JFXPanel to initialize JavaFX
        JFXPanel fxPanel = new JFXPanel();
        frame.add(fxPanel);

        // Start JavaFX on the Swing EDT
        Platform.runLater(() -> {
            try {
                // Create a temporary file to save the video
                File videoFile = File.createTempFile("video", ".mp4");

                // Write the received video data to the temporary file
                FileOutputStream fileOutputStream = new FileOutputStream(videoFile);
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                }
                fileOutputStream.close();

                // Create a Media object with the temporary file
                Media media = new Media(videoFile.toURI().toString());

                // Create a MediaPlayer to play the video
                MediaPlayer mediaPlayer = new MediaPlayer(media);

                // Create a JavaFX VideoView to display the video
                javafx.scene.layout.Pane pane = new javafx.scene.layout.Pane();
                javafx.scene.media.MediaView mediaView = new javafx.scene.media.MediaView(mediaPlayer);
                pane.getChildren().add(mediaView);

                // Create a JavaFX Scene with the VideoView
                javafx.scene.Scene scene = new javafx.scene.Scene(pane, 640, 480);

                // Set the JavaFX Scene on the JFXPanel
                fxPanel.setScene(scene);

                // Play the video
                mediaPlayer.play();

                // Add a listener to close the socket and exit the application when the video ends
                mediaPlayer.setOnEndOfMedia(() -> {
                    try {
                        sfd.close();
                        videoFile.delete(); // Eliminar el archivo temporal
                        System.exit(0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
