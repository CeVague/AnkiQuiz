package com.cevague.ankiquiz;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import static com.cevague.ankiquiz.R.*;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Parcel;
import android.os.Parcelable;
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

import com.cevague.ankiquiz.sql.FileModel;
import com.cevague.ankiquiz.utils.AudioPlayer;

import java.util.ArrayList;
import java.util.Random;

public class GameQCMFragment extends Fragment {


    public interface OnAnswerListener {
        void onAnswer(boolean win, boolean played);
    }

    private OnAnswerListener answerListener;

    public void setOnAnswerListener(OnAnswerListener listener){
        this.answerListener = listener;
    }

    FileModel question, answer;
    ArrayList<FileModel> answerChoices;

    ImageView imgQuestion;
    TextView txtQuestion;
    ImageButton sndQuestion;
    Button[] btnAnswers;
    ImageButton[] imgBtnAnswers;
    int goodAnswer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_game_qcm, container, false);

        if (getArguments() != null) {
            question = getArguments().getParcelable("question");
            Log.d("FragmentB", "Valeur reçue question : " + question);

            answer = getArguments().getParcelable("answer");
            Log.d("FragmentB", "Valeur reçue answer : " + answer);

            answerChoices = getArguments().getParcelableArrayList("answerChoices");
            Log.d("FragmentB", "Valeur reçue answerChoices : " + answerChoices);
        }

        if(question == null || answer == null){
            answerListener.onAnswer(true, false);
            return view;
        }



        imgQuestion = view.findViewById(R.id.imageViewQCMQuestion);
        imgQuestion.setVisibility(GONE);
        txtQuestion = view.findViewById(R.id.textViewQCMQuestion);
        txtQuestion.setVisibility(GONE);
        sndQuestion = view.findViewById(R.id.buttonQCMQuestion);
        sndQuestion.setVisibility(GONE);

        btnAnswers = new Button[4];
        btnAnswers[0] = view.findViewById(R.id.textButtonQCMAnswer1);
        btnAnswers[1] = view.findViewById(R.id.textButtonQCMAnswer2);
        btnAnswers[2] = view.findViewById(R.id.textButtonQCMAnswer3);
        btnAnswers[3] = view.findViewById(R.id.textButtonQCMAnswer4);

        imgBtnAnswers = new ImageButton[4];
        imgBtnAnswers[0] = view.findViewById(R.id.imageButtonQCMAnswer1);
        imgBtnAnswers[1] = view.findViewById(R.id.imageButtonQCMAnswer2);
        imgBtnAnswers[2] = view.findViewById(R.id.imageButtonQCMAnswer3);
        imgBtnAnswers[3] = view.findViewById(R.id.imageButtonQCMAnswer4);


        switch (question.getType()){

            case "jpg":
                Bitmap bitmap = BitmapFactory.decodeFile(question.getAbsolute_path());
                imgQuestion.setImageBitmap(bitmap);
                imgQuestion.setVisibility(VISIBLE);
                break;
            case "mp3":
                sndQuestion.setVisibility(VISIBLE);
                sndQuestion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AudioPlayer.playAudio(getContext(), question.getAbsolute_path());
                    }
                });
                break;
            default:
                txtQuestion.setText(question.getAbsolute_path());
                txtQuestion.setVisibility(VISIBLE);
        }


        int btnVisibility, imgVisibility;
        if(answer.getType().equals("img")){
            btnVisibility = GONE;
            imgVisibility = VISIBLE;
        }else{
            btnVisibility = VISIBLE;
            imgVisibility = GONE;
        }

        for(int i=0;i<4;i++){
            btnAnswers[i].setVisibility(btnVisibility);
            imgBtnAnswers[i].setVisibility(imgVisibility);
        }



        goodAnswer = new Random().nextInt(4);
        answerChoices.set(goodAnswer, answer);

        for(int i=0;i<4;i++){
            btnAnswers[i].setText(answerChoices.get(i).getAbsolute_path());

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

}