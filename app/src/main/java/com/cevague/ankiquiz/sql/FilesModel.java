package com.cevague.ankiquiz.sql;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

public class FilesModel {

    private List<String> files_uri;

    public FilesModel(List<String> files_uri) {
        this.files_uri = files_uri;
    }

    @Override
    public String toString() {
        return "FilesModel{" +
                "files_uri=" + files_uri +
                '}';
    }

    public List<String> getFilesUri() {
        return files_uri;
    }

    public String getRandomUri(){
        int i = new Random().nextInt(this.files_uri.size());
        return getOneUri(i);
    }

    public String getOneUri(int i){
        return this.files_uri.get(i);
    }

    public void addFileUri(String uri) {
        if(this.files_uri.isEmpty()){
            this.files_uri = new ArrayList<String>();
        }
        this.files_uri.add(uri);
    }

    public void setFilesUri(List<String> files_uri) {
        this.files_uri = files_uri;
    }
}
