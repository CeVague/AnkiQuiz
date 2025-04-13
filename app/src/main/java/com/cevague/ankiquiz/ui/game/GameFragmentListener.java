package com.cevague.ankiquiz.ui.game;

import androidx.fragment.app.Fragment;

public class GameFragmentListener  extends Fragment {


    public interface OnAnswerListener {
        void onAnswer(boolean win, boolean played);
    }

    public GameQCMFragment.OnAnswerListener answerListener;

    public void setOnAnswerListener(GameQCMFragment.OnAnswerListener listener){
        this.answerListener = listener;
    }
}
