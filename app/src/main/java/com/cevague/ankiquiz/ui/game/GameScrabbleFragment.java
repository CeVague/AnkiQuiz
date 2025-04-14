package com.cevague.ankiquiz.ui.game;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.cevague.ankiquiz.R.color;
import static com.cevague.ankiquiz.R.id;
import static com.cevague.ankiquiz.R.layout;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.cevague.ankiquiz.R;
import com.cevague.ankiquiz.sql.FileModel;
import com.cevague.ankiquiz.utils.AudioPlayer;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameScrabbleFragment extends GameFragmentListener {

    FileModel question;
    String answer;
    ArrayList<FileModel> answerChoices;

    ImageView imgQuestion;
    TextView txtQuestion;
    ImageButton sndQuestion;

    TextView txtAnswer;

    int nbFalse;
    private final static int NB_MAX_FALSE = 3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(layout.fragment_game_scrabble, container, false);

        if (getArguments() != null) {
            question = getArguments().getParcelable("question");
            Log.d("GameScrabbleFragment", "Valeur reçue question : " + question);

            answer = ((FileModel) getArguments().getParcelable("answer")).getAbsolute_path();
            Log.d("GameScrabbleFragment", "Valeur reçue answer : " + answer);

            answerChoices = getArguments().getParcelableArrayList("answerChoices");
            Log.d("GameQCMFragment", "Valeur reçue answerChoices : " + answerChoices);
        }

        if(question == null || answer == null){
            answerListener.onAnswer(true, false);
            return view;
        }

        nbFalse = 0;

        // Initialisation des boutons et affichages
        imgQuestion = view.findViewById(id.imageViewScrabbleQuestion);
        imgQuestion.setVisibility(GONE);
        txtQuestion = view.findViewById(id.textViewScrabbleQuestion);
        txtQuestion.setVisibility(GONE);
        sndQuestion = view.findViewById(id.buttonScrabbleQuestion);
        sndQuestion.setVisibility(GONE);


        // Gestion de la question et de son affichage
        switch (question.getType()){
            case "jpg":
                Bitmap bitmap = imageFromPath(question.getAbsolute_path());
                imgQuestion.setImageBitmap(bitmap);
                imgQuestion.setVisibility(VISIBLE);
                imgQuestion.setOnClickListener(v -> showImagePopup(getContext(), bitmap));
                break;
            case "mp3":
                sndQuestion.setVisibility(VISIBLE);
                sndQuestion.setOnClickListener(v -> AudioPlayer.playAudio(getContext(), question.getAbsolute_path()));
                sndQuestion.callOnClick();
                break;
            default:
                txtQuestion.setText(question.getAbsolute_path());
                txtQuestion.setVisibility(VISIBLE);
        }



        txtAnswer = view.findViewById(id.answerTextScrabble);

        GridLayout gridLayout = view.findViewById(R.id.gridLayoutScrabble);

        int chunkSize = 1 + answer.length() / 8;
        int nbColumns = 7 - chunkSize;

        gridLayout.setColumnCount(nbColumns);

        List<String> listStrings = stringToStrings(answer, chunkSize);

        // Création d'une liste de mauvaises réponses
        List<String> otherCharacters = new ArrayList<>();
        for(FileModel file : answerChoices){
            otherCharacters.addAll(stringToStrings(file.getAbsolute_path(), chunkSize));
        }
        Collections.shuffle(otherCharacters);

        // Ajout de 50% de mauvaises réponses ou en avoir au moins 21
        listStrings.addAll(otherCharacters.subList(0, nbColumns * 4 - listStrings.size()));


        Collections.shuffle(listStrings);

        for (String str : listStrings) {
            Button btn = new Button(requireContext());
            btn.setAllCaps(false);
            btn.setText(String.valueOf(str));
            btn.setTextSize(25);

            // Optionnel : ID, style, onClick, etc.
            btn.setId(View.generateViewId());

            // Définir les paramètres de placement dans la grille
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f); // partage équitable de l’espace horizontal
            params.setMargins(8, 8, 8, 8);

            btn.setLayoutParams(params);
            gridLayout.addView(btn);

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean goodAnswer = clickLetter(v, str);
                    if(!goodAnswer){
                        Animation wizzAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.wizz);
                        v.startAnimation(wizzAnimation);
                        btn.setBackgroundColor(getResources().getColor(color.colorError));
                    }else{
                        Animation zoomInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.clic);
                        v.startAnimation(zoomInAnimation);
                        btn.setEnabled(false);
                        btn.setAlpha(0.5f);
                    }
                }
            });
        }

        return  view;
    }

    private void showImagePopup(Context context, Bitmap bitmap) {
        Dialog dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar);
        dialog.setContentView(layout.dialog_fullscreen_image);

        SubsamplingScaleImageView imageView = dialog.findViewById(id.zoomableImageView);
        imageView.setImage(ImageSource.bitmap(bitmap));

        ImageButton imgBtn = dialog.findViewById(id.button_close);
        imgBtn.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private static Bitmap imageFromPath(String path) {
        return BitmapFactory.decodeFile(path);
    }

    private static List<String> stringToStrings(String str, int chunkSize){
        List<String> troncons = new ArrayList<>();

        for(String word : str.split(" ")) {
            for (int i = 0; i < word.length(); i += chunkSize) {
                int fin = Math.min(i + chunkSize, word.length());
                troncons.add(word.substring(i, fin));
            }
        }

        return troncons;
    }

    public static char[] concatCharArrays(char[] a, char[] b) {
        char[] result = new char[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    // A chaque lettre, vérifie que ce soit une bonne réponse
    private boolean clickLetter(View v, String str){
        if(nbFalse >= NB_MAX_FALSE){
            return false;
        }

        String oldAnswer = txtAnswer.getText().toString();

        // Ajout des espaces s'il y en a besoin
        while(answer.substring(oldAnswer.length(), oldAnswer.length()+1).isBlank()){
            oldAnswer += " ";
        }

        // On crée le mot avec une lettre en plus
        String tempAnswer = oldAnswer + str;

        // Si c'est la réponse complette alors on a gagné
        if(tempAnswer.equals(answer)){
            answerListener.onAnswer(true, true);
            txtAnswer.setText(answer);
            return true;
        }

        // Si ce n'est pas le bon mot, on vérifie que les débuts soient identiques
        boolean goodAnswer = answer.startsWith(tempAnswer);

        // Si oui on met a jour le texte
        if(goodAnswer){
            txtAnswer.setText(tempAnswer);
            return true;
        }else{
            nbFalse++;
            if(nbFalse == NB_MAX_FALSE){
                answerListener.onAnswer(false, true);
                txtAnswer.setText(answer);
            }
            return false;
        }
    }

}