package com.onlineexam.DatabaseHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by 方明 on 2017/3/12.
 */

public class SqliteCreate extends SQLiteOpenHelper {
    public SqliteCreate(Context context){
        super(context,"app_onlineexam",null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE \"user\" (\n" +
                "\"ID\"  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                "\"SchoolID\"  INTEGER,\n" +
                "\"Name\"  TEXT,\n" +
                "\"Password\"  TEXT,\n" +
                "\"TelNo\"  TEXT(11),\n" +
                "\"Photo\"  TEXT,\n" +
                "\"IsTeacher\"  INTEGER\n" +
                ");\n" +
                "\n" +
                "CREATE UNIQUE INDEX \"SchoolID\"\n" +
                "ON \"user\" (\"SchoolID\" ASC);";
        db.execSQL(sql);
        sql = "CREATE TABLE \"course\" (\n" +
                "\"CourseID\"  INTEGER,\n" +
                "\"CourseName\"  TEXT,\n" +
                "\"CreateDate\"  TEXT,\n" +
                "\"DeadLine\"  TEXT,\n" +
                "\"Context\"  TEXT\n" +
                ");";
        db.execSQL(sql);
        sql = "CREATE TABLE \"question\" (\n" +
                "\"QuestionID\"  INTEGER,\n" +
                "\"CourseID\"  INTEGER,\n" +
                "\"Question\"  TEXT,\n" +
                "\"ChoiceA\"  TEXT,\n" +
                "\"ChoiceB\"  TEXT,\n" +
                "\"ChoiceC\"  TEXT,\n" +
                "\"ChoiceD\"  TEXT,\n" +
                "\"Answer\"  TEXT,\n" +
                "\"Score\"  INTEGER\n" +
                ");";
        db.execSQL(sql);
        Log.d("database","数据库已创建");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS `user`";
        db.execSQL(sql);
        sql = "DROP TABLE IF EXISTS `course`";
        db.execSQL(sql);
        sql = "DROP TABLE IF EXISTS `question`";
        db.execSQL(sql);
    }
}
