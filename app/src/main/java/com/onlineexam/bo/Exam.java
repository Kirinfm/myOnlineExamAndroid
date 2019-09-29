package com.onlineexam.bo;

import java.io.Serializable;

/**
 * Created by 方明 on 2017/3/19.
 */

public class Exam implements Serializable {
    private int CourseID;
    private String CourseName;
    private String CreateDate;
    private String DeadLine;
    private String Context;
    private int Score;

    public int getCourseID() {
        return CourseID;
    }

    public void setCourseID(int courseID) {
        CourseID = courseID;
    }

    public String getCourseName() {
        return CourseName;
    }

    public void setCourseName(String courseName) {
        CourseName = courseName;
    }

    public String getCreateDate() {
        return CreateDate;
    }

    public void setCreateDate(String createDate) {
        CreateDate = createDate;
    }

    public String getDeadLine() {
        return DeadLine;
    }

    public void setDeadLine(String deadLine) {
        DeadLine = deadLine;
    }

    public String getContext() {
        return Context;
    }

    public void setContext(String context) {
        Context = context;
    }

    public int getScore() {
        return Score;
    }

    public void setScore(int score) {
        Score = score;
    }
}
