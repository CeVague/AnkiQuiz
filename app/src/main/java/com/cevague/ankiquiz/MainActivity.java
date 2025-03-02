package com.cevague.ankiquiz;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cevague.ankiquiz.ui.data.DataManagementFragment;
import com.cevague.ankiquiz.ui.welcome.WelcomeFragment;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        addFragment(new WelcomeFragment(), false);
    }

    // Créer le menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    // Gérer les clics sur le menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.manage_data){

            FragmentManager fragmentManager = getSupportFragmentManager();
            // Trouver le fragment actuellement attaché au FragmentContainerView
            Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);

            // Vérifier qu'il ne soit pas déjà affiché
            if (! (currentFragment instanceof DataManagementFragment)) {
                addFragment(new DataManagementFragment());
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addFragment(Fragment fragment){
        addFragment(fragment, true);
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