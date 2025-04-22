package com.cevague.ankiquiz.sql;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.File;

public class FileModel implements Parcelable {

    private long idF;
    private long idI;
    private String cardSet;
    private String setFolder;
    private String cardFolder;
    private String fileName;
    private String absolutePath;
    private String type;

    public FileModel() {
    }

    public FileModel(long idF, long idI, String cardSet, String setFolder, String cardFolder, String fileName, String type) {
        this.idF = idF;
        this.idI = idI;
        this.cardSet = cardSet;
        this.setFolder = setFolder;
        this.cardFolder = cardFolder;
        this.fileName = fileName;
        this.type = type;
    }

    protected FileModel(Parcel in) {
        idF = in.readLong();
        idI = in.readLong();
        cardSet = in.readString();
        setFolder = in.readString();
        cardFolder = in.readString();
        fileName = in.readString();
        type = in.readString();
    }

    public static final Creator<FileModel> CREATOR = new Creator<FileModel>() {
        @Override
        public FileModel createFromParcel(Parcel in) {
            return new FileModel(in);
        }

        @Override
        public FileModel[] newArray(int size) {
            return new FileModel[size];
        }
    };

    @Override
    public String toString() {
        return "FileModel{" +
                "idF=" + idF +
                ", idI=" + idI +
                ", cardSet='" + cardSet + '\'' +
                ", setFolder='" + setFolder + '\'' +
                ", cardFolder='" + cardFolder + '\'' +
                ", fileName='" + fileName + '\'' +
                ", type='" + type + '\'' +
                '}';
    }



    public long getIdF() {
        return idF;
    }

    public void setIdF(long idF) {
        this.idF = idF;
    }

    public long getIdI() {
        return idI;
    }

    public void setIdI(long idI) {
        this.idI = idI;
    }

    public String getCardSet() {
        return cardSet;
    }

    public void setCardSet(String cardSet) {
        this.cardSet = cardSet;
    }

    public String getSetFolder() {
        return setFolder;
    }

    public void setSetFolder(String setFolder) {
        this.setFolder = setFolder;
    }

    public String getCardFolder() {
        return cardFolder;
    }

    public void setCardFolder(String cardFolder) {
        this.cardFolder = cardFolder;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getAbsolutePath(Context context) {
        String basePath = context.getFilesDir().getAbsolutePath();
        return basePath + "/data/" + setFolder + "/" + cardFolder + "/" + fileName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeLong(idF);
        dest.writeLong(idI);
        dest.writeString(cardSet);
        dest.writeString(setFolder);
        dest.writeString(cardFolder);
        dest.writeString(fileName);
        dest.writeString(type);
    }
}