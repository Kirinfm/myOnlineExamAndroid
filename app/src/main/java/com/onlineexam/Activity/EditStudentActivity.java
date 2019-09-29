package com.onlineexam.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.onlineexam.R;
import com.onlineexam.bo.Exam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EditStudentActivity extends AppCompatActivity {
    private ListView lv;
    private TextView excellent;
    private TextView good;
    private TextView fair;
    private TextView unfair;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_student);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        lv = (ListView) findViewById(R.id.list);
        excellent = (TextView) findViewById(R.id.excellent);
        good = (TextView) findViewById(R.id.good);
        fair = (TextView) findViewById(R.id.fair);
        unfair = (TextView) findViewById(R.id.unfair);
        List<Exam> scores = (List<Exam>) getIntent().getSerializableExtra("scorelist");
        int eflag = 0;
        int gflag = 0;
        int fflag = 0;
        int uflag = 0;
        for (int i = 0; i < scores.size(); i++) {
            switch (scores.get(i).getScore()/10){
                case 10:
                case 9:eflag++;break;
                case 8:
                case 7:gflag++;break;
                case 6:fflag++;break;
                default:uflag++;break;
            }
        }
        excellent.setText("优秀人数："+eflag);
        good.setText("良好人数："+gflag);
        fair.setText("合格人数："+fflag);
        unfair.setText("不合格人数："+uflag);
        List<HashMap<String, String>> data = new ArrayList<>();
        for (int i = 0; i < scores.size(); i++) {
            HashMap<String, String> datamap = new HashMap<>();
            datamap.put("Studentinfo", scores.get(i).getCourseID()+"  "+scores.get(i).getCourseName());
            datamap.put("Score", scores.get(i).getScore() + "");
            data.add(datamap);
        }
        SimpleAdapter adapter = new SimpleAdapter(this,data,R.layout.score_list_layout,new String[]{"Studentinfo","Score"},new int[]{R.id.title,R.id.Score});
        lv.setAdapter(adapter);
    }

}
