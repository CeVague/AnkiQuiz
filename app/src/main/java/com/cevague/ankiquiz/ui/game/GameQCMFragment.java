package com.cevague.ankiquiz.ui.game;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import static com.cevague.ankiquiz.R.*;
import static com.cevague.ankiquiz.utils.ImgUtils.imageFromPath;
import static com.cevague.ankiquiz.utils.ImgUtils.scaledImageFromPath;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.cevague.ankiquiz.R;
import com.cevague.ankiquiz.sql.FileModel;
import com.cevague.ankiquiz.utils.AudioPlayer;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.util.ArrayList;
import java.util.Random;

public class GameQCMFragment extends GameFragmentListener { // implémente OnAnswerListener

    FileModel question, answer;
    ArrayList<FileModel> answerChoices;

    ImageView imgQuestion;
    TextView txtQuestion;
    ImageButton sndQuestion;
    CustomButton[] btnAnswers;
    int goodAnswer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_game_qcm, container, false);

        if (getArguments() != null) {
            question = getArguments().getParcelable("question");
            Log.d("GameQCMFragment", "Valeur reçue question : " + question);

            answer = getArguments().getParcelable("answer");
            Log.d("GameQCMFragment", "Valeur reçue answer : " + answer);

            answerChoices = getArguments().getParcelableArrayList("answerChoices");
            Log.d("GameQCMFragment", "Valeur reçue answerChoices : " + answerChoices);
        }

        if(question == null || answer == null){
            answerListener.onAnswer(true, false);
            return view;
        }

        // Initialisation des boutons et affichages

        imgQuestion = view.findViewById(R.id.imageViewQCMQuestion);
        imgQuestion.setVisibility(GONE);
        txtQuestion = view.findViewById(R.id.textViewQCMQuestion);
        txtQuestion.setVisibility(GONE);
        sndQuestion = view.findViewById(R.id.buttonQCMQuestion);
        sndQuestion.setVisibility(GONE);

        btnAnswers = new CustomButton[4];
        btnAnswers[0] = view.findViewById(R.id.buttonQCMAnswer1);
        btnAnswers[1] = view.findViewById(R.id.buttonQCMAnswer2);
        btnAnswers[2] = view.findViewById(R.id.buttonQCMAnswer3);
        btnAnswers[3] = view.findViewById(R.id.buttonQCMAnswer4);


        // Gestion de la question et de son affichage
        switch (question.getType()){
            case "jpg":
                Bitmap bitmap = BitmapFactory.decodeFile(question.getAbsolute_path());
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



        goodAnswer = new Random().nextInt(4);
        answerChoices.set(goodAnswer, answer);

        // Atribution à chaque bouton son role
        for(int i=0;i<4;i++){


            // Si les réponses sont des images, on veut un bouton img spécifiques
            if(answer.getType().equals("jpg")){
                // Convertir le chemin en Bitmap
                String path = answerChoices.get(i).getAbsolute_path();
                Bitmap smallBitmap = scaledImageFromPath(path, 400, 300);

                // L'appliquer au bouton
                btnAnswers[i].setImageBitmap(smallBitmap);

                // Au clic sur le bouton on charge la grosse image
                btnAnswers[i].setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Bitmap bitmap = imageFromPath(path);
                        showImagePopup(getContext(), bitmap);
                        return false;
                    }
                });

            // Si les réponses sont des sons, on veut un bouton im spécifiques
            }else if(answer.getType().equals("mp3")){

            // Si les réponses sont du texte, on veut un bouton classique
            }else{
                btnAnswers[i].setText(answerChoices.get(i).getAbsolute_path());
            }

            if(i == goodAnswer){
                btnAnswers[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        answerListener.onAnswer(true, true);
                        Animation zoomInAnimation = AnimationUtils.loadAnimation(getContext(), anim.clic);
                        v.startAnimation(zoomInAnimation);
                        answerAnimation();
                    }
                });
            }else{
                btnAnswers[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        answerListener.onAnswer(false, true);
                        Animation wizzAnimation = AnimationUtils.loadAnimation(getContext(), anim.wizz);
                        v.startAnimation(wizzAnimation);
                        answerAnimation();
                    }
                });
            }
        }


        return  view;
    }

    private void answerAnimation(){
        for(int i=0;i<4;i++){
            btnAnswers[i].setOnClickListener(null);
            if(i != goodAnswer){
                btnAnswers[i].setBackgroundColor(getResources().getColor(R.color.colorAccent));
            }else{
                btnAnswers[i].setBackgroundColor(getResources().getColor(color.colorPrimaryVariant));
            }

        }
    }

    private void showImagePopup(Context context, Bitmap bitmap) {
        Dialog dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar);
        dialog.setContentView(R.layout.dialog_fullscreen_image);

        SubsamplingScaleImageView imageView = dialog.findViewById(R.id.zoomableImageView);
        imageView.setImage(ImageSource.bitmap(bitmap));

        ImageButton imgBtn = dialog.findViewById(R.id.button_close);
        imgBtn.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

}