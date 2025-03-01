package com.cevague.ankiquiz.ui.welcome;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.cevague.ankiquiz.R;
import com.cevague.ankiquiz.ui.data.DataManagementFragment;

public class WelcomeFragment extends Fragment {

    Button buttonPlay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);

        buttonPlay = view.findViewById(R.id.button_play);

        startPulseAnimation(buttonPlay);

        return view;
    }


    private void startPulseAnimation(View view) {
        ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                view,
                PropertyValuesHolder.ofFloat("scaleX", 1.0f, 1.5f),
                PropertyValuesHolder.ofFloat("scaleY", 1.0f, 1.5f)
        );
        scaleDown.setDuration(700);
        scaleDown.setRepeatCount(ValueAnimator.INFINITE);
        scaleDown.setRepeatMode(ValueAnimator.REVERSE);
        scaleDown.start();
    }
}