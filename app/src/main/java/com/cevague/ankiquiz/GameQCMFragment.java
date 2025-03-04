package com.cevague.ankiquiz;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cevague.ankiquiz.sql.FileModel;

import java.util.ArrayList;

public class GameQCMFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_game_qcm, container, false);

        if (getArguments() != null) {
            FileModel question = getArguments().getParcelable("question");
            Log.d("FragmentB", "Valeur reçue question : " + question);

            FileModel answer = getArguments().getParcelable("answer");
            Log.d("FragmentB", "Valeur reçue answer : " + answer);

            ArrayList<FileModel> answerChoices = getArguments().getParcelableArrayList("answerChoices");
            Log.d("FragmentB", "Valeur reçue answerChoices : " + answerChoices);
        }

        return  view;
    }
}