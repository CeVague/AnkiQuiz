package com.cevague.ankiquiz.sql;

import androidx.annotation.NonNull;

public class InfoModel {

    private long id_i;
    private String name;
    private String hint;
    private String description;
    private String img_uri;

    public InfoModel(long id_i, String name, String hint, String description, String img_uri) {
        this.id_i = id_i;
        this.name = name;
        this.hint = hint;
        this.description = description;
        this.img_uri = img_uri;
    }

    @NonNull
    @Override
    public String toString() {
        return "InfoModel{" +
                "id_i=" + id_i +
                ", name='" + name + '\'' +
                ", hint='" + hint + '\'' +
                ", description='" + description + '\'' +
                ", img_uri='" + img_uri + '\'' +
                '}';
    }

    public long getId_i() {
        return id_i;
    }

    public void setId_i(long id_i) {
        this.id_i = id_i;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImg_uri() {
        return img_uri;
    }

    public void setImg_uri(String img_uri) {
        this.img_uri = img_uri;
    }
}
