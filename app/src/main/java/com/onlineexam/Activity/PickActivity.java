package com.onlineexam.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.onlineexam.Model.LoginModel;
import com.onlineexam.R;
import com.onlineexam.bo.Exam;
import com.onlineexam.util.CheckboxListAdapter;
import com.onlineexam.util.URLConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PickActivity extends AppCompatActivity {
    private ListView lv;
    private Button submit;
    private LoginModel loginModel;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loginModel = new LoginModel(this);

        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置进度条的形式为圆形转动的进度条
        dialog.setCancelable(true);// 设置是否可以通过点击Back键取消

        lv = (ListView) findViewById(R.id.list);
        List<Exam> courses = (List<Exam>) getIntent().getSerializableExtra("courselist");
        List<HashMap<String, String>> data = new ArrayList<>();
        for (int i = 0; i < courses.size(); i++) {
            HashMap<String, String> datamap = new HashMap<>();
            datamap.put("CourseName", courses.get(i).getCourseName());
            datamap.put("CourseID", courses.get(i).getCourseID() + "");
            data.add(datamap);
        }
        final CheckboxListAdapter adapter = new CheckboxListAdapter(this, data);
        lv.setAdapter(adapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Boolean> isSelected = adapter.getIsSelected();
                String CourseIDArr = "";
                for (String key : isSelected.keySet()) {
                    if (isSelected.get(key)) {
                        CourseIDArr += key + " ";
                    }
                }
                CourseIDArr = CourseIDArr.trim();
                Submit(loginModel.getUserinfo().get(0).getSchoolid(), CourseIDArr);
            }
        });
    }

    public void Submit(final int no, final String CourseIDArr) {
        RequestQueue rq = Volley.newRequestQueue(getApplicationContext());

        dialog.setMessage("正在提交...");
        dialog.show();

        StringRequest Req = new StringRequest(Request.Method.POST, URLConfig.Pick_url, new Response.Listener<String>() {

            public void onResponse(String response) {
                dialog.dismiss();
                try {
                    JSONObject jobj = new JSONObject(response);
                    boolean error = jobj.getBoolean("error");
                    Log.d("msg", response.toString());
                    if (!error) {
                        Toast.makeText(getApplicationContext(), "选课成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(PickActivity.this, MainActivity.class);
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
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "提交数据失败", Toast.LENGTH_SHORT).show();
            }
        }) {

            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("SchoolID", no + "");
                params.put("CourseIDArr",CourseIDArr);
                return params;
            }
        };
        rq.add(Req);
    }
}

