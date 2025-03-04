package com.cevague.ankiquiz;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.cevague.ankiquiz.ui.game.GameLoadingFragment;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);


        String cardSetString = getIntent().getStringExtra("cardSetString");

        getSupportFragmentManager().popBackStack("selection", FragmentManager.POP_BACK_STACK_INCLUSIVE);


        GameLoadingFragment fragment = new GameLoadingFragment();

        Bundle bundle = new Bundle();
        bundle.putString("cardSetString", cardSetString);
        fragment.setArguments(bundle);


        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();

    }
}