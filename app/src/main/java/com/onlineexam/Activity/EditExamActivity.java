package com.onlineexam.Activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.onlineexam.Model.ExamModel;
import com.onlineexam.Model.QuestionModel;
import com.onlineexam.R;
import com.onlineexam.bo.Question;
import com.onlineexam.util.TListViewAdapter;
import com.onlineexam.util.URLConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditExamActivity extends AppCompatActivity {
    private ListView listView;
    private QuestionModel questionModel;
    private ExamModel examModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_exam);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = (ListView) findViewById(R.id.list);
        questionModel = new QuestionModel(this);
        List<Question> list = questionModel.getAllQuestionList();
        final List<HashMap<String, String>> data = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            HashMap<String, String> map = new HashMap<>();
            map.put("QuestionID", list.get(i).getQuestionID() + "");
            map.put("Question", list.get(i).getQuestion());
            map.put("Score", list.get(i).getScore() + "");
            data.add(map);
        }
        final TListViewAdapter adapter = new TListViewAdapter(this, data, R.layout.t_list_layout, new String[]{"Question", "Score"}, new int[]{R.id.title, R.id.Score});
        listView.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> map = new HashMap<>();
                map.put("Question", "新建试题");
                map.put("Score", 0 + "");
                insertQuestion("新建试题", map);
                data.add(map);
                adapter.notifyDataSetChanged();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void insertQuestion(final String str, final HashMap<String, String> map) {
        examModel = new ExamModel(getApplicationContext());
        questionModel = new QuestionModel(getApplicationContext());
        RequestQueue rq = Volley.newRequestQueue(getApplicationContext());
        StringRequest Req = new StringRequest(Request.Method.POST, URLConfig.InsertQuestion_url, new Response.Listener<String>() {

            public void onResponse(String response) {
                try {
                    JSONObject jobj = new JSONObject(response);
                    boolean error = jobj.getBoolean("error");
                    Log.d("msg", response.toString());
                    if (!error) {
                        JSONArray questions = jobj.getJSONArray("question");
                        JSONObject question = questions.getJSONObject(0);
                        Question q = new Question();
                        q.setQuestionID(question.getInt("QuestionID"));
                        q.setQuestion(question.getString("Question"));
                        q.setChoiceA(question.getString("ChoiceA"));
                        q.setChoiceB(question.getString("ChoiceB"));
                        q.setChoiceC(question.getString("ChoiceC"));
                        q.setChoiceD(question.getString("ChoiceD"));
                        q.setAnswer(question.getString("Answer"));
                        q.setScore(question.getInt("Score"));
                        questionModel.insertQuestion(q);
                        map.put("QuestionID", q.getQuestionID() + "");
                        Toast.makeText(getApplicationContext(), "插入成功", Toast.LENGTH_SHORT).show();
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
                params.put("Question", str);
                params.put("CourseID", examModel.getExamList().get(0).getCourseID() + "");
                return params;
            }
        };
        rq.add(Req);
    }

}
