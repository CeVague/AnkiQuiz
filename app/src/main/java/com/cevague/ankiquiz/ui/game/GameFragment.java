package com.cevague.ankiquiz.ui.game;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;

import com.cevague.ankiquiz.R;
import com.cevague.ankiquiz.sql.CardModel;
import com.cevague.ankiquiz.utils.AudioPlayer;

import java.util.ArrayList;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameFragment extends Fragment {
    private static final String ARG_LISTE_CARTES = "liste_cartes";
    private static final int ID_GAME = 0;

    ImageButton imageButton;
    Button[] buttons;

    private ArrayList<CardModel> cardList;

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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_game, container, false);

        imageButton = view.findViewById(R.id.imageButton_M2N);

        buttons = new Button[]{
                view.findViewById(R.id.button_M2N_1),
                view.findViewById(R.id.button_M2N_2),
                view.findViewById(R.id.button_M2N_3),
                view.findViewById(R.id.button_M2N_4)
        };

        CardModel card = findCard();
        if(card != null){
            int rnd_sound = new Random().nextInt(card.getAudios().size());
            int rnd_answer = new Random().nextInt(4);


            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AudioPlayer.playAudio(getContext(), card.getAudios().get(rnd_sound).getAbsolute_path());
                }
            });

            for(int i=0;i<4;i++){
                if(i == rnd_answer){
                    buttons[i].setText(card.getInfo().getName());
                }else{
                    int rnd_tmp = new Random().nextInt(cardList.size()-1);
                    CardModel card_tmp = cardList.get(rnd_tmp);
                    if(card_tmp == card){
                        card_tmp = cardList.get(cardList.size()-1);
                    }
                    buttons[i].setText(card_tmp.getInfo().getName());
                }
            }
        }

        return view;
    }

    private CardModel findCard(){
        ArrayList<CardModel> tmp_list = new ArrayList<CardModel>(cardList);

        for(CardModel card : tmp_list){
            if(!card.isTo_learn() || !card.getGame_type(ID_GAME)){
                tmp_list.remove(card);
            }else{
                return card;
            }
        }
        return null;
    }
}