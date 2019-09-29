package com.onlineexam.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.onlineexam.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckboxListAdapter extends BaseAdapter {
    private Context context;
    private List<? extends Map<String, String>> data;

    // 用来控制CheckBox的选中状况
    private static HashMap<String, Boolean> isSelected;

    class ViewCheckBox {

        TextView tv;
        CheckBox cb;
    }

    public static HashMap<String, Boolean> getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(HashMap<String, Boolean> isSelected) {
        CheckboxListAdapter.isSelected = isSelected;
    }

    public CheckboxListAdapter(Context context, List<? extends Map<String, String>> data) {
        this.context = context;
        this.data = data;
        isSelected = new HashMap<>();
        for (int i = 0; i < data.size(); i++) {
            getIsSelected().put(data.get(i).get("CourseID"), false);
        }
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewCheckBox holder;
        final Map dataset = data.get(position);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.pick_list_layout, null);
            holder = new ViewCheckBox();
            holder.cb = (CheckBox) convertView.findViewById(R.id.checkbox);
            holder.tv = (TextView) convertView.findViewById(R.id.title);
            convertView.setTag(holder);
        } else {
            // 取出holder
            holder = (ViewCheckBox) convertView.getTag();
        }

        holder.tv.setText(dataset.get("CourseName").toString());
        // 监听checkBox并根据原来的状态来设置新的状态
        holder.cb.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                if (isSelected.get(data.get(position).get("CourseID"))) {
                    isSelected.put(data.get(position).get("CourseID"), false);
                    setIsSelected(isSelected);
                } else {
                    isSelected.put(data.get(position).get("CourseID"), true);
                    setIsSelected(isSelected);
                }

            }
        });

        // 根据isSelected来设置checkbox的选中状况
        holder.cb.setChecked(getIsSelected().get(data.get(position).get("CourseID")));
        return convertView;
    }
}
