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

public class T_MainActivity extends AppCompatActivity
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
        setContentView(R.layout.activity_t__main);
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
        Intent intent = new Intent(T_MainActivity.this, LoginActivity.class);
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
        loginModel = new LoginModel(getApplicationContext());

        if (id == R.id.nav_editquestion) {
            insertExam(loginModel.getUserinfo().get(0).getSchoolid());
            insertQuestions(loginModel.getUserinfo().get(0).getSchoolid());
        } else if (id == R.id.nav_editstudent) {
            getScore(loginModel.getUserinfo().get(0).getSchoolid());
        } else if (id == R.id.nav_setting) {
            Intent intent = new Intent(this, UpdateTeacherinfoActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void insertQuestions(final int no) {
        dialog.setMessage("正在获取数据...");
        dialog.show();
        RequestQueue rq = Volley.newRequestQueue(getApplicationContext());
        StringRequest Req = new StringRequest(Request.Method.POST, URLConfig.GetTQuestionList_url, new Response.Listener<String>() {

            public void onResponse(String response) {
                dialog.dismiss();
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
                            question.setCourseID(questionlistobj.getInt("CourseID"));
                            question.setQuestion(questionlistobj.getString("Question"));
                            question.setChoiceA(questionlistobj.getString("ChoiceA"));
                            question.setChoiceB(questionlistobj.getString("ChoiceB"));
                            question.setChoiceC(questionlistobj.getString("ChoiceC"));
                            question.setChoiceD(questionlistobj.getString("ChoiceD"));
                            question.setAnswer(questionlistobj.getString("Answer"));
                            question.setScore(questionlistobj.getInt("Score"));
                            questionModel.insertQuestionList(question);
                        }
                        Intent intent = new Intent(T_MainActivity.this,EditExamActivity.class);
                        startActivity(intent);
                    } else {
                        String errormsg = jobj.getString("errormsg");
                        Toast.makeText(getApplicationContext(), errormsg, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(T_MainActivity.this,EditExamActivity.class);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    try {
                        JSONObject jobj = new JSONObject(response);
                        boolean error = jobj.getBoolean("error");
                        if (!error) {
                            JSONObject questionlist = jobj.getJSONObject("questionlist");
                            Question question = new Question();
                            question.setQuestionID(questionlist.getInt("QuestionID"));
                            question.setCourseID(questionlist.getInt("CourseID"));
                            question.setQuestion(questionlist.getString("Question"));
                            question.setChoiceA(questionlist.getString("ChoiceA"));
                            question.setChoiceB(questionlist.getString("ChoiceB"));
                            question.setChoiceC(questionlist.getString("ChoiceC"));
                            question.setChoiceD(questionlist.getString("ChoiceD"));
                            question.setAnswer(questionlist.getString("Answer"));
                            question.setScore(questionlist.getInt("Score"));
                            questionModel.insertQuestionList(question);
                            Intent intent = new Intent(T_MainActivity.this,EditExamActivity.class);
                            startActivity(intent);
                        } else {
                            String errormsg = jobj.getString("errormsg");
                            Toast.makeText(getApplicationContext(), errormsg, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(T_MainActivity.this,EditExamActivity.class);
                            startActivity(intent);
                        }
                    } catch (JSONException e1) {
                        e1.printStackTrace();
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
                params.put("TeacherID", no + "");
                return params;
            }
        };
        rq.add(Req);
    }

    public void insertExam(final int no) {
        RequestQueue rq = Volley.newRequestQueue(getApplicationContext());
        StringRequest Req = new StringRequest(Request.Method.POST, URLConfig.GetTCourse_url, new Response.Listener<String>() {

            public void onResponse(String response) {
                examModel = new ExamModel(getApplicationContext());
                try {
                    JSONObject jobj = new JSONObject(response);
                    boolean error = jobj.getBoolean("error");
                    Log.d("msg", response.toString());
                    if (!error) {
                        JSONArray courses = jobj.getJSONArray("course");
                        JSONObject course = courses.getJSONObject(0);
                        Exam exam = new Exam();
                        exam.setCourseID(course.getInt("CourseID"));
                        exam.setCourseName(course.getString("CourseName"));
                        examModel.insertExam(exam);
                    } else {
                        String errormsg = jobj.getString("errormsg");
                        Toast.makeText(getApplicationContext(), errormsg, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(), "获取数据失败", Toast.LENGTH_SHORT).show();
            }
        }) {

            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("TeacherID", no + "");
                return params;
            }
        };
        rq.add(Req);
    }

    public void getScore(final int no) {
        RequestQueue rq = Volley.newRequestQueue(getApplicationContext());

        dialog.setMessage("正在获取数据...");
        dialog.show();

        StringRequest Req = new StringRequest(Request.Method.POST, URLConfig.GetTScore_url, new Response.Listener<String>() {

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
                            score.setCourseID(scorelistobj.getInt("StudentID"));
                            score.setCourseName(scorelistobj.getString("Name"));
                            score.setScore(scorelistobj.getInt("Score"));
                            scores.add(score);
                        }
                        Intent intent = new Intent(T_MainActivity.this, EditStudentActivity.class);
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
                            score.setCourseID(scorelistobj.getInt("StudentID"));
                            score.setCourseName(scorelistobj.getString("Name"));
                            score.setScore(scorelistobj.getInt("Score"));
                            scores.add(score);
                            Intent intent = new Intent(T_MainActivity.this, EditStudentActivity.class);
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
                params.put("TeacherID", no + "");
                return params;
            }
        };
        rq.add(Req);
    }
}
