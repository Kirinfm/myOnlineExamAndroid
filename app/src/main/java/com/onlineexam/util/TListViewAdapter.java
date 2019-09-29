package com.onlineexam.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.onlineexam.Activity.EditQuestionActivity;
import com.onlineexam.Model.QuestionModel;
import com.onlineexam.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 方明 on 2017/3/26.
 */

public class TListViewAdapter extends BaseAdapter {
    private final LayoutInflater mInflater;

    private int[] mTo;
    private String[] mFrom;
    private SimpleAdapter.ViewBinder mViewBinder;

    private List<? extends Map<String, ?>> mData;

    private int mResource;
    private int mDropDownResource;

    public TListViewAdapter(Context context, List<? extends Map<String, ?>> data,
                            @LayoutRes int resource, String[] from, @IdRes int[] to) {
        mData = data;
        mResource = mDropDownResource = resource;
        mFrom = from;
        mTo = to;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(mInflater, position, convertView, parent, mResource);
    }

    private View createViewFromResource(LayoutInflater inflater, int position, View convertView,
                                        ViewGroup parent, int resource) {
        View v;
        if (convertView == null) {
            v = inflater.inflate(resource, parent, false);
        } else {
            v = convertView;
        }

        bindView(position, v, parent);

        return v;
    }

    private void bindView(int position, View view, ViewGroup parent) {
        final Map dataSet = mData.get(position);
        if (dataSet == null) {
            return;
        }

        final SimpleAdapter.ViewBinder binder = mViewBinder;
        final String[] from = mFrom;
        final int[] to = mTo;
        final int count = to.length;

        for (int i = 0; i < count; i++) {
            final View v = view.findViewById(to[i]);
            if (v != null) {
                final Object data = dataSet.get(from[i]);
                String text = data == null ? "" : data.toString();
                if (text == null) {
                    text = "";
                }

                boolean bound = false;
                if (binder != null) {
                    bound = binder.setViewValue(v, data, text);
                }

                if (!bound) {
                    if (v instanceof Checkable) {
                        if (data instanceof Boolean) {
                            ((Checkable) v).setChecked((Boolean) data);
                        } else if (v instanceof TextView) {
                            ((TextView) v).setText(text);
                        } else {
                            throw new IllegalStateException(v.getClass().getName() +
                                    " should be bound to a Boolean, not a " +
                                    (data == null ? "<unknown type>" : data.getClass()));
                        }
                    } else if (v instanceof TextView) {
                        ((TextView) v).setText(text);
                    } else if (v instanceof ImageView) {
                        if (data instanceof Integer) {
                            ((ImageView) v).setImageResource((Integer) data);
                        } else {
                            try {
                                ((ImageView) v).setImageResource(Integer.parseInt(text));
                            } catch (NumberFormatException nfe) {
                                ((ImageView) v).setImageURI(Uri.parse(text));
                            }
                        }
                    } else {
                        throw new IllegalStateException(v.getClass().getName() + " is not a " +
                                " view that can be bounds by this SimpleAdapter");
                    }
                }
            }
        }
        //set top and details click
        click(view, position, parent, dataSet);
    }

    //add by FM
    public void click(View v, final int position, final ViewGroup parent, final Map dataSet) {
        TextView delete = (TextView) v.findViewById(R.id.delete);
        TextView change = (TextView) v.findViewById(R.id.change);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletequestion(Integer.parseInt(dataSet.get("QuestionID").toString()),v);
                mData.remove(position);
                notifyDataSetChanged();
            }
        });
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), EditQuestionActivity.class);
                intent.putExtra("QuestionID", Integer.parseInt(dataSet.get("QuestionID").toString()));
                v.getContext().startActivity(intent);
            }
        });
    }

    public void deletequestion(final int QuestionID, final View v) {
        final QuestionModel questionModel = new QuestionModel(v.getContext());
        RequestQueue rq = Volley.newRequestQueue(v.getContext());
        StringRequest Req = new StringRequest(Request.Method.POST, URLConfig.DeleteQuestion_url, new Response.Listener<String>() {

            public void onResponse(String response) {
                try {
                    JSONObject jobj = new JSONObject(response);
                    boolean error = jobj.getBoolean("error");
                    Log.d("msg", response.toString());
                    if (!error) {
                        questionModel.deleteQuestionByID(QuestionID);
                        Toast.makeText(v.getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                    } else {
                        String errormsg = jobj.getString("errormsg");
                        Toast.makeText(v.getContext(), errormsg, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(v.getContext(), "获取数据失败", Toast.LENGTH_SHORT).show();
            }
        }) {

            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("QuestionID", QuestionID + "");
                return params;
            }
        };
        rq.add(Req);
    }
}
