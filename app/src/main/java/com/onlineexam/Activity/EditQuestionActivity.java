package com.onlineexam.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.onlineexam.Model.QuestionModel;
import com.onlineexam.R;
import com.onlineexam.bo.Question;
import com.onlineexam.util.URLConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditQuestionActivity extends AppCompatActivity {
    private QuestionModel questionModel;
    private EditText question;
    private EditText choiceA;
    private EditText choiceB;
    private EditText choiceC;
    private EditText choiceD;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_question);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        questionModel = new QuestionModel(this);
        radioGroup = (RadioGroup) findViewById(R.id.choices);
        submit = (Button) findViewById(R.id.submit);
        question = (EditText) findViewById(R.id.question);
        choiceA = (EditText) findViewById(R.id.ChoiceA);
        choiceB = (EditText) findViewById(R.id.ChoiceB);
        choiceC = (EditText) findViewById(R.id.ChoiceC);
        choiceD = (EditText) findViewById(R.id.ChoiceD);
        final int QuestionID = getIntent().getIntExtra("QuestionID", -1);
        if (QuestionID != -1) {
            Question q = questionModel.getQuestionById(QuestionID);
            question.setText(q.getQuestion());
            choiceA.setText(q.getChoiceA());
            choiceB.setText(q.getChoiceB());
            choiceC.setText(q.getChoiceC());
            choiceD.setText(q.getChoiceD());
            switch (q.getAnswer()) {
                case "A":
                    radioButton = (RadioButton) radioGroup.getChildAt(0);
                    radioButton.setChecked(true);
                    break;
                case "B":
                    radioButton = (RadioButton) radioGroup.getChildAt(1);
                    radioButton.setChecked(true);
                    break;
                case "C":
                    radioButton = (RadioButton) radioGroup.getChildAt(2);
                    radioButton.setChecked(true);
                    break;
                case "D":
                    radioButton = (RadioButton) radioGroup.getChildAt(3);
                    radioButton.setChecked(true);
                    break;
            }
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String question_text = question.getText().toString();
                    String choiceA_text = choiceA.getText().toString();
                    String choiceB_text = choiceB.getText().toString();
                    String choiceC_text = choiceC.getText().toString();
                    String choiceD_text = choiceD.getText().toString();
                    radioButton = (RadioButton) radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
                    if (radioButton == null) {
                        Toast.makeText(getApplicationContext(), "请设置答案", Toast.LENGTH_SHORT).show();
                    } else {
                        String answer = null;
                        switch (radioGroup.indexOfChild(radioButton)) {
                            case 0:
                                answer = "A";
                                break;
                            case 1:
                                answer = "B";
                                break;
                            case 2:
                                answer = "C";
                                break;
                            case 3:
                                answer = "D";
                                break;
                        }
                        Question q = new Question();
                        q.setQuestionID(QuestionID);
                        q.setQuestion(question_text);
                        q.setChoiceA(choiceA_text);
                        q.setChoiceB(choiceB_text);
                        q.setChoiceC(choiceC_text);
                        q.setChoiceD(choiceD_text);
                        q.setAnswer(answer);
                        Dialog(v, q);
                    }
                }
            });
        }
    }

    public void Dialog(View v, final Question q) {
        final EditText editText = new EditText(v.getContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setTitle("输入分值（1-100整数）");
        builder.setView(editText);
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确认提交", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                q.setScore(Integer.parseInt(editText.getText().toString()));
                updateQuestion(q);
            }
        });
        builder.show();
    }

    public void updateQuestion(final Question q) {
        RequestQueue rq = Volley.newRequestQueue(getApplicationContext());
        StringRequest Req = new StringRequest(Request.Method.POST, URLConfig.UpdateQuestion_url, new Response.Listener<String>() {

            public void onResponse(String response) {
                try {
                    JSONObject jobj = new JSONObject(response);
                    boolean error = jobj.getBoolean("error");
                    Log.d("msg", response.toString());
                    if (!error) {
                        questionModel = new QuestionModel(getApplicationContext());
                        questionModel.updateQuestion(q);
                        Toast.makeText(getApplicationContext(), "更新完成", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditQuestionActivity.this, EditExamActivity.class);
                        startActivity(intent);
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
                Toast.makeText(getApplicationContext(), "提交数据失败", Toast.LENGTH_SHORT).show();
            }
        }) {

            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("QuestionID", q.getQuestionID() + "");
                params.put("Question", q.getQuestion());
                params.put("ChoiceA", q.getChoiceA());
                params.put("ChoiceB", q.getChoiceB());
                params.put("ChoiceC", q.getChoiceC());
                params.put("ChoiceD", q.getChoiceD());
                params.put("Answer", q.getAnswer());
                params.put("Score", q.getScore() + "");
                return params;
            }
        };
        rq.add(Req);
    }
}

