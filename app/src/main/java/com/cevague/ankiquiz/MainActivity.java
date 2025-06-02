package com.cevague.ankiquiz;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cevague.ankiquiz.ui.data.DataManagementFragment;
import com.cevague.ankiquiz.ui.welcome.WelcomeFragment;
import com.cevague.ankiquiz.utils.ImgUtils;

public class MainActivity extends AppCompatActivity {

    ImageButton toolbarButton;

    DrawerArrowDrawable drawerArrowDrawable;
    boolean isBackArrow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImgUtils.init();

        toolbarButton = findViewById(R.id.toolbar_button);
        drawerArrowDrawable = new DrawerArrowDrawable(this);
        drawerArrowDrawable.setColor(ContextCompat.getColor(this, R.color.colorOnPrimary));
        toolbarButton.setImageDrawable(drawerArrowDrawable);

        drawerArrowDrawable.setProgress(0f);

        activerModeMenu();

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (currentFragment instanceof WelcomeFragment) {
                activerModeMenu(); // Tu es sur l'accueil
            } else {
                activerModeRetour(); // Tu es ailleurs
            }
        });

        addFragment(new WelcomeFragment(), false);
    }

    // Exemple : switcher vers mode retour
    void activerModeRetour() {
        if(!isBackArrow){
            animateToBackArrow();
        }
        toolbarButton.setOnClickListener(v -> onBackPressed());
    }

    // Exemple : switcher vers mode menu
    void activerModeMenu() {
        if(isBackArrow){
            animateToMenuIcon();
        }
        toolbarButton.setOnClickListener(v -> {
            // Affiche un menu
            PopupMenu popup = new PopupMenu(this, v);
            popup.getMenuInflater().inflate(R.menu.main_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.manage_data) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    // Trouver le fragment actuellement attaché au FragmentContainerView
                    Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);

                    // Vérifier qu'il ne soit pas déjà affiché
                    if (! (currentFragment instanceof DataManagementFragment)) {
                        addFragment(new DataManagementFragment());
                    }
                    return true;
                }
                return false;
            });
            popup.show();
        });
    }

    private void animateToBackArrow() {
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.addUpdateListener(animation -> {
            float progress = (float) animation.getAnimatedValue();
            drawerArrowDrawable.setProgress(progress);
        });
        animator.setDuration(300);
        animator.start();
        isBackArrow = true;
    }

    private void animateToMenuIcon() {
        ValueAnimator animator = ValueAnimator.ofFloat(1f, 0f);
        animator.addUpdateListener(animation -> {
            float progress = (float) animation.getAnimatedValue();
            drawerArrowDrawable.setProgress(progress);
        });
        animator.setDuration(300);
        animator.start();
        isBackArrow = false;
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