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
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class GameLoadingFragment extends Fragment {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private CountDownTimer countDown;

    private String cardSetString;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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




        Future<ArrayList<CardModel>> future = executeAsyncTaskWithFuture(getContext(), cardSetString);



        progressBar.setProgress(0);
        progressBar.setProgressMax(1000);

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

    public static GameFragment newInstance(ArrayList<CardModel> liste) {
        GameFragment fragment = new GameFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("card_list", liste);
        fragment.setArguments(args);
        return fragment;
    }

    private Future<ArrayList<CardModel>> executeAsyncTaskWithFuture(Context context, String cards_set) {
        Callable<ArrayList<CardModel>> callableTask = () -> {

            ArrayList<CardModel> set = new ArrayList<CardModel>();
            try (DBHelper db = new DBHelper(context)) {
                for(String card_set : cards_set.split(";")){
                    set.addAll(db.getAllCardsBefore(card_set, Calendar.getInstance().getTime()));
                }
            }

            // set.removeIf(cm -> !cm.isTo_learn());

            for(int i=0;i<set.size();i++){
                set.get(i).setGame_type(new boolean[]{true, true, true, true, true, true, true, true, true});
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