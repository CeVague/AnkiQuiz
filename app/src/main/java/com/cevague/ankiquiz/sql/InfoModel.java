package com.cevague.ankiquiz.sql;

public class InfoModel {

    private long id_i;
    private String card_set;
    private String folder;
    private String name;
    private String hint;
    private String description;
    private String img_path;
    private String absolute_path;

    public InfoModel() {
    }

    public InfoModel(long id_i, String card_set, String folder, String name, String hint, String description, String img_path) {
        this.id_i = id_i;
        this.card_set = card_set;
        this.folder = folder;
        this.name = name;
        this.hint = hint;
        this.description = description;
        this.img_path = img_path;
    }

    @Override
    public String toString() {
        return "InfoModel{" +
                "id_i=" + id_i +
                ", card_set='" + card_set + '\'' +
                ", folder='" + folder + '\'' +
                ", name='" + name + '\'' +
                ", hint='" + hint + '\'' +
                ", description='" + description + '\'' +
                ", img_path='" + img_path + '\'' +
                ", absolute_path='" + absolute_path + '\'' +
                '}';
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

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
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

    public String getImg_path() {
        return img_path;
    }

    public void setImg_path(String img_path) {
        this.img_path = img_path;
    }

    public String getImg_absolute_path() {
        return absolute_path;
    }

    public void setImg_absolute_path(String absolute_path) {
        this.absolute_path = absolute_path;
    }

    public void setImg_absolute_path() {
        this.absolute_path = "data/"+getCard_set()+"/"+getFolder()+"/"+getImg_path();
    }


}
