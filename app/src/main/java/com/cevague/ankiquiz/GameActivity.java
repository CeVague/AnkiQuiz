package com.cevague.ankiquiz;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cevague.ankiquiz.ui.game.GameStartFragment;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);


        String cards_set = getIntent().getStringExtra("cards_set");

        getSupportFragmentManager().popBackStack("selection", FragmentManager.POP_BACK_STACK_INCLUSIVE);


        GameStartFragment fragment = new GameStartFragment();

        Bundle bundle = new Bundle();
        bundle.putString("cards_set", cards_set);
        fragment.setArguments(bundle);


        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();

    }
}