package com.onlineexam.Model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.onlineexam.DatabaseHelper.SqliteCreate;
import com.onlineexam.bo.Exam;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 方明 on 2017/3/19.
 */

public class ExamModel {
    private SqliteCreate sqlitecreate;
    private SQLiteDatabase db;

    public ExamModel(Context context) {
        sqlitecreate = new SqliteCreate(context);
    }

    public List<Exam> getExamList() {
        db = sqlitecreate.getWritableDatabase();
        ArrayList<Exam> exams = new ArrayList<Exam>();
        String sql = "SELECT * FROM course";
        Cursor c = db.rawQuery(sql, null);
        while (c.moveToNext()) {
            Exam exam = new Exam();
            exam.setCourseID(c.getInt(c.getColumnIndex("CourseID")));
            exam.setCourseName(c.getString(c.getColumnIndex("CourseName")));
            exam.setCreateDate(c.getString(c.getColumnIndex("CreateDate")));
            exam.setDeadLine(c.getString(c.getColumnIndex("DeadLine")));
            exam.setContext(c.getString(c.getColumnIndex("Context")));
            exams.add(exam);
        }
        c.close();
        db.close();
        return exams;
    }

    public void insertExamList(Exam exam) {
        db = sqlitecreate.getWritableDatabase();
        String sql = "INSERT INTO course (CourseID,CourseName,CreateDate,DeadLine,Context) VALUES (?,?,?,?,?)";
        db.beginTransaction();
        try {
            db.execSQL(sql, new Object[]{exam.getCourseID(), exam.getCourseName(), exam.getCreateDate(), exam.getDeadLine(), exam.getContext()});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    public void insertExam(Exam exam){
        db = sqlitecreate.getWritableDatabase();
        String sql = "INSERT INTO course (CourseID,CourseName) VALUES (?,?)";
        db.beginTransaction();
        try {
            db.execSQL(sql, new Object[]{exam.getCourseID(), exam.getCourseName()});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    public void deleteExam() {
        db = sqlitecreate.getWritableDatabase();
        db.delete("course", null, null);
        db.close();
    }

    public void deleteExamById(int CourseID) {
        db = sqlitecreate.getWritableDatabase();
        db.delete("course", "CourseID = " + CourseID, null);
        db.close();
    }
}
