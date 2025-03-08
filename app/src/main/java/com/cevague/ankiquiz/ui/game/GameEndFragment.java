package com.cevague.ankiquiz.ui.game;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cevague.ankiquiz.R;
import com.cevague.ankiquiz.sql.CardModel;

import java.util.ArrayList;
import java.util.Collections;

public class GameEndFragment extends Fragment {

    ArrayList<CardModel> resultList;

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

        return view;
    }
}