package com.usiellau.messageforward.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.usiellau.messageforward.other.MyApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/8 0008.
 */

public class DBUtil {
    private static DBHelper dbHelper=new DBHelper(MyApplication.getContext(),"NumberStore.db",null,1);

    public static DBHelper getDBHelper(){
        return dbHelper;
    }
    public static SQLiteDatabase getWritableDatabase(){
        return dbHelper.getWritableDatabase();
    }

    public static void deleteMonitorNumberByNumber(String number){
        getWritableDatabase().delete("number_monitor","number=?",new String[]{number});
    }

    public static void deleteForwardNumberByNumber(String number){
        getWritableDatabase().delete("number_forward","number=?",new String[]{number});
    }

    public static void deleteForwardEmailByAddress(String address){
        getWritableDatabase().delete("email_forward","address=?",new String[]{address});
    }

    public static List<String> queryMonitorNumber(){
        List<String> res=new ArrayList<>();
        SQLiteDatabase database=getWritableDatabase();
        Cursor cursor=database.query("number_monitor",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            String number;
            do{
                number=cursor.getString(cursor.getColumnIndex("number"));
                res.add(number);
            }while (cursor.moveToNext());
            cursor.close();
        }

//        Log.d("DBUtil","查询到数据：");
//        for(String s:res){
//            Log.d("DBUtil",s);
//        }

        return res;
    }
    public static List<String> queryForwardNumber(){
        List<String> res=new ArrayList<>();
        SQLiteDatabase database=getWritableDatabase();
        Cursor cursor=database.query("number_forward",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            String number;
            do{
                number=cursor.getString(cursor.getColumnIndex("number"));
                res.add(number);
            }while (cursor.moveToNext());
            cursor.close();
        }
        return res;
    }

    public static List<String> queryForwardEmail(){
        List<String> res=new ArrayList<>();
        SQLiteDatabase database=getWritableDatabase();
        Cursor cursor=database.query("email_forward",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            String address;
            do{
                address=cursor.getString(cursor.getColumnIndex("address"));
                res.add(address);
            }while (cursor.moveToNext());
            cursor.close();
        }
        return res;
    }
}
