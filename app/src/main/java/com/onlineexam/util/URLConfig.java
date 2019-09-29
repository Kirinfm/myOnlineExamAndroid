package com.onlineexam.util;

/**
 * Created by 方明 on 2017/3/12.
 */

public class URLConfig {
    //由于Android机没有修改host文件，使用ip来访问网址，192.168.1.103为作为服务端电脑的ip
    //Login url
    public static String Login_url = "http://192.168.1.103/onlineexamtest/index/index/login";
    public static String Register_url = "http://192.168.1.103/onlineexamtest/index/index/register";
    public static String GetExamList_url = "http://192.168.1.103/onlineexamtest/index/exam/index";
    public static String GetQuestionList_url = "http://192.168.1.103/onlineexamtest/index/exam/getquestion";
    public static String Handleup_url = "http://192.168.1.103/onlineexamtest/index/exam/handleup";
    public static String GetScore_url = "http://192.168.1.103/onlineexamtest/index/others/getscore";
    public static String GetCourse_url = "http://192.168.1.103/onlineexamtest/index/others/getcourse";
    public static String Pick_url = "http://192.168.1.103/onlineexamtest/index/others/pick";
    public static String Update_url = "http://192.168.1.103/onlineexamtest/index/others/updateuserinfo";
    //Teacher
    public static String GetTQuestionList_url = "http://192.168.1.103/onlineexamtest/index/t_exam/getquestion";
    public static String GetTCourse_url = "http://192.168.1.103/onlineexamtest/index/t_exam/getcourse";
    public static String InsertQuestion_url = "http://192.168.1.103/onlineexamtest/index/t_exam/insertquestion";
    public static String DeleteQuestion_url = "http://192.168.1.103/onlineexamtest/index/t_exam/deletequestion";
    public static String UpdateQuestion_url = "http://192.168.1.103/onlineexamtest/index/t_exam/updatequestion";
    public static String GetTScore_url = "http://192.168.1.103/onlineexamtest/index/t_others/getscore";
}
