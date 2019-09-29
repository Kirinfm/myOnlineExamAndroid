package com.onlineexam.bo;

import java.io.Serializable;

/**
 * Created by 方明 on 2017/3/22.
 */

public class Question implements Serializable {
    private int QuestionID;
    private int CourseID;
    private String Question;
    private String ChoiceA;
    private String ChoiceB;
    private String ChoiceC;
    private String ChoiceD;
    private String Answer;
    private int Score;

    public int getQuestionID() {
        return QuestionID;
    }

    public void setQuestionID(int questionID) {
        QuestionID = questionID;
    }

    public int getCourseID() {
        return CourseID;
    }

    public void setCourseID(int courseID) {
        CourseID = courseID;
    }

    public String getQuestion() {
        return Question;
    }

    public void setQuestion(String question) {
        Question = question;
    }

    public String getChoiceA() {
        return ChoiceA;
    }

    public void setChoiceA(String choiceA) {
        ChoiceA = choiceA;
    }

    public String getChoiceB() {
        return ChoiceB;
    }

    public void setChoiceB(String choiceB) {
        ChoiceB = choiceB;
    }

    public String getChoiceC() {
        return ChoiceC;
    }

    public void setChoiceC(String choiceC) {
        ChoiceC = choiceC;
    }

    public String getChoiceD() {
        return ChoiceD;
    }

    public void setChoiceD(String choiceD) {
        ChoiceD = choiceD;
    }

    public String getAnswer() {
        return Answer;
    }

    public void setAnswer(String answer) {
        Answer = answer;
    }

    public int getScore() {
        return Score;
    }

    public void setScore(int score) {
        Score = score;
    }
}
