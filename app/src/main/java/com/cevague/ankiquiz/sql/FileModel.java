package com.cevague.ankiquiz.sql;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class FileModel implements Parcelable {

    private long id_f;
    private long id_i;
    private String card_set;
    private String path;
    private String absolute_path;
    private String type;

    public FileModel() {
    }

    public FileModel(long id_f, long id_i, String card_set, String path, String absolute_path, String type) {
        this.id_f = id_f;
        this.id_i = id_i;
        this.card_set = card_set;
        this.path = path;
        this.absolute_path = absolute_path;
        this.type = type;
    }

    protected FileModel(Parcel in) {
        id_f = in.readLong();
        id_i = in.readLong();
        card_set = in.readString();
        path = in.readString();
        absolute_path = in.readString();
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
                "id_f=" + id_f +
                ", id_i=" + id_i +
                ", card_set='" + card_set + '\'' +
                ", path='" + path + '\'' +
                ", absolute_path='" + absolute_path + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    public long getId_f() {
        return id_f;
    }

    public void setId_f(long id_f) {
        this.id_f = id_f;
    }

    public long getId_i() {
        return id_i;
    }

    public void setId_i(long id_i) {
        this.id_i = id_i;
    }

    public String getCard_set() {
        return card_set;
    }

    public void setCard_set(String card_set) {
        this.card_set = card_set;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAbsolute_path() {
        return absolute_path;
    }

    public void setAbsolute_path(String absolute_path) {
        this.absolute_path = absolute_path;
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
        dest.writeLong(id_f);
        dest.writeLong(id_i);
        dest.writeString(card_set);
        dest.writeString(path);
        dest.writeString(absolute_path);
        dest.writeString(type);
    }
}
