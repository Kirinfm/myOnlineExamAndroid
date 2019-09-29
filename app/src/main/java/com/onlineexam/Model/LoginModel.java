package com.onlineexam.Model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.onlineexam.DatabaseHelper.SqliteCreate;
import com.onlineexam.bo.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 方明 on 2017/3/12.
 */

public class LoginModel {
    private SqliteCreate sqlitecreate;
    private SQLiteDatabase db;

    public LoginModel(Context context) {
        sqlitecreate = new SqliteCreate(context);
    }

    public List<User> getUserinfo() {
        db = sqlitecreate.getWritableDatabase();
        ArrayList<User> users = new ArrayList<User>();
        String sql = "SELECT * FROM user";
        Cursor c = db.rawQuery(sql, null);
        while (c.moveToNext()) {
            User u = new User();
            u.setId(c.getInt(c.getColumnIndex("ID")));
            u.setSchoolid(c.getInt(c.getColumnIndex("SchoolID")));
            u.setName(c.getString(c.getColumnIndex("Name")));
            u.setPassword(c.getString(c.getColumnIndex("Password")));
            u.setTelNo(c.getString(c.getColumnIndex("TelNo")));
            u.setPhoto(c.getString(c.getColumnIndex("Photo")));
            u.setIsteacher(c.getInt(c.getColumnIndex("IsTeacher")));
            users.add(u);
        }
        c.close();
        db.close();
        return users;
    }

    public void insertUserinfo(User user) {
        db = sqlitecreate.getWritableDatabase();
        String sql = "INSERT INTO user (SchoolID,Name,Password,TelNo,Photo,IsTeacher) VALUES (?,?,?,?,?,?)";
        db.beginTransaction();
        try {
            db.execSQL(sql, new Object[]{user.getSchoolid(), user.getName(), user.getPassword(), user.getTelNo(), user.getPhoto(), user.getIsteacher()});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    public void updateUserinfo(User user) {
        db = sqlitecreate.getWritableDatabase();
        String sql = "UPDATE user SET TelNo = ? , Password = ? WHERE SchoolID = ?";
        db.beginTransaction();
        try {
            db.execSQL(sql, new Object[]{user.getTelNo(), user.getPassword(), user.getSchoolid()});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    public void deleteUserinfo() {
        db = sqlitecreate.getWritableDatabase();
        db.delete("user", null, null);
        db.close();
    }
}
