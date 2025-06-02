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
import android.widget.Toast;

import com.cevague.ankiquiz.BuildConfig;
import com.cevague.ankiquiz.R;
import com.cevague.ankiquiz.sql.DBHelper;
import com.cevague.ankiquiz.ui.selection.SelectionFragment;

import java.util.Calendar;

public class WelcomeFragment extends Fragment {

    Button buttonPlay, buttonReset;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);

        buttonPlay = view.findViewById(R.id.button_play);
        buttonReset = view.findViewById(R.id.button_reset);

        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new SelectionFragment();
                // Obtenir le FragmentManager
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                // Commencer une transaction de fragment
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                // Remplacer le contenu du FragmentContainerView par le fragment
                transaction.replace(R.id.fragment_container, fragment);

                // Ajouter la transaction à la back stack (optionnel)
                transaction.addToBackStack("selection");

                // Valider la transaction
                transaction.commit();
            }
        });

        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try (DBHelper db = new DBHelper(getContext())) {
                    long nb = db.resetNTCard(Calendar.getInstance().getTime());
                    Toast.makeText(getContext(), String.valueOf(nb), Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (!BuildConfig.DEBUG) {
            buttonReset.setVisibility(View.GONE);
            startPulseAnimation(view);
        }

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