package com.cevague.ankiquiz.sql;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class CardModel implements Parcelable {

    private long id_c;
    private InfoModel info;
    private ArrayList<FileModel> audios;
    private ArrayList<FileModel> images;
    private ArrayList<FileModel> texts;

    private boolean to_learn;
    private int level;
    private Date next_time;
    private boolean[] game_type;

    public CardModel() {
    }

    public CardModel(InfoModel info, int level, Date next_time) {
        this.info = info;
        this.level = level;
        this.next_time = next_time;
    }

    public CardModel(long id_c, InfoModel info, ArrayList<FileModel> audios, ArrayList<FileModel> images, ArrayList<FileModel> texts, boolean to_learn, int level, Date next_time, boolean[] game_type) {
        this.id_c = id_c;
        this.info = info;
        this.audios = audios;
        this.images = images;
        this.texts = texts;
        this.to_learn = to_learn;
        this.level = level;
        this.next_time = next_time;
        this.game_type = game_type;
    }

    protected CardModel(Parcel in) {
        id_c = in.readLong();
        info = in.readParcelable(InfoModel.class.getClassLoader());
        audios = in.createTypedArrayList(FileModel.CREATOR);
        images = in.createTypedArrayList(FileModel.CREATOR);
        texts = in.createTypedArrayList(FileModel.CREATOR);
        to_learn = in.readByte() != 0;
        level = in.readInt();
        game_type = in.createBooleanArray();
    }

    public static final Creator<CardModel> CREATOR = new Creator<CardModel>() {
        @Override
        public CardModel createFromParcel(Parcel in) {
            return new CardModel(in);
        }

        @Override
        public CardModel[] newArray(int size) {
            return new CardModel[size];
        }
    };

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
                ", game_type=" + Arrays.toString(game_type) +
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

    public boolean[] getGame_type() {
        return game_type;
    }

    public boolean getGame_type(int i) {
        return game_type[i];
    }

    public void setGame_type(boolean[] game_type) {
        this.game_type = game_type;
    }

    public void setGame_type(int i, boolean b) {
        this.game_type[i] = b;
    }

    public ArrayList<Integer> getIdGameTypeLeft(){
        ArrayList<Integer> tmp = new ArrayList<Integer>();
        for(int i=0;i<game_type.length;i++){
            if(game_type[i]){
                tmp.add(i);
            }
        }
        return tmp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeLong(id_c);
        dest.writeParcelable(info, flags);
        dest.writeTypedList(audios);
        dest.writeTypedList(images);
        dest.writeTypedList(texts);
        dest.writeByte((byte) (to_learn ? 1 : 0));
        dest.writeInt(level);
        dest.writeBooleanArray(game_type);
    }
}
