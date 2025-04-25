package com.project.client.controllers;

import java.net.URL;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class BackgroundMusicController {
    private static BackgroundMusicController instance;
    private MediaPlayer mediaPlayer;

    private BackgroundMusicController() {
        URL musicFile = getClass().getResource("/music/sunnydays.mp3");
        Media media = new Media(musicFile.toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
    }

    public static BackgroundMusicController getInstance() {
        if (instance == null) {
            instance = new BackgroundMusicController();
        }
        return instance;
    }

    public void play() {
        if (mediaPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
            mediaPlayer.play();
        }
    }

    public void stop() {
        mediaPlayer.stop();
    }
}


