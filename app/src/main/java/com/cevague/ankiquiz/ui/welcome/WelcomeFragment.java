package com.cevague.ankiquiz.ui.welcome;

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

    Button buttonData, buttonPlay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);

        buttonData = view.findViewById(R.id.button_load);
        buttonPlay = view.findViewById(R.id.button_play);

        buttonData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an instance of the new fragment
                DataManagementFragment newFragment = new DataManagementFragment();

                // Get the FragmentManager and start a transaction
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                // Replace the current fragment with the new fragment
                transaction.replace(R.id.fragment_container, newFragment);

                // Add the transaction to the back stack (optional)
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
            }
        });

        return view;
    }
}