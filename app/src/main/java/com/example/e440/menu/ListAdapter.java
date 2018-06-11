package com.example.e440.menu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by e440 on 10-04-18.
 */

public class ListAdapter extends ArrayAdapter {
    private int layoutResource;
    public ListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public ListAdapter(Context context, int resource, List<Student> items) {
        super(context, resource, items);
        this.layoutResource=resource;
        }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(layoutResource, null);

        }

        Student s =(Student)getItem(position);
        if (s != null) {
            TextView tt1 = (TextView) v.findViewById(R.id.studentNameTextView);
            TextView tt2 = (TextView) v.findViewById(R.id.studentRutTextView);

            if (tt1 != null) {
                tt1.setText(s.getLast_name()+" "+s.getName());
            }

            if (tt2 != null) {
                tt2.setText("RUT: "+ s.getRut());
            }

        }

        return v;
    }

}
