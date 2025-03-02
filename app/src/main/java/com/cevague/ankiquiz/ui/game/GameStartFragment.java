package com.cevague.ankiquiz.ui.game;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cevague.ankiquiz.R;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;


public class GameStartFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_game_start, container, false);


        CircularProgressBar progressBar = view.findViewById(R.id.circularCountDown);
        TextView textViewPB = view.findViewById(R.id.textViewCountDown);

        progressBar.setProgress(0);
        progressBar.setProgressMax(1000);

        new CountDownTimer(4000, 10) { // 3 secondes, tick toutes les secondes
            public void onTick(long millisUntilFinished) {
                int progress = (int) (millisUntilFinished +1000) % 2000 ;
                progressBar.setProgress(Math.abs(1000-progress));

                int orientation = (int) millisUntilFinished / 1000 ;
                if(orientation % 2 == 0){
                    progressBar.setProgressDirection(CircularProgressBar.ProgressDirection.TO_RIGHT);
                }else{
                    progressBar.setProgressDirection(CircularProgressBar.ProgressDirection.TO_LEFT);
                }

                textViewPB.setText(String.valueOf((int) millisUntilFinished / 1000));
            }

            public void onFinish() {
                Toast.makeText(view.getContext(), "Chargement terminé !", Toast.LENGTH_SHORT).show();
                progressBar.setProgress(0);
            }
        }.start();

        return view;
    }

}