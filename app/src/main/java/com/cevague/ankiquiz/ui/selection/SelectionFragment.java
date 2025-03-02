package com.cevague.ankiquiz.ui.selection;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.cevague.ankiquiz.R;
import com.cevague.ankiquiz.sql.DBHelper;
import com.cevague.ankiquiz.ui.data.InfoRecyclerViewAdapter;

import java.util.ArrayList;

public class SelectionFragment extends Fragment {
    RecyclerView recyclerView;
    Button button_play;
    CardSetRecyclerViewAdapter adapter;
    ArrayList<String> list_card_set;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_selection, container, false);

        recyclerView = view.findViewById(R.id.recyclerView_selection);
        button_play = view.findViewById(R.id.button_play_selection);



        try (DBHelper db = new DBHelper(getContext())) {
            list_card_set = db.getAllCardSet();
        }


        adapter = new CardSetRecyclerViewAdapter(getContext(),list_card_set);
        adapter.setOnItemClickListener(new CardSetRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ArrayList<Boolean> list_selection) {


                // Charger l'animation depuis le fichier XML
                Animation slideUpAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);
                Animation slideDownAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_down);

                if(list_selection.contains(true) && (button_play.getVisibility() == View.INVISIBLE)){
                    button_play.startAnimation(slideUpAnimation);

                    // Rendre le bouton invisible après l'animation
                    slideUpAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationEnd(Animation animation) {
                        }

                        @Override
                        public void onAnimationStart(Animation animation) {
                            button_play.setVisibility(View.VISIBLE);

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {}
                    });
                }else if (!list_selection.contains(true)){
                    button_play.startAnimation(slideDownAnimation);

                    // Rendre le bouton invisible après l'animation
                    slideDownAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            button_play.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {}
                    });
                }
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);


        return view;
    }

    @Override
    public void onResume(){

        try (DBHelper db = new DBHelper(getContext())) {
            ArrayList<String> list_card_set = db.getAllCardSet();
        }

        adapter.notifyDataSetChanged();

        super.onResume();
    }


}