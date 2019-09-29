package com.onlineexam.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.onlineexam.util.SessionManager;
import com.onlineexam.util.URLConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private LoginModel loginmodel;
    private SessionManager session;
    private ProgressDialog dialog;
    private Button login_button;
    private TextView account;
    private EditText inputusername;
    private EditText inputpassword;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginmodel = new LoginModel(this);

        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置进度条的形式为圆形转动的进度条
        dialog.setCancelable(true);// 设置是否可以通过点击Back键取消

        session = new SessionManager(getApplicationContext());
        if (session.isLoggedIn()) {
            if (loginmodel.getUserinfo().get(0).getIsteacher() == 1){
                Intent intent = new Intent(LoginActivity.this, T_MainActivity.class);
                startActivity(intent);
                finish();
            }else {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }

        login_button = (Button) findViewById(R.id.Login);
        account = (TextView) findViewById(R.id.account);
        inputusername = (EditText) findViewById(R.id.username);
        inputpassword = (EditText) findViewById(R.id.password);

        login_button.setOnClickListener(new View.OnClickListener() {
            //点击进入主页
            public void onClick(View v) {
                String str = inputusername.getText().toString();
                String password = inputpassword.getText().toString();
                if (str.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "输入用户名和密码", Toast.LENGTH_SHORT).show();
                } else {
                    int username = Integer.parseInt(str);
                    LoginAction(username, password);
                }
            }
        });

        account.setOnClickListener(new View.OnClickListener() {
            //点击进入注册页面
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    public void LoginAction(final int no, final String password) {
        RequestQueue rq = Volley.newRequestQueue(getApplicationContext());

        dialog.setMessage("正在登录...");
        dialog.show();
        StringRequest Req = new StringRequest(Request.Method.POST, URLConfig.Login_url, new Response.Listener<String>() {

            public void onResponse(String response) {
                dialog.dismiss();
                try {
                    JSONObject jobj = new JSONObject(response);
                    boolean error = jobj.getBoolean("error");
                    Log.d("Name", response.toString());
                    if (!error) {
                        JSONObject userobj = jobj.getJSONObject("userinfo");
                        isTeacher(userobj);
                        session.setLogin(true);
                    } else {
                        String errormsg = jobj.getString("errormsg");
                        Toast.makeText(getApplicationContext(), errormsg, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "登录失败", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            public void onErrorResponse(VolleyError volleyError) {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "登录失败", Toast.LENGTH_SHORT).show();
            }
        }) {

            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", no + "");
                params.put("password", password);
                return params;
            }
        };
        rq.add(Req);
//        ArrayList<User> users = new ArrayList<User>();
//        User user = new User();
//        user.setSchoolid(no);
//        user.setPassword(password);
//        users = (ArrayList<User>) loginmodel.getUserinfo(user);
//        return users;
    }

    public void isTeacher(JSONObject jsonObject) {
        try {
            User user = new User();
            user.setSchoolid(jsonObject.getInt("SchoolID"));
            user.setName(jsonObject.getString("Name"));
            user.setPassword(jsonObject.getString("Password"));
            user.setTelNo(jsonObject.getString("TelNo"));
            user.setPhoto(jsonObject.getString("Photo"));
            user.setIsteacher(jsonObject.getInt("IsTeacher"));
            loginmodel.insertUserinfo(user);
            if (jsonObject.getInt("IsTeacher") == 1) {
                Intent intent = new Intent(LoginActivity.this, T_MainActivity.class);
                //intent.putExtra("userinfo", user);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                //intent.putExtra("userinfo", user);
                startActivity(intent);
                finish();
            }
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "登录失败", Toast.LENGTH_SHORT).show();
        }
    }
}
