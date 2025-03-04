package com.cevague.ankiquiz.ui.game;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.cevague.ankiquiz.GameQCMFragment;
import com.cevague.ankiquiz.R;
import com.cevague.ankiquiz.sql.CardModel;
import com.cevague.ankiquiz.sql.FileModel;
import com.cevague.ankiquiz.sql.InfoModel;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
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
    private static final String ARG_QUESTION = "liste_cartes";
    private static final String ARG_ANSWERS = "liste_cartes";
    private static final String ARG_SOLUTION = "liste_cartes";
    private static final int NB_TYPE = 1;

    private ArrayList<CardModel> cardList;
    private List<Pair<Integer, CardModel>> choiceList = new ArrayList<>();
    private Dictionary<CardModel, Boolean> resultDict = new Hashtable<>();
    private int idQuestion = 0;

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


        System.out.println(idQuestion);

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
            default:
                question = stringToFile(questionPair.second.getInfo().getName());
                answer = stringToFile(questionPair.second.getInfo().getName());
                answerChoices = getAnswerChoices(answerList, "name", 4);
                break;
        }

        GameQCMFragment fragment = new GameQCMFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("question", question);
        bundle.putParcelable("answer", answer);
        bundle.putParcelableArrayList("answerChoices", answerChoices);
        fragment.setArguments(bundle);

        // Ajouter le fragment enfant
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null); // Permet de revenir au fragment précédent en appuyant sur retour
            transaction.commit();
        }



        idQuestion++;

        return view;
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
        int i = new Random().nextInt(list.size());
        return list.get(i);
    }
}