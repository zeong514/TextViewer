package kr.co.z_eong.textviewer.db;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import kr.co.z_eong.textviewer.sub.Bookmark;
import kr.co.z_eong.textviewer.sub.Position;

public class DBmanager extends SQLiteOpenHelper{
    public DBmanager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DBmanager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String qrery = "CREATE TABLE background(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "color INTEGER, root TEXT, size INTEGER, page INTEGER);";

        db.execSQL(qrery);

        qrery = "CREATE TABLE position (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, num INTEGER);";

        db.execSQL(qrery);

        qrery = "CREATE TABLE bookmark (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "root TEXT, page INTEGER, title TEXT, name TEXT);";

        db.execSQL(qrery);

        qrery = "insert into background values(0,0,'/storage/emulated/0',15,0);";

        db.execSQL(qrery);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertBookmark(String root, int page, String title, String name){
        SQLiteDatabase db = getReadableDatabase();

        String qrery = "insert into bookmark values(null,'"+root+"',"+page+",'"+title+"','"+name+"');";

        db.execSQL(qrery);
    }

    public ArrayList<Bookmark> seleteBookmark(){
        ArrayList<Bookmark> bookmarks = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String query = "select * from bookmark;";
        Cursor cursor = db.rawQuery(query,null);
        while (cursor.moveToNext()) {
            Integer id = cursor.getInt(0);
            String root = cursor.getString(1);
            Integer page = cursor.getInt(2);
            String title = cursor.getString(3);
            String name = cursor.getString(4);

            Bookmark bookmark = new Bookmark(id,root,page,title,name);
            bookmarks.add(bookmark);
        }
        cursor.close();

        return bookmarks;
    }

    public ArrayList<Bookmark> seleteBookmarkRoot(String ogname){
        ArrayList<Bookmark> bookmarks = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String query = "select * from bookmark where name ='"+ogname+"';";
        Cursor cursor = db.rawQuery(query,null);
        while (cursor.moveToNext()) {
            Integer id = cursor.getInt(0);
            String root = cursor.getString(1);
            Integer page = cursor.getInt(2);
            String title = cursor.getString(3);
            String name = cursor.getString(4);

            Bookmark bookmark = new Bookmark(id,root,page,title,name);
            bookmarks.add(bookmark);
        }
        cursor.close();

        return bookmarks;
    }

    public void deleteBookmark(int id){
        SQLiteDatabase db = getReadableDatabase();

        String query ="delete from bookmark where id = "+id+";";
        db.execSQL(query);
    }



    public void insertPosition(String name, int num){
        SQLiteDatabase db = getReadableDatabase();

        String qrery = "insert into position values(null,'"+name+"',"+num+");";

        db.execSQL(qrery);
    }

    public void updatePosition(int num, int id){
        SQLiteDatabase db = getReadableDatabase();

        String qrery = "update position set num = "+num+" where id = "+id+";";

        db.execSQL(qrery);
    }

    public ArrayList<Position> seletePosition(){
        ArrayList<Position> positions = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String query = "select * from position;";
        Cursor cursor = db.rawQuery(query,null);
        while (cursor.moveToNext()) {
            Integer id = cursor.getInt(0);
            String root = cursor.getString(1);
            Integer num = cursor.getInt(2);

            Position position = new Position(id,root,num);
            positions.add(position);
        }
        cursor.close();

        return positions;
    }


    public void updateBackground(int color){
        SQLiteDatabase db = getReadableDatabase();

        String qrery = "update background set color = "+color+" where id = 0;";

        db.execSQL(qrery);
    }

    public void updateRoot(String root){
        SQLiteDatabase db = getReadableDatabase();

        String qrery = "update background set root = '"+root+"' where id = 0;";

        db.execSQL(qrery);
    }

    public void updatePage(Integer page){
        SQLiteDatabase db = getReadableDatabase();

        String qrery = "update background set page = "+page+" where id = 0;";

        db.execSQL(qrery);
    }

    public void updateTextsize(int size){
        SQLiteDatabase db = getReadableDatabase();

        String qrery = "update background set size = "+size+" where id = 0;";

        db.execSQL(qrery);
    }


    public int seletePage(){
        SQLiteDatabase db = getReadableDatabase();
        int col = 0;

        String query = "select page from background where id = 0;";
        Cursor cursor = db.rawQuery(query,null);
        while (cursor.moveToNext()) {
            Integer page = cursor.getInt(0);
            col = page;

        }
        cursor.close();

        return col;
    }



    public int seleteBackground(){
        SQLiteDatabase db = getReadableDatabase();
        int col = 0;

        String query = "select color from background where id = 0;";
        Cursor cursor = db.rawQuery(query,null);
        while (cursor.moveToNext()) {
            Integer color = cursor.getInt(0);
            col = color;

        }
        cursor.close();

        return col;
    }

    public String seleteRoot(){
        SQLiteDatabase db = getReadableDatabase();
        String col = "";

        String query = "select root from background where id = 0;";
        Cursor cursor = db.rawQuery(query,null);
        while (cursor.moveToNext()) {
            String root = cursor.getString(0);
            col = root;

        }
        cursor.close();

        return col;
    }

    public int seleteTextsize(){
        SQLiteDatabase db = getReadableDatabase();
        int col = 0;

        String query = "select size from background where id = 0;";
        Cursor cursor = db.rawQuery(query,null);
        while (cursor.moveToNext()) {
            Integer size = cursor.getInt(0);
            col = size;

        }
        cursor.close();

        return col;
    }
}
