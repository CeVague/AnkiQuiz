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

        addFragment(new GameStartFragment(), false);

        String cards_set = getIntent().getStringExtra("cards_set");
    }

    private void addFragment(Fragment fragment, boolean addToBackstack){
        // Obtenir le FragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Commencer une transaction de fragment
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // Remplacer le contenu du FragmentContainerView par le fragment
        transaction.replace(R.id.fragment_container, fragment);

        if(addToBackstack){
            // Ajouter la transaction à la back stack (optionnel)
            transaction.addToBackStack(null);
        }

        // Valider la transaction
        transaction.commit();
    }
}