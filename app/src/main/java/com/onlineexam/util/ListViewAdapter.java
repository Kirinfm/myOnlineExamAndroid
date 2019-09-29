package com.onlineexam.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.onlineexam.Activity.ExamActivity;
import com.onlineexam.R;

import java.util.List;
import java.util.Map;

/**
 * Created by 方明 on 2017/3/16.
 */

public class ListViewAdapter extends BaseAdapter {
    private final LayoutInflater mInflater;

    private int[] mTo;
    private String[] mFrom;
    private SimpleAdapter.ViewBinder mViewBinder;

    private List<? extends Map<String, ?>> mData;
    private List<? extends Map<String, ?>> mDataDetail;

    private int mResource;
    private int mDropDownResource;

    public ListViewAdapter(Context context, List<? extends Map<String, ?>> data, List<? extends Map<String, ?>> datadetail,
                           @LayoutRes int resource, String[] from, @IdRes int[] to) {
        mData = data;
        mDataDetail = datadetail;
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
        TextView detail = (TextView) v.findViewById(R.id.detail);
        TextView answer = (TextView) v.findViewById(R.id.answer);

        detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map detaildataSet = mDataDetail.get(position);
                Dialog(parent, detaildataSet);
            }
        });
        answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ExamActivity.class);
                intent.putExtra("CourseID", Integer.parseInt(dataSet.get("CourseID").toString()));
                v.getContext().startActivity(intent);
            }
        });
    }

    public void Dialog(final ViewGroup parent, final Map detaildataSet) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialog = inflater.inflate(R.layout.detaildialog_layout, null);
        TextView title = (TextView) dialog.findViewById(R.id.title);
        TextView time = (TextView) dialog.findViewById(R.id.time);
        TextView content = (TextView) dialog.findViewById(R.id.content);
        title.setText(detaildataSet.get("title").toString());
        time.setText(detaildataSet.get("time").toString());
        content.setText(detaildataSet.get("content").toString());
        AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext());
        builder.setTitle("详情");
        builder.setView(dialog);
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("开始答题", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(parent.getContext(), ExamActivity.class);
                intent.putExtra("CourseID", Integer.parseInt(detaildataSet.get("CourseID").toString()));
                parent.getContext().startActivity(intent);
            }
        });
        builder.show();
    }
}
