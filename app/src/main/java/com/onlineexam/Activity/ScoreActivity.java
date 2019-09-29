package com.onlineexam.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.onlineexam.R;
import com.onlineexam.bo.Exam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScoreActivity extends AppCompatActivity {

    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        lv = (ListView) findViewById(R.id.list);
        List<Exam> scores = (List<Exam>) getIntent().getSerializableExtra("scorelist");
        List<HashMap<String, String>> data = new ArrayList<>();
        for (int i = 0; i < scores.size(); i++) {
            HashMap<String, String> datamap = new HashMap<>();
            datamap.put("CourseName", scores.get(i).getCourseName());
            datamap.put("Score", scores.get(i).getScore() + "");
            data.add(datamap);
        }
        SimpleAdapter adapter = new SimpleAdapter(this,data,R.layout.score_list_layout,new String[]{"CourseName","Score"},new int[]{R.id.title,R.id.Score});
        lv.setAdapter(adapter);
    }

}
