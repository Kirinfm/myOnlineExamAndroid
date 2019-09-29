package com.onlineexam.Model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.onlineexam.DatabaseHelper.SqliteCreate;
import com.onlineexam.bo.Question;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 方明 on 2017/3/22.
 */

public class QuestionModel {
    private SqliteCreate sqlitecreate;
    private SQLiteDatabase db;

    public QuestionModel(Context context) {
        sqlitecreate = new SqliteCreate(context);
    }

    public List<Question> getQuestionList(int CourseID) {
        db = sqlitecreate.getWritableDatabase();
        ArrayList<Question> questions = new ArrayList<Question>();
        String sql = "SELECT * FROM question WHERE CourseID = ?";
        Cursor c = db.rawQuery(sql, new String[]{CourseID + ""});
        while (c.moveToNext()) {
            Question question = new Question();
            question.setQuestionID(c.getInt(c.getColumnIndex("QuestionID")));
            question.setCourseID(c.getInt(c.getColumnIndex("CourseID")));
            question.setQuestion(c.getString(c.getColumnIndex("Question")));
            question.setChoiceA(c.getString(c.getColumnIndex("ChoiceA")));
            question.setChoiceB(c.getString(c.getColumnIndex("ChoiceB")));
            question.setChoiceC(c.getString(c.getColumnIndex("ChoiceC")));
            question.setChoiceD(c.getString(c.getColumnIndex("ChoiceD")));
            question.setAnswer(c.getString(c.getColumnIndex("Answer")));
            question.setScore(c.getInt(c.getColumnIndex("Score")));
            questions.add(question);
        }
        c.close();
        db.close();
        return questions;
    }

    public Question getQuestionById(int QuestionID) {
        db = sqlitecreate.getWritableDatabase();
        String sql = "SELECT * FROM question WHERE QuestionID = ?";
        Cursor c = db.rawQuery(sql, new String[]{QuestionID + ""});
        Question question = new Question();
        if (c.moveToNext()) {
            question.setQuestionID(c.getInt(c.getColumnIndex("QuestionID")));
            question.setCourseID(c.getInt(c.getColumnIndex("CourseID")));
            question.setQuestion(c.getString(c.getColumnIndex("Question")));
            question.setChoiceA(c.getString(c.getColumnIndex("ChoiceA")));
            question.setChoiceB(c.getString(c.getColumnIndex("ChoiceB")));
            question.setChoiceC(c.getString(c.getColumnIndex("ChoiceC")));
            question.setChoiceD(c.getString(c.getColumnIndex("ChoiceD")));
            question.setAnswer(c.getString(c.getColumnIndex("Answer")));
            question.setScore(c.getInt(c.getColumnIndex("Score")));
        }
        c.close();
        db.close();
        return question;
    }

    public List<Question> getAllQuestionList() {
        db = sqlitecreate.getWritableDatabase();
        ArrayList<Question> questions = new ArrayList<Question>();
        String sql = "SELECT * FROM question";
        Cursor c = db.rawQuery(sql, null);
        while (c.moveToNext()) {
            Question question = new Question();
            question.setQuestionID(c.getInt(c.getColumnIndex("QuestionID")));
            question.setCourseID(c.getInt(c.getColumnIndex("CourseID")));
            question.setQuestion(c.getString(c.getColumnIndex("Question")));
            question.setChoiceA(c.getString(c.getColumnIndex("ChoiceA")));
            question.setChoiceB(c.getString(c.getColumnIndex("ChoiceB")));
            question.setChoiceC(c.getString(c.getColumnIndex("ChoiceC")));
            question.setChoiceD(c.getString(c.getColumnIndex("ChoiceD")));
            question.setAnswer(c.getString(c.getColumnIndex("Answer")));
            question.setScore(c.getInt(c.getColumnIndex("Score")));
            questions.add(question);
        }
        c.close();
        db.close();
        return questions;
    }

    public void insertQuestionList(Question questions) {
        db = sqlitecreate.getWritableDatabase();
        String sql = "INSERT INTO question (QuestionID,CourseID,Question,ChoiceA,ChoiceB,ChoiceC,ChoiceD,Answer,Score) VALUES (?,?,?,?,?,?,?,?,?)";
        db.beginTransaction();
        try {
            db.execSQL(sql, new Object[]{questions.getQuestionID(), questions.getCourseID(), questions.getQuestion(), questions.getChoiceA(), questions.getChoiceB(), questions.getChoiceC(), questions.getChoiceD(), questions.getAnswer(), questions.getScore()});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    public void insertQuestion(Question questions) {
        db = sqlitecreate.getWritableDatabase();
        String sql = "INSERT INTO question (QuestionID,Question,ChoiceA,ChoiceB,ChoiceC,ChoiceD,Answer,Score) VALUES (?,?,?,?,?,?,?,?)";
        db.beginTransaction();
        try {
            db.execSQL(sql, new Object[]{questions.getQuestionID(), questions.getQuestion(), questions.getChoiceA(), questions.getChoiceB(), questions.getChoiceC(), questions.getChoiceD(), questions.getAnswer(), questions.getScore()});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    public void updateQuestion(Question questions) {
        db = sqlitecreate.getWritableDatabase();
        String sql = "UPDATE question SET Question = ? , ChoiceA = ? , ChoiceB = ? , ChoiceC = ? , ChoiceD = ? , Answer = ? , Score = ? WHERE QuestionID = ?";
        db.beginTransaction();
        try {
            db.execSQL(sql, new Object[]{questions.getQuestion(), questions.getChoiceA(), questions.getChoiceB(), questions.getChoiceC(), questions.getChoiceD(), questions.getAnswer(), questions.getScore(), questions.getQuestionID()});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    public void deleteQuestion() {
        db = sqlitecreate.getWritableDatabase();
        db.delete("question", null, null);
        db.close();
    }

    public void deleteQuestionByID(int QuestionID) {
        db = sqlitecreate.getWritableDatabase();
        db.delete("question", "QuestionID = " + QuestionID, null);
        db.close();
    }
}
