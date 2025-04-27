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
import android.widget.TextView;

import com.cevague.ankiquiz.R;
import com.cevague.ankiquiz.sql.CardModel;
import com.cevague.ankiquiz.sql.DBHelper;
import com.cevague.ankiquiz.sql.FileModel;
import com.cevague.ankiquiz.utils.ImgUtils;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class GameLoadingFragment extends Fragment {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private CountDownTimer countDown;
    private static final int MAX_BY_SESSION = 6; // Nombre max de carte a apprendre par séance
    private String cardSetString; // Liste des sets separés par une virgule

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Récupération de cardSetString
        if (getArguments() != null) {
            cardSetString = getArguments().getString("cardSetString");
        }
        assert cardSetString != null;
        Log.i("cardSetString", cardSetString);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_game_loading, container, false);


        CircularProgressBar progressBar = view.findViewById(R.id.circularCountDown);
        TextView textViewPB = view.findViewById(R.id.textViewCountDown);

        // Chargement asychrome des données
        Future<ArrayList<CardModel>> future = executeAsyncTaskWithFuture(getContext(), cardSetString);

        progressBar.setProgress(0);
        progressBar.setProgressMax(1000);

        // Compte a rebourd qui, a la fin, récupère le résultat du chargement
        countDown = new CountDownTimer(1000, 10) {
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
                try {
                    // Création du fragment de jeu bindé avec les cards
                    GameFragment fragment = GameFragment.newInstance(future.get());
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .commit();
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        };
        countDown.start();

        return view;
    }

    private Future<ArrayList<CardModel>> executeAsyncTaskWithFuture(Context context, String cards_set) {
        Callable<ArrayList<CardModel>> callableTask = () -> {

            ArrayList<CardModel> set = new ArrayList<>();
            try (DBHelper db = new DBHelper(context)) {
                // Pour chaque set (séparé par des ;) on ajouter toutes les cartes à faire
                for(String card_set : cards_set.split(";")){
                    set.addAll(db.getAllCardsBefore(card_set, Calendar.getInstance().getTime()));
                }

                // Limitation du nombre de card a apprendre en une session
                Collections.shuffle(set);
                set = new ArrayList<>(set.subList(0, Math.min(MAX_BY_SESSION, set.size())));
            }

            // set.removeIf(cm -> !cm.isTo_learn());

            // Chaque carte doit faire chaque jeux
            for(int i=0;i<set.size();i++){
                set.get(i).setGame_type(new boolean[]{true, true, true, true, true, true, true, true, true});
            }

            // Initialisation du cache
            ImgUtils.init();

            // Préchargement de toutes les images
            for(CardModel card : set){
                for(FileModel file : card.getImages()){
                    ImgUtils.preloadImageFromPath(file.getAbsolutePath(requireContext()));
                    ImgUtils.preloadScaledImageFromPath(file.getAbsolutePath(requireContext()), 300, 300);
                }
            }

            return set;
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