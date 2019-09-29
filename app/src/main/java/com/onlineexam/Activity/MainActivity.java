package com.onlineexam.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.onlineexam.Model.ExamModel;
import com.onlineexam.Model.LoginModel;
import com.onlineexam.Model.QuestionModel;
import com.onlineexam.R;
import com.onlineexam.bo.Exam;
import com.onlineexam.bo.Question;
import com.onlineexam.bo.User;
import com.onlineexam.util.SessionManager;
import com.onlineexam.util.URLConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private LoginModel loginModel;
    private ExamModel examModel;
    private QuestionModel questionModel;
    private SessionManager session;
    private ProgressDialog dialog;
    private User user;
    private TextView schoolid;
    private TextView name;
    private TextView telno;
    private Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置进度条的形式为圆形转动的进度条
        dialog.setCancelable(true);// 设置是否可以通过点击Back键取消

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            logout();
        }

        //User user = (User) getIntent().getSerializableExtra("userinfo");
        loginModel = new LoginModel(this);
        examModel = new ExamModel(this);
        questionModel = new QuestionModel(this);
        examModel.deleteExam();
        questionModel.deleteQuestion();
        this.user = loginModel.getUserinfo().get(0);
        schoolid = (TextView) findViewById(R.id.schoolid);
        name = (TextView) findViewById(R.id.name);
        telno = (TextView) findViewById(R.id.telno);
        schoolid.setText(user.getSchoolid() + "");
        name.setText(user.getName());
        telno.setText(user.getTelNo());

        logout = (Button) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    public void logout() {
        loginModel.deleteUserinfo();
        examModel.deleteExam();
        questionModel.deleteQuestion();
        session.setLogin(false);
        Toast.makeText(this, "退出账号", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_test) {
            getExamList(this.user.getSchoolid());
        } else if (id == R.id.nav_Score) {
            getScore(this.user.getSchoolid());
        } else if (id == R.id.nav_pick) {
            getPickCourse(this.user.getSchoolid());
        } else if (id == R.id.nav_setting) {
            Intent intent = new Intent(this,UpdateinfoActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void getExamList(final int no) {
        RequestQueue rq = Volley.newRequestQueue(getApplicationContext());

        dialog.setMessage("正在获取试题...");
        dialog.show();

        final List<Exam> exams = new ArrayList<Exam>();

        StringRequest Req = new StringRequest(Request.Method.POST, URLConfig.GetExamList_url, new Response.Listener<String>() {

            public void onResponse(String response) {
                dialog.dismiss();
                examModel = new ExamModel(getApplicationContext());
                try {
                    JSONObject jobj = new JSONObject(response);
                    boolean error = jobj.getBoolean("error");
                    Log.d("msg", response.toString());
                    if (!error) {
                        JSONArray examlist = jobj.getJSONArray("examlist");
                        for (int i = 0; i < examlist.length(); i++) {
                            JSONObject examlistobj = examlist.getJSONObject(i);
                            Exam exam = new Exam();
                            exam.setCourseID(examlistobj.getInt("CourseID"));
                            exam.setCourseName(examlistobj.getString("CourseName"));
                            exam.setCreateDate(examlistobj.getString("CreateDate"));
                            exam.setDeadLine(examlistobj.getString("DeadLine"));
                            exam.setContext(examlistobj.getString("Context"));
                            examModel.insertExamList(exam);
                            insertQuestions(exam.getCourseID());
                        }
                        Intent intent = new Intent(MainActivity.this, ExamListActivity.class);
                        startActivity(intent);
                    } else {
                        String errormsg = jobj.getString("errormsg");
                        Toast.makeText(getApplicationContext(), errormsg, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    try {
                        JSONObject jobj = new JSONObject(response);
                        boolean error = jobj.getBoolean("error");
                        if (!error) {
                            JSONObject examlist = jobj.getJSONObject("examlist");
                            Exam exam = new Exam();
                            exam.setCourseID(examlist.getInt("CourseID"));
                            exam.setCourseName(examlist.getString("CourseName"));
                            exam.setCreateDate(examlist.getString("CreateDate"));
                            exam.setDeadLine(examlist.getString("DeadLine"));
                            exam.setContext(examlist.getString("Context"));
                            examModel.insertExamList(exam);
                            insertQuestions(exam.getCourseID());
                            Intent intent = new Intent(MainActivity.this, ExamListActivity.class);
                            startActivity(intent);
                        } else {
                            String errormsg = jobj.getString("errormsg");
                            Toast.makeText(getApplicationContext(), errormsg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                        Toast.makeText(getApplicationContext(), "获取试题失败", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        }, new Response.ErrorListener() {

            public void onErrorResponse(VolleyError volleyError) {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "获取数据失败", Toast.LENGTH_SHORT).show();
            }
        }) {

            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("SchoolID", no + "");
                return params;
            }
        };
        rq.add(Req);
    }

    public void insertQuestions(final int CourseID) {
        RequestQueue rq = Volley.newRequestQueue(getApplicationContext());
        StringRequest Req = new StringRequest(Request.Method.POST, URLConfig.GetQuestionList_url, new Response.Listener<String>() {

            public void onResponse(String response) {
                questionModel = new QuestionModel(getApplicationContext());
                try {
                    JSONObject jobj = new JSONObject(response);
                    boolean error = jobj.getBoolean("error");
                    Log.d("msg", response.toString());
                    if (!error) {
                        JSONArray questionlist = jobj.getJSONArray("questionlist");
                        for (int i = 0; i < questionlist.length(); i++) {
                            JSONObject questionlistobj = questionlist.getJSONObject(i);
                            Question question = new Question();
                            question.setQuestionID(questionlistobj.getInt("QuestionID"));
                            question.setCourseID(CourseID);
                            question.setQuestion(questionlistobj.getString("Question"));
                            question.setChoiceA(questionlistobj.getString("ChoiceA"));
                            question.setChoiceB(questionlistobj.getString("ChoiceB"));
                            question.setChoiceC(questionlistobj.getString("ChoiceC"));
                            question.setChoiceD(questionlistobj.getString("ChoiceD"));
                            question.setAnswer(questionlistobj.getString("Answer"));
                            question.setScore(questionlistobj.getInt("Score"));
                            questionModel.insertQuestionList(question);
                        }
                    } else {
                        String errormsg = jobj.getString("errormsg");
                        Toast.makeText(getApplicationContext(), errormsg, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    try {
                        JSONObject jobj = new JSONObject(response);
                        boolean error = jobj.getBoolean("error");
                        if (!error) {
                            JSONObject questionlist = jobj.getJSONObject("questionlist");
                            Question question = new Question();
                            question.setQuestionID(questionlist.getInt("QuestionID"));
                            question.setCourseID(CourseID);
                            question.setQuestion(questionlist.getString("Question"));
                            question.setChoiceA(questionlist.getString("ChoiceA"));
                            question.setChoiceB(questionlist.getString("ChoiceB"));
                            question.setChoiceC(questionlist.getString("ChoiceC"));
                            question.setChoiceD(questionlist.getString("ChoiceD"));
                            question.setAnswer(questionlist.getString("Answer"));
                            question.setScore(questionlist.getInt("Score"));
                            questionModel.insertQuestionList(question);
                        } else {
                            String errormsg = jobj.getString("errormsg");
                            Toast.makeText(getApplicationContext(), errormsg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }

                }
            }
        }, new Response.ErrorListener() {

            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(), "获取数据失败", Toast.LENGTH_SHORT).show();
            }
        }) {

            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("CourseID", CourseID + "");
                return params;
            }
        };
        rq.add(Req);
    }

    public void getScore(final int no) {
        RequestQueue rq = Volley.newRequestQueue(getApplicationContext());

        dialog.setMessage("正在获取数据...");
        dialog.show();

        StringRequest Req = new StringRequest(Request.Method.POST, URLConfig.GetScore_url, new Response.Listener<String>() {

            public void onResponse(String response) {
                dialog.dismiss();
                try {
                    JSONObject jobj = new JSONObject(response);
                    boolean error = jobj.getBoolean("error");
                    Log.d("msg", response.toString());
                    if (!error) {
                        JSONArray scorelist = jobj.getJSONArray("scorelist");
                        List<Exam> scores = new ArrayList<Exam>();
                        for (int i = 0; i < scorelist.length(); i++) {
                            JSONObject scorelistobj = scorelist.getJSONObject(i);
                            Exam score = new Exam();
                            score.setCourseID(scorelistobj.getInt("CourseID"));
                            score.setCourseName(scorelistobj.getString("CourseName"));
                            score.setScore(scorelistobj.getInt("Score"));
                            scores.add(score);
                        }
                        Intent intent = new Intent(MainActivity.this, ScoreActivity.class);
                        intent.putExtra("scorelist", (Serializable) scores);
                        startActivity(intent);
                    } else {
                        String errormsg = jobj.getString("errormsg");
                        Toast.makeText(getApplicationContext(), errormsg, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    try {
                        JSONObject jobj = new JSONObject(response);
                        boolean error = jobj.getBoolean("error");
                        if (!error) {
                            List<Exam> scores = new ArrayList<Exam>();
                            JSONObject scorelistobj = jobj.getJSONObject("examlist");
                            Exam score = new Exam();
                            score.setCourseID(scorelistobj.getInt("CourseID"));
                            score.setCourseName(scorelistobj.getString("CourseName"));
                            score.setScore(scorelistobj.getInt("Score"));
                            scores.add(score);
                            Intent intent = new Intent(MainActivity.this, ScoreActivity.class);
                            intent.putExtra("scorelist", (Serializable) scores);
                            startActivity(intent);
                        } else {
                            String errormsg = jobj.getString("errormsg");
                            Toast.makeText(getApplicationContext(), errormsg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                        Toast.makeText(getApplicationContext(), "获取成绩失败", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        }, new Response.ErrorListener() {

            public void onErrorResponse(VolleyError volleyError) {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "获取数据失败", Toast.LENGTH_SHORT).show();
            }
        }) {

            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("SchoolID", no + "");
                return params;
            }
        };
        rq.add(Req);
    }

    public void getPickCourse(final int no) {
        RequestQueue rq = Volley.newRequestQueue(getApplicationContext());

        dialog.setMessage("正在获取数据...");
        dialog.show();

        StringRequest Req = new StringRequest(Request.Method.POST, URLConfig.GetCourse_url, new Response.Listener<String>() {

            public void onResponse(String response) {
                dialog.dismiss();
                try {
                    JSONObject jobj = new JSONObject(response);
                    boolean error = jobj.getBoolean("error");
                    Log.d("msg", response.toString());
                    if (!error) {
                        JSONArray courselist = jobj.getJSONArray("courselist");
                        List<Exam> courses = new ArrayList<Exam>();
                        for (int i = 0; i < courselist.length(); i++) {
                            JSONObject courselistobj = courselist.getJSONObject(i);
                            Exam course = new Exam();
                            course.setCourseID(courselistobj.getInt("CourseID"));
                            course.setCourseName(courselistobj.getString("CourseName"));
                            courses.add(course);
                        }
                        Intent intent = new Intent(MainActivity.this, PickActivity.class);
                        intent.putExtra("courselist", (Serializable) courses);
                        startActivity(intent);
                    } else {
                        String errormsg = jobj.getString("errormsg");
                        Toast.makeText(getApplicationContext(), errormsg, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    try {
                        JSONObject jobj = new JSONObject(response);
                        boolean error = jobj.getBoolean("error");
                        if (!error) {
                            List<Exam> courses = new ArrayList<Exam>();
                            JSONObject courselistobj = jobj.getJSONObject("courselist");
                            Exam course = new Exam();
                            course.setCourseID(courselistobj.getInt("CourseID"));
                            course.setCourseName(courselistobj.getString("CourseName"));
                            courses.add(course);
                            Intent intent = new Intent(MainActivity.this, PickActivity.class);
                            intent.putExtra("courselist", (Serializable) courses);
                            startActivity(intent);
                        } else {
                            String errormsg = jobj.getString("errormsg");
                            Toast.makeText(getApplicationContext(), errormsg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                        Toast.makeText(getApplicationContext(), "获取数据失败", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        }, new Response.ErrorListener() {

            public void onErrorResponse(VolleyError volleyError) {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "获取数据失败", Toast.LENGTH_SHORT).show();
            }
        }) {

            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("SchoolID", no + "");
                return params;
            }
        };
        rq.add(Req);
    }
}
