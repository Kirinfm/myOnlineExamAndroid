package com.onlineexam.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.onlineexam.bo.Question;
import com.onlineexam.bo.User;
import com.onlineexam.util.URLConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExamActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private QuestionModel questionModel;
    private View v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        GetView.removeAllViewMap();
        final int CourseID = getIntent().getIntExtra("CourseID", 0);
        questionModel = new QuestionModel(this);
        final List<Question> data = questionModel.getQuestionList(CourseID);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), data);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            Boolean flag = false;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == 1) {
                    flag = false;
                }
                if (state == 2) {
                    flag = true;
                }
                if (state == 0) {
                    if (mViewPager.getCurrentItem() == mViewPager.getAdapter().getCount() - 1 && !flag) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(mViewPager.getContext());
                        dialog.setMessage("是否提交试卷？");
                        dialog.setNegativeButton("取消", null);
                        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int sum = 0;
                                for (int i = 0; i < mViewPager.getAdapter().getCount(); i++) {
                                    v = GetView.getView(i);
                                    RadioGroup radioGroup = (RadioGroup) v.findViewById(R.id.choices);
                                    RadioButton checkedButton = (RadioButton) radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
                                    if (checkedButton == null) {
                                        sum += 0;
                                    } else {
                                        String answer = null;
                                        switch (radioGroup.indexOfChild(checkedButton)) {
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
                                        if (answer.equals(data.get(i).getAnswer())) {
                                            sum += data.get(i).getScore();
                                        }
                                    }
                                }
                                handleup(sum, CourseID);
                            }
                        });
                        dialog.show();
                    }
                }
            }
        });


    }

    /**
     * 提交成绩
     */
    public void handleup(final int score, final int courseid) {
        LoginModel l = new LoginModel(getApplicationContext());
        User user = l.getUserinfo().get(0);
        final int schoolid = user.getSchoolid();
        Log.d("msg", score + " " + courseid + " " + schoolid);
        RequestQueue rq = Volley.newRequestQueue(getApplicationContext());
        StringRequest Req = new StringRequest(Request.Method.POST, URLConfig.Handleup_url, new Response.Listener<String>() {

            public void onResponse(String response) {
                Log.d("msg", response.toString());
                questionModel = new QuestionModel(getApplicationContext());
                try {
                    JSONObject jobj = new JSONObject(response);
                    boolean error = jobj.getBoolean("error");
                    if (!error) {
                        ExamModel examModel = new ExamModel(getApplicationContext());
                        examModel.deleteExamById(courseid);
                        Toast.makeText(getApplicationContext(), "提交完成!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ExamActivity.this, ExamListActivity.class);
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
                Toast.makeText(getApplicationContext(), "提交失败", Toast.LENGTH_SHORT).show();
            }
        }) {

            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("SchoolID", schoolid + "");
                params.put("CourseID", courseid + "");
                params.put("Score", score + "");
                return params;
            }
        };
        rq.add(Req);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(Question question, int position) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putString("question", question.getQuestion());
            args.putString("ChoiceA", question.getChoiceA());
            args.putString("ChoiceB", question.getChoiceB());
            args.putString("ChoiceC", question.getChoiceC());
            args.putString("ChoiceD", question.getChoiceD());
            args.putInt("num", position);
            fragment.setArguments(args);
            return fragment;
        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_exam, container, false);
            GetView.setViewMap(rootView, getArguments().getInt("num"));
            Log.d("msg", rootView.toString() + getArguments().getInt("num"));
            TextView textView = (TextView) rootView.findViewById(R.id.question);
            RadioButton ChoiceA = (RadioButton) rootView.findViewById(R.id.choiceA);
            RadioButton ChoiceB = (RadioButton) rootView.findViewById(R.id.choiceB);
            RadioButton ChoiceC = (RadioButton) rootView.findViewById(R.id.choiceC);
            RadioButton ChoiceD = (RadioButton) rootView.findViewById(R.id.choiceD);
            textView.setText(getArguments().getString("question"));
            ChoiceA.setText(getArguments().getString("ChoiceA"));
            ChoiceB.setText(getArguments().getString("ChoiceB"));
            ChoiceC.setText(getArguments().getString("ChoiceC"));
            ChoiceD.setText(getArguments().getString("ChoiceD"));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private List<Question> Mdata;

        public SectionsPagerAdapter(FragmentManager fm, List<Question> data) {
            super(fm);
            Mdata = data;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Question question = Mdata.get(position);
            Fragment f = PlaceholderFragment.newInstance(question, position);
            return f;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return Mdata.size();
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "试题" + position;
        }
    }

    public static class GetView {
        private static HashMap<Integer, View> ViewHashMap = new HashMap<Integer, View>();

        public static void setViewMap(View v, int position) {
            ViewHashMap.put(position, v);
        }

        public static void removeAllViewMap() {
            ViewHashMap.clear();
        }

        public static View getView(int position) {
            return ViewHashMap.get(position);
        }
    }
}
