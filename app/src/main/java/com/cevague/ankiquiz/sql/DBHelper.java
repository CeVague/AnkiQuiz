package com.cevague.ankiquiz.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "cards.db";
    private static final int DB_VERSION = 1;



    private static final String TABLE_INFOS = "infos";
    private static final String COL_ID_INFO = "id_i";
    private static final String COL_CARD_SET = "name_card_set";
    private static final String COL_FOLDER = "folder";
    private static final String COL_INFO_NAME = "name";
    private static final String COL_HINT = "hint";
    private static final String COL_DESCRIPTION = "description";
    private static final String COL_IMG_PATH = "img_path";



    private static final String TABLE_FILES = "files";
    private static final String COL_ID_FILE = "id_f";
    private static final String COL_PATH = "path";
    private static final String COL_ABSOLUTE_PATH = "abs_path";
    private static final String COL_TYPE = "type";



    private static final String TABLE_CARDS = "cards";
    private static final String COL_ID_CARD = "id_c";
    private static final String COL_TO_LEARN = "to_learn";
    private static final String COL_LEVEL = "lvl";
    private static final String COL_NEXT_TIME = "next_time";


    public DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String query_infos = "CREATE TABLE " + TABLE_INFOS + " ("
                + COL_ID_INFO + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_CARD_SET + " TEXT NOT NULL,"
                + COL_FOLDER + " TEXT NOT NULL,"
                + COL_INFO_NAME + " TEXT NOT NULL,"
                + COL_HINT + " TEXT NOT NULL,"
                + COL_DESCRIPTION + " TEXT NOT NULL,"
                + COL_IMG_PATH + " INTEGER NOT NULL)";

        db.execSQL(query_infos);

        String query_files = "CREATE TABLE " + TABLE_FILES + " ("
                + COL_ID_FILE + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_ID_INFO + " INTEGER NOT NULL,"
                + COL_CARD_SET + " TEXT NOT NULL,"
                + COL_PATH + " TEXT NOT NULL,"
                + COL_ABSOLUTE_PATH + " TEXT NOT NULL,"
                + COL_TYPE + " TEXT NOT NULL,"
                + "FOREIGN KEY(" + COL_ID_INFO + ") REFERENCES " + TABLE_INFOS + "(" + COL_ID_INFO + "))";

        db.execSQL(query_files);

        String query_cards = "CREATE TABLE " + TABLE_CARDS + " ("
                + COL_ID_CARD + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_ID_INFO + " INTEGER NOT NULL,"
                + COL_TO_LEARN + " BOOLEAN NOT NULL,"
                + COL_LEVEL + " INTEGER NOT NULL,"
                + COL_NEXT_TIME + " TEXT NOT NULL,"
                + "FOREIGN KEY(" + COL_ID_INFO + ") REFERENCES " + TABLE_INFOS + "(" + COL_ID_INFO + "))";

        db.execSQL(query_cards);
    }

    public InfoModel getInfo(long id){
        InfoModel info = null;

        String request =
                "SELECT * FROM " + TABLE_INFOS +
                        " WHERE "+COL_ID_INFO+" == " + id;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(request, null);
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();

            info = new InfoModel();
            info.setId_i(cursor.getLong(0));
            info.setCard_set(cursor.getString(1));
            info.setFolder(cursor.getString(2));
            info.setName(cursor.getString(3));
            info.setHint(cursor.getString(4));
            info.setDescription(cursor.getString(5));
            info.setImg_path(cursor.getString(6));
            info.setImg_absolute_path();
        }
        cursor.close();
        return info;
    }

    public InfoModel getInfo(String card_set, String folder){
        InfoModel info = null;

        String request =
                "SELECT * FROM " + TABLE_INFOS
                        + " WHERE " + COL_FOLDER + " == '" + folder + "'"
                        + " AND " + COL_CARD_SET + " == '" + card_set + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(request, null);
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();

            info = new InfoModel();
            info.setId_i(cursor.getLong(0));
            info.setCard_set(cursor.getString(1));
            info.setFolder(cursor.getString(2));
            info.setName(cursor.getString(3));
            info.setHint(cursor.getString(4));
            info.setDescription(cursor.getString(5));
            info.setImg_path(cursor.getString(6));
            info.setImg_absolute_path();
        }
        cursor.close();
        return info;
    }

    public boolean existInfo(String card_set, String folder){
        String request =
                "SELECT * FROM " + TABLE_INFOS
                        + " WHERE " + COL_FOLDER + " == '" + folder + "'"
                        + " AND " + COL_CARD_SET + " == '" + card_set + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(request, null);

        boolean tmp = cursor.getCount() > 0;

        cursor.close();
        db.close();

        return tmp;
    }

    public ArrayList<InfoModel> getAllInfo(String card_set){
        ArrayList<InfoModel> listInfo = new ArrayList<InfoModel>();

        String request =
                "SELECT * FROM " + TABLE_INFOS +
                        " WHERE "+COL_CARD_SET+" == '" + card_set + "'";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(request, null);

        if(cursor.moveToFirst()){
            do{
                InfoModel info = new InfoModel();
                info.setId_i(cursor.getLong(0));
                info.setCard_set(cursor.getString(1));
                info.setFolder(cursor.getString(2));
                info.setName(cursor.getString(3));
                info.setHint(cursor.getString(4));
                info.setDescription(cursor.getString(5));
                info.setImg_path(cursor.getString(6));
                info.setImg_absolute_path();

                listInfo.add(info);
            }while(cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return listInfo;
    }

    public long addInfo(InfoModel info){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COL_CARD_SET, info.getCard_set());
        values.put(COL_FOLDER, info.getFolder());
        values.put(COL_INFO_NAME, info.getName());
        values.put(COL_HINT, info.getHint());
        values.put(COL_DESCRIPTION, info.getDescription());
        values.put(COL_IMG_PATH, info.getImg_path());

        long id = db.insert(TABLE_INFOS, null, values);

        db.close();

        return id;
    }

    public boolean existFile(FilesModel file){
        String request =
                "SELECT * FROM " + TABLE_FILES
                        + " WHERE " + COL_PATH + " == '" + file.getPath() + "'"
                        + " AND " + COL_ID_INFO + " == " + file.getId_i();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(request, null);

        boolean tmp = cursor.getCount() > 0;

        cursor.close();
        db.close();

        return tmp;
    }

    public long addFile(FilesModel file){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_ID_INFO, file.getId_i());
        values.put(COL_CARD_SET, file.getCard_set());
        values.put(COL_PATH, file.getPath());
        values.put(COL_ABSOLUTE_PATH, file.getAbsolute_path());
        values.put(COL_TYPE, file.getType());

        long id = db.insert(TABLE_FILES, null, values);

        db.close();

        return id;
    }

    public ArrayList<FilesModel> getAllFiles(String card_set){
        return getAllFiles(card_set, "");
    }

    public ArrayList<FilesModel> getAllFiles(String card_set, String type){
        ArrayList<FilesModel> listFiles = new ArrayList<FilesModel>();

        String request =
                "SELECT * FROM " + TABLE_FILES +
                        " WHERE "+COL_CARD_SET+" == '" + card_set + "'";

        if(!type.isEmpty()){
            request += " AND "+COL_TYPE+" == '" + type +"'";
        }

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(request, null);

        if(cursor.moveToFirst()){
            do{
                FilesModel file = new FilesModel();
                file.setId_f(cursor.getLong(0));
                file.setId_i(cursor.getLong(1));
                file.setCard_set(cursor.getString(2));
                file.setPath(cursor.getString(3));
                file.setAbsolute_path(cursor.getString(4));
                file.setType(cursor.getString(5));

                listFiles.add(file);
            }while(cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return listFiles;
    }

    public ArrayList<FilesModel> getAllFiles(long id_i){
        return getAllFiles(id_i, "");
    }

    public ArrayList<FilesModel> getAllFiles(long id_i, String type){
        ArrayList<FilesModel> listFiles = new ArrayList<FilesModel>();

        String request =
                "SELECT * FROM " + TABLE_FILES +
                        " WHERE "+COL_ID_INFO+" == " + id_i;

        if(!type.isEmpty()){
            request += " AND "+COL_TYPE+" == '" + type +"'";
        }

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(request, null);

        if(cursor.moveToFirst()){
            do{
                FilesModel file = new FilesModel();
                file.setId_f(cursor.getLong(0));
                file.setId_i(cursor.getLong(1));
                file.setCard_set(cursor.getString(2));
                file.setPath(cursor.getString(3));
                file.setAbsolute_path(cursor.getString(4));
                file.setType(cursor.getString(5));

                listFiles.add(file);
            }while(cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return listFiles;
    }

    public long addCard(CardModel card){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_ID_INFO, card.getInfo().getId_i());
        values.put(COL_TO_LEARN, card.isTo_learn());
        values.put(COL_LEVEL, card.getLevel());
        values.put(COL_NEXT_TIME, card.getNext_time().toString());

        long id = db.insert(TABLE_CARDS, null, values);

        db.close();

        return id;
    }

    public void onReset() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FILES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INFOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARDS);
        onCreate(db);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FILES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INFOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARDS);
        onCreate(db);
    }
}
