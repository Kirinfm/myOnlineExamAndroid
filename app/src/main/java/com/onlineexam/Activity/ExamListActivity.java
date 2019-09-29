package com.onlineexam.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ListView;

import com.onlineexam.Model.ExamModel;
import com.onlineexam.R;
import com.onlineexam.bo.Exam;
import com.onlineexam.util.ListViewAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExamListActivity extends AppCompatActivity {

    private ListView listView;
    private ExamModel examModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //list
        listView = (ListView) findViewById(R.id.list);
        examModel = new ExamModel(this);
        ArrayList<Exam> exams = (ArrayList<Exam>) examModel.getExamList();
        List<HashMap<String, String>> data = new ArrayList<>();
        List<HashMap<String, String>> datadetail = new ArrayList<>();
        for (int i = 0; i < exams.size(); i++) {
            HashMap<String, String> map = new HashMap<>();
            HashMap<String, String> mapdetail = new HashMap<>();
            map.put("title", exams.get(i).getCourseName());
            map.put("time", exams.get(i).getCreateDate().substring(0, 10));
            map.put("content", exams.get(i).getContext().substring(0, 10));
            map.put("CourseID", exams.get(i).getCourseID() + "");
            mapdetail.put("title", exams.get(i).getCourseName());
            mapdetail.put("time", exams.get(i).getCreateDate().substring(0, 10));
            mapdetail.put("content", exams.get(i).getContext());
            mapdetail.put("CourseID", exams.get(i).getCourseID() + "");
            data.add(map);
            datadetail.add(mapdetail);
        }
        Log.d("list", data.toString());
        //SimpleAdapter simpleAdapter = new SimpleAdapter(this, data, R.layout.list_layout, new String[]{"title", "time", "content"}, new int[]{R.id.title, R.id.time, R.id.content});
        ListViewAdapter simpleAdapter = new ListViewAdapter(this, data, datadetail, R.layout.list_layout, new String[]{"title", "time", "content"}, new int[]{R.id.title, R.id.time, R.id.content});
        listView.setAdapter(simpleAdapter);

        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ExamListActivity.this, "", Toast.LENGTH_SHORT).show();
            }
        });*/

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

}
