package com.cevague.ankiquiz.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.widget.Toast;

import java.io.IOException;

public class AudioPlayer {
    private static MediaPlayer mediaPlayer;

    // Lire un fichier audio depuis le stockage interne
    public static void playAudio(Context context, String filePath) {
        try {
            stopAudio(); // Stoppe l'audio en cours s'il y en a un

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.start();

            // Afficher un Toast et libérer le MediaPlayer quand la lecture est terminée
            mediaPlayer.setOnCompletionListener(mp -> {
                stopAudio();
            });

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Erreur de lecture du fichier", Toast.LENGTH_SHORT).show();
        }
    }

    // Stopper la lecture
    public static void stopAudio() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    // Vérifier si un audio est en cours
    public static boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }
}