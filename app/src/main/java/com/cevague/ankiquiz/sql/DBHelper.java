package com.cevague.ankiquiz.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
    private static final String COL_SET_FOLDER = "set_folder";
    private static final String COL_CARD_FOLDER = "card_folder";
    private static final String COL_FILE_NAME = "file_name";
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
                + COL_SET_FOLDER + " TEXT NOT NULL,"
                + COL_CARD_FOLDER + " TEXT NOT NULL,"
                + COL_FILE_NAME + " TEXT NOT NULL,"
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

    public boolean existFile(FileModel file){
        String request =
                "SELECT * FROM " + TABLE_FILES
                        + " WHERE " + COL_FILE_NAME + " == '" + file.getFileName() + "'"
                        + " AND " + COL_SET_FOLDER + " == '" + file.getSetFolder() + "'"
                        + " AND " + COL_CARD_FOLDER + " == '" + file.getCardFolder() + "'"
                        + " AND " + COL_ID_INFO + " == " + file.getIdI();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(request, null);

        boolean tmp = cursor.getCount() > 0;

        cursor.close();
        db.close();

        return tmp;
    }

    public long addFile(FileModel file){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_ID_INFO, file.getIdI());
        values.put(COL_CARD_SET, file.getCardSet());
        values.put(COL_SET_FOLDER, file.getSetFolder());
        values.put(COL_CARD_FOLDER, file.getCardFolder());
        values.put(COL_FILE_NAME, file.getFileName());
        values.put(COL_TYPE, file.getType());

        long id = db.insert(TABLE_FILES, null, values);

        db.close();

        return id;
    }

    public ArrayList<FileModel> getAllFiles(String card_set){
        return getAllFiles(card_set, "");
    }

    public ArrayList<FileModel> getAllFiles(String card_set, String type){
        ArrayList<FileModel> listFiles = new ArrayList<FileModel>();

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
                FileModel file = new FileModel();
                file.setIdF(cursor.getLong(0));
                file.setIdI(cursor.getLong(1));
                file.setCardSet(cursor.getString(2));
                file.setSetFolder(cursor.getString(3));
                file.setCardFolder(cursor.getString(4));
                file.setFileName(cursor.getString(5));
                file.setType(cursor.getString(6));

                listFiles.add(file);
            }while(cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return listFiles;
    }

    public ArrayList<FileModel> getAllFiles(long id_i){
        return getAllFiles(id_i, "");
    }

    public ArrayList<FileModel> getAllFiles(long id_i, String type){
        ArrayList<FileModel> listFiles = new ArrayList<FileModel>();

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
                FileModel file = new FileModel();
                file.setIdF(cursor.getLong(0));
                file.setIdI(cursor.getLong(1));
                file.setCardSet(cursor.getString(2));
                file.setSetFolder(cursor.getString(3));
                file.setCardFolder(cursor.getString(4));
                file.setFileName(cursor.getString(5));
                file.setType(cursor.getString(6));

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

        String dateFormat = (String) android.text.format.DateFormat.format("yyyy-MM-dd", card.getNext_time());
        values.put(COL_NEXT_TIME, dateFormat);

        long id = db.insert(TABLE_CARDS, null, values);

        db.close();

        return id;
    }

    public long updateCard(CardModel card){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_ID_CARD, card.getId_c());
        values.put(COL_ID_INFO, card.getInfo().getId_i());
        values.put(COL_TO_LEARN, card.isTo_learn());
        values.put(COL_LEVEL, card.getLevel());

        String dateFormat = (String) android.text.format.DateFormat.format("yyyy-MM-dd", card.getNext_time());
        values.put(COL_NEXT_TIME, dateFormat);

        String strWhereClause = COL_ID_CARD + " = " + card.getId_c();

        long nb = db.update(TABLE_CARDS, values, strWhereClause, null);
        db.close();

        return nb;
    }

    public long resetNTCard(Date date){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        String dateFormat = (String) android.text.format.DateFormat.format("yyyy-MM-dd", date);
        values.put(COL_NEXT_TIME, dateFormat);

        long nb = db.update(TABLE_CARDS, values, null, null);
        db.close();

        return nb;
    }

    public CardModel getSimpleCard(long id) {
        CardModel card = null;

        String request =
                "SELECT * FROM " + TABLE_CARDS
                        + " WHERE " + COL_ID_CARD + " == " + id;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(request, null);
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();

            card = new CardModel();

            card.setId_c(cursor.getLong(0));
            card.setTo_learn(cursor.getInt(2) == 1);
            card.setLevel(cursor.getInt(3));

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                card.setNext_time(format.parse(cursor.getString(4)));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        cursor.close();
        return card;
    }

    public CardModel getSimpleCard(InfoModel info) {
        return getSimpleCard(info.getId_i());
    }


    public ArrayList<CardModel> getAllCards(String card_set){
        ArrayList<CardModel> listCards = new ArrayList<CardModel>();

        ArrayList<InfoModel> list_infos = getAllInfo(card_set);

        for(InfoModel info : list_infos){
            CardModel card = getSimpleCard(info.getId_i());
            card.setInfo(info);
            card.setAudios(getAllFiles(info.getId_i(), "mp3"));
            card.setImages(getAllFiles(info.getId_i(), "jpg"));
            card.setTexts(getAllFiles(info.getId_i(), "txt"));

            listCards.add(card);
        }

        return listCards;
    }

    public ArrayList<CardModel> getAllCardsBefore(String card_set, Date date){
        String dateFormat = (String) android.text.format.DateFormat.format("yyyy-MM-dd", date);

        ArrayList<CardModel> listCards = new ArrayList<>();

        for(CardModel card : getAllCards(card_set)){
            if(!card.getNext_time().after(date)){
                listCards.add(card);
            }
        }

        return listCards;
    }


    public boolean existCard(CardModel card){
        String request =
                "SELECT * FROM " + TABLE_CARDS
                        + " WHERE " + COL_ID_INFO + " == '" + card.getInfo().getId_i() + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(request, null);

        boolean tmp = cursor.getCount() > 0;

        cursor.close();
        db.close();

        return tmp;
    }


    public ArrayList<String> getAllCardSet(){
        ArrayList<String> listFiles = new ArrayList<String>();

        String request = "SELECT DISTINCT " + COL_CARD_SET + " FROM " + TABLE_INFOS ;


        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(request, null);

        if(cursor.moveToFirst()){
            do{
                listFiles.add(cursor.getString(0));
            }while(cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return listFiles;
    }

    public String getSetFolderFromSetName(String setName){
        String setFolder = null;

        String request = "SELECT " + COL_SET_FOLDER + " FROM " + TABLE_FILES
                + " WHERE " + COL_CARD_SET + " = ?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(request, new String[]{setName});
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();

            setFolder = cursor.getString(0);
        }
        cursor.close();

        return setFolder;
    }

    public void deleteFile(FileModel file){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FILES, COL_ID_FILE + " = ?", new String[] { String.valueOf(file.getIdF()) });
        db.close();
    }

    public void deleteInfo(InfoModel info){
        deleteInfo(info.getId_i());
    }

    public void deleteInfo(long id_info){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_INFOS, COL_ID_INFO + " = ?", new String[] { String.valueOf(id_info) });
        db.close();
    }

    public void deleteCardFromInfo(long id_info){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CARDS, COL_ID_INFO + " = ?", new String[] { String.valueOf(id_info) });
        db.close();
    }

    public void deleteCard(CardModel card){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CARDS, COL_ID_CARD + " = ?", new String[] { String.valueOf(card.getId_c()) });
        db.close();
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
