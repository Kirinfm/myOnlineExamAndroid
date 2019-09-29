package com.onlineexam.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import com.onlineexam.bo.User;
import com.onlineexam.util.URLConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateTeacherinfoActivity extends AppCompatActivity {
    private LoginModel loginModel;
    private TextView schoolid;
    private TextView name;
    private EditText telno;
    private EditText psd;
    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_teacherinfo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loginModel = new LoginModel(this);
        schoolid = (TextView) findViewById(R.id.schoolid);
        name = (TextView) findViewById(R.id.name);
        telno = (EditText) findViewById(R.id.telno);
        psd = (EditText) findViewById(R.id.psd);
        submit = (Button) findViewById(R.id.submit);

        final User user = loginModel.getUserinfo().get(0);
        schoolid.setText(user.getSchoolid() + "");
        name.setText(user.getName());
        telno.setText(user.getTelNo());
        psd.setText(user.getPassword());

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String gettelno = telno.getText().toString();
                String getpsd = psd.getText().toString();
                if (!(gettelno.equals(user.getTelNo())) || !(getpsd.equals(user.getPassword()))) {
                    if (checkTelno(gettelno)) {
                        updateinfo(gettelno, getpsd, user.getSchoolid());
                    } else {
                        Toast.makeText(getApplicationContext(), "输入正确的手机号", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "没有进行任何更改", Toast.LENGTH_SHORT).show();
                }
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public boolean checkTelno(String telno) {
        Pattern pattern = null;
        Matcher matcher = null;
        boolean flag = false;
        pattern = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$"); // 验证手机号
        matcher = pattern.matcher(telno);
        flag = matcher.matches();
        return flag;
    }

    public void updateinfo(final String telno, final String psd, final int no) {
        final LoginModel loginModel = new LoginModel(getApplicationContext());
        RequestQueue rq = Volley.newRequestQueue(getApplicationContext());
        StringRequest Req = new StringRequest(Request.Method.POST, URLConfig.Update_url, new Response.Listener<String>() {

            public void onResponse(String response) {
                Log.d("msg", response.toString());
                try {
                    JSONObject jobj = new JSONObject(response);
                    boolean error = jobj.getBoolean("error");
                    if (!error) {
                        User user = new User();
                        user.setPassword(psd);
                        user.setTelNo(telno);
                        user.setSchoolid(no);
                        loginModel.updateUserinfo(user);
                        Toast.makeText(getApplicationContext(), "修改完成!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UpdateTeacherinfoActivity.this, T_MainActivity.class);
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
                Toast.makeText(getApplicationContext(), "修改失败", Toast.LENGTH_SHORT).show();
            }
        }) {

            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("SchoolID", no + "");
                params.put("Password", psd);
                params.put("TelNO", telno);
                return params;
            }
        };
        rq.add(Req);
    }
}
