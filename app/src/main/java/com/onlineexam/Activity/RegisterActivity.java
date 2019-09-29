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
import com.onlineexam.R;
import com.onlineexam.util.URLConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private ProgressDialog dialog;
    private Button register_button;
    private TextView account;
    private EditText inputusername;
    private EditText inputpassword;
    private EditText inputpsd;
    private EditText inputtelno;
    private EditText inputinfoname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置进度条的形式为圆形转动的进度条
        dialog.setCancelable(true);// 设置是否可以通过点击Back键取消

        register_button = (Button) findViewById(R.id.Register);
        account = (TextView) findViewById(R.id.account);
        inputusername = (EditText) findViewById(R.id.username);
        inputpassword = (EditText) findViewById(R.id.password);
        inputpsd = (EditText) findViewById(R.id.password_confirm);
        inputtelno = (EditText) findViewById(R.id.telno);
        inputinfoname = (EditText) findViewById(R.id.infoname);

        register_button.setOnClickListener(new View.OnClickListener() {
            //点击进入主页
            public void onClick(View v) {
                String str = inputusername.getText().toString();
                String password = inputpassword.getText().toString();
                String psd = inputpsd.getText().toString();
                String telno = inputtelno.getText().toString();
                String name = inputinfoname.getText().toString();
                if (str.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "输入用户名和密码", Toast.LENGTH_SHORT).show();
                } else {
                    if (!password.equals(psd)) {
                        Toast.makeText(getApplicationContext(), "请确认密码", Toast.LENGTH_SHORT).show();
                    } else {
                        if (!checkTelno(telno) || !checkName(name)) {
                            Toast.makeText(getApplicationContext(), "请输入正确的手机号和姓名", Toast.LENGTH_SHORT).show();
                        } else {
                            int username = Integer.parseInt(str);
                            RegisterAction(username, password, telno, name);
                        }
                    }

                }
                /*Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);*/
            }
        });

        account.setOnClickListener(new View.OnClickListener() {
            //点击进入注册页面
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
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

    public boolean checkName(String name){
        Pattern pattern = null;
        Matcher matcher = null;
        boolean flag = false;
        pattern = Pattern.compile("[\\u4e00-\\u9fa5]+"); // 验证姓名
        matcher = pattern.matcher(name);
        flag = matcher.matches();
        return flag;
    }

    public void RegisterAction(final int username, final String password, final String telno, final String name){
        RequestQueue rq = Volley.newRequestQueue(getApplicationContext());

        dialog.setMessage("正在注册...");
        dialog.show();

        StringRequest Req = new StringRequest(Request.Method.POST, URLConfig.Register_url, new Response.Listener<String>() {

            public void onResponse(String response) {
                dialog.dismiss();
                try {
                    JSONObject jobj = new JSONObject(response);
                    boolean error = jobj.getBoolean("error");
                    Log.d("msg", response.toString());
                    if (!error) {
                        Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
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
                Toast.makeText(getApplicationContext(), "注册失败", Toast.LENGTH_SHORT).show();
            }
        }) {

            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("SchoolID", username+ "");
                params.put("Password", password);
                params.put("Telno", telno);
                params.put("Name", name);
                return params;
            }
        };
        rq.add(Req);
    }

}
