package com.cevague.ankiquiz.ui.game;

import androidx.fragment.app.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cevague.ankiquiz.R;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class GameStartFragment extends Fragment {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private CountDownTimer countDown;

    private String cards_set;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cards_set = getArguments().getString("cards_set");
            Log.d("Fragment", "Message reçu : " + cards_set);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_game_start, container, false);


        CircularProgressBar progressBar = view.findViewById(R.id.circularCountDown);
        TextView textViewPB = view.findViewById(R.id.textViewCountDown);




        Future<String> future = executeAsyncTaskWithFuture(getContext(), cards_set);



        progressBar.setProgress(0);
        progressBar.setProgressMax(1000);

        countDown = new CountDownTimer(4000, 10) { // 3 secondes, tick toutes les secondes
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
                progressBar.setProgress(0);

                Toast.makeText(view.getContext(), "Chargement terminé !", Toast.LENGTH_SHORT).show();
                try {
                    Toast.makeText(view.getContext(), future.get(), Toast.LENGTH_SHORT).show();
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        };
        countDown.start();





        return view;
    }

    private Future<String> executeAsyncTaskWithFuture(Context context, String cards_set) {
        Callable<String> callableTask = () -> {
            // Simuler un travail long
            try {
                Thread.sleep(8000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Résultat de la tâche pour : " + cards_set;
        };

        return executorService.submit(callableTask);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Arrêter l'executor lorsque l'activité est détruite
        executorService.shutdown();
        countDown.cancel();
    }

}