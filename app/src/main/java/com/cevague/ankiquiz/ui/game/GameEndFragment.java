package com.cevague.ankiquiz.ui.game;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.cevague.ankiquiz.R;
import com.cevague.ankiquiz.sql.CardModel;
import com.cevague.ankiquiz.sql.DBHelper;

import java.util.ArrayList;
import java.util.Collections;

public class GameEndFragment extends Fragment {

    ArrayList<CardModel> resultList;
    CardRecyclerViewAdapter adapter;
    RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            resultList = getArguments().getParcelableArrayList("resultList");
        }else{
            throw new RuntimeException("No resultList gave in argument");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_end, container, false);

        recyclerView = view.findViewById(R.id.recyclerView_end_game);

        adapter = new CardRecyclerViewAdapter(getContext(), resultList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        ImageButton closeButton = view.findViewById(R.id.button_close);
        closeButton.setOnClickListener(v -> requireActivity().onBackPressed());

        // Enregistrement des modification des cards
        try (DBHelper db = new DBHelper(getContext())) {
            for (CardModel card : resultList) {
                db.updateCard(card);
            }
        }
        return view;
    }
}