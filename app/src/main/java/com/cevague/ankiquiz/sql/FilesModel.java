package com.cevague.ankiquiz.sql;

public class FilesModel {

    private long id_i;
    private long id_f;
    private String path;
    private String type;

    public FilesModel() {
    }

    public FilesModel(long id_i, long id_f, String path, String type) {
        this.id_i = id_i;
        this.id_f = id_f;
        this.path = path;
        this.type = type;
    }

    @Override
    public String toString() {
        return "FilesModel{" +
                "id_i=" + id_i +
                ", id_f=" + id_f +
                ", path='" + path + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    public long getId_i() {
        return id_i;
    }

    public void setId_i(long id_i) {
        this.id_i = id_i;
    }

    public long getId_f() {
        return id_f;
    }

    public void setId_f(long id_f) {
        this.id_f = id_f;
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
