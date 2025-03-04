package com.cevague.ankiquiz.utils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cevague.ankiquiz.sql.CardModel;

import java.util.ArrayList;

public class SharedViewModel extends ViewModel {
    private ArrayList<CardModel> card_list = new ArrayList<CardModel>();

    public ArrayList<CardModel> getCard_list() {
        return card_list;
    }

    public void setCard_list(ArrayList<CardModel> card_list) {
        this.card_list = card_list;
    }
}