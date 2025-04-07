package com.cevague.ankiquiz.ui.game;

import static android.view.View.INVISIBLE;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.cevague.ankiquiz.R;
import com.cevague.ankiquiz.sql.CardModel;
import com.cevague.ankiquiz.sql.FileModel;
import com.cevague.ankiquiz.utils.AudioPlayer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameFragment extends Fragment {
    private static final String ARG_LISTE_CARTES = "liste_cartes";
    private static final int NB_TYPE = 3;

    private ArrayList<CardModel> cardList;
    private List<Pair<Integer, CardModel>> choiceList = new ArrayList<>();
    private Dictionary<CardModel, Boolean> resultDict = new Hashtable<>();
    private Dictionary<CardModel, Boolean> doneDict = new Hashtable<>();
    private int idQuestion = 0;

    private Button btnNext;
    private ImageButton btnClose;

    public static GameFragment newInstance(ArrayList<CardModel> cardList) {
        GameFragment fragment = new GameFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_LISTE_CARTES, cardList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            cardList = getArguments().getParcelableArrayList(ARG_LISTE_CARTES);

            for(CardModel card : cardList){
                for(int i = 0; i < NB_TYPE; i++){
                    choiceList.add(new Pair<>(i, card));
                }
                resultDict.put(card, Boolean.TRUE);
                doneDict.put(card, Boolean.FALSE);
            }

            Collections.shuffle(choiceList);
        }else{
            throw new RuntimeException("No DataModel list gave in argument");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_game, container, false);

        btnNext = view.findViewById(R.id.button_next_game);
        btnClose = view.findViewById(R.id.button_close);

        btnClose.setOnClickListener(v -> startEndGame());

        idQuestion = 0;

        nextQuestion();

        return view;
    }

    private void nextQuestion(){
        System.out.println(idQuestion);

        GameQCMFragment fragment = new GameQCMFragment();
        Bundle bundle = getNextBundle();

        if(bundle == null){
            startEndGame();
            return ;
        }

        fragment.setArguments(bundle);

        fragment.setOnAnswerListener(new GameQCMFragment.OnAnswerListener() {
            @Override
            public void onAnswer(boolean win, boolean played) {
                if(played){
                    CardModel card = choiceList.get(idQuestion).second;
                    boolean old = resultDict.get(card);
                    resultDict.put(card, old && win);

                    doneDict.put(card, Boolean.TRUE);

                    setButtonNext();
                }else{
                    idQuestion++;
                    nextQuestion();
                }
            }
        });

        // Ajouter le fragment enfant
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container_game, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    private FileModel stringToFile(String txt){
        return new FileModel(-1, -1, null, txt, txt, "txt");
    }

    private ArrayList<CardModel> getAnswerList(ArrayList<CardModel> cardList, CardModel question){
        ArrayList<CardModel> tmp = new ArrayList<CardModel>(cardList);
        tmp.remove(question);
        Collections.shuffle(tmp);
        return tmp;
    }

    private ArrayList<FileModel> getAnswerChoices(ArrayList<CardModel> cardList, String type, int nb){
        while(cardList.size() < nb){
            cardList.addAll(cardList);
        }

        ArrayList<FileModel> answerChoices = new ArrayList<>();

        for(int i=0;i<nb;i++){
            FileModel tmp;
            if(type.equals("mp3")){
                tmp = getRandomElement(cardList.get(i).getAudios());
            }else if(type.equals("jpg")){
                tmp = getRandomElement(cardList.get(i).getImages());
            }else if(type.equals("txt")){
                tmp = getRandomElement(cardList.get(i).getTexts());
            }else{
                tmp = stringToFile(cardList.get(i).getInfo().getName());
            }
            answerChoices.add(tmp);
        }


        return answerChoices;
    }

    private <T> T getRandomElement(ArrayList<T> list){
        if(list == null || list.isEmpty()){
            return null;
        }
        int i = new Random().nextInt(list.size());
        return list.get(i);
    }

    private Bundle getNextBundle(){
        if(idQuestion >= choiceList.size()){
            return null;
        }

        // Génération de la question et de ses réponses

        // Get the ieme question of the set
        Pair<Integer, CardModel> questionPair = choiceList.get(idQuestion);
        // Get all the answer card possible (shuffle)
        ArrayList<CardModel> answerList = getAnswerList(cardList, questionPair.second);

        FileModel question;
        FileModel answer;
        ArrayList<FileModel> answerChoices;

        switch (questionPair.first){
            case 0:
                question = getRandomElement(questionPair.second.getAudios());
                answer = stringToFile(questionPair.second.getInfo().getName());
                answerChoices = getAnswerChoices(answerList, "name", 4);
                break;
            case 1:
                question = getRandomElement(questionPair.second.getImages());
                answer = stringToFile(questionPair.second.getInfo().getName());
                answerChoices = getAnswerChoices(answerList, "name", 4);
                break;
            case 2:
                question = getRandomElement(questionPair.second.getTexts());
                answer = stringToFile(questionPair.second.getInfo().getName());
                answerChoices = getAnswerChoices(answerList, "name", 4);
                break;
            default:
                question = stringToFile(questionPair.second.getInfo().getName());
                answer = stringToFile(questionPair.second.getInfo().getName());
                answerChoices = getAnswerChoices(answerList, "name", 4);
                break;
        }

        Bundle bundle = new Bundle();
        bundle.putParcelable("question", question);
        bundle.putParcelable("answer", answer);
        bundle.putParcelableArrayList("answerChoices", answerChoices);

        return bundle;
    }

    private void setButtonNext(){
        Animation slideUpAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);
        btnNext.startAnimation(slideUpAnimation);

        // Rendre le bouton invisible après l'animation
        slideUpAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationStart(Animation animation) {
                btnNext.setVisibility(View.VISIBLE);

            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idQuestion++;
                btnNext.setVisibility(INVISIBLE);
                AudioPlayer.stopAudio();

                if(idQuestion >= choiceList.size()){
                    Log.i("Boutton next", "Plus aucune question");
                    startEndGame();
                }else{
                    nextQuestion();
                }
            }
        });
    }

    private void startEndGame(){
        ArrayList<CardModel> resultList = new ArrayList<>();
        for(CardModel card : cardList){
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            if(resultDict.get(card) && doneDict.get(card)) {
                int level = card.getLevel() + 1;
                if (level == 1) {
                    c.add(Calendar.DATE, 1);
                } else if (level == 2) {
                    c.add(Calendar.DATE, 2);
                } else if (level == 3) {
                    c.add(Calendar.DATE, 4);
                } else if (level == 4) {
                    c.add(Calendar.DATE, 7);
                } else if (level == 5) {
                    c.add(Calendar.DATE, 14);
                } else {
                    c.add(Calendar.DATE, 28);
                }
                card.setNext_time(c.getTime());
                card.setLevel(level);
                card.setWin(1);
            }else if(doneDict.get(card)){
                card.setNext_time(c.getTime());
                card.setLevel(0);
                card.setWin(-1);
            }else{
                card.setWin(0);
            }
            resultList.add(card);
        }

        AudioPlayer.stopAudio();

        GameEndFragment endFragment = new GameEndFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("resultList", resultList);
        endFragment.setArguments(bundle);

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, endFragment);
        transaction.commit();

    }
}