package com.cevague.ankiquiz.sql;

public class FilesModel {

    private long id_f;
    private long id_i;
    private String card_set;
    private String path;
    private String type;

    public FilesModel() {
    }

    public FilesModel(long id_f, long id_i, String card_set, String path, String type) {
        this.id_f = id_f;
        this.id_i = id_i;
        this.card_set = card_set;
        this.path = path;
        this.type = type;
    }

    @Override
    public String toString() {
        return "FilesModel{" +
                "id_f=" + id_f +
                ", id_i=" + id_i +
                ", card_set='" + card_set + '\'' +
                ", path='" + path + '\'' +
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
