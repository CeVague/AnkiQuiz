package com.cevague.ankiquiz.sql;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;

public class CardModel {

    private long id_c;
    private InfoModel info;
    private ArrayList<FileModel> audios;
    private ArrayList<FileModel> images;
    private ArrayList<FileModel> texts;

    private boolean to_learn;
    private int level;
    private Date next_time;

    public CardModel() {
    }

    public CardModel(long id_c, InfoModel info, ArrayList<FileModel> audios, ArrayList<FileModel> images, ArrayList<FileModel> texts, boolean to_learn, int level, Date next_time) {
        this.id_c = id_c;
        this.info = info;
        this.audios = audios;
        this.images = images;
        this.texts = texts;
        this.to_learn = to_learn;
        this.level = level;
        this.next_time = next_time;
    }

    @NonNull
    @Override
    public String toString() {
        return "CardModel{" +
                "id_c=" + id_c +
                ", info=" + info +
                ", audios=" + audios +
                ", images=" + images +
                ", texts=" + texts +
                ", to_learn=" + to_learn +
                ", level=" + level +
                ", next_time=" + next_time +
                '}';
    }

    public long getId_c() {
        return id_c;
    }

    public void setId_c(long id_c) {
        this.id_c = id_c;
    }

    public InfoModel getInfo() {
        return info;
    }

    public void setInfo(InfoModel info) {
        this.info = info;
    }

    public ArrayList<FileModel> getAudios() {
        return audios;
    }

    public void setAudios(ArrayList<FileModel> audios) {
        this.audios = audios;
    }

    public ArrayList<FileModel> getImages() {
        return images;
    }

    public void setImages(ArrayList<FileModel> images) {
        this.images = images;
    }

    public ArrayList<FileModel> getTexts() {
        return texts;
    }

    public void setTexts(ArrayList<FileModel> texts) {
        this.texts = texts;
    }

    public boolean isTo_learn() {
        return to_learn;
    }

    public void setTo_learn(boolean to_learn) {
        this.to_learn = to_learn;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Date getNext_time() {
        return next_time;
    }

    public void setNext_time(Date next_time) {
        this.next_time = next_time;
    }
}
