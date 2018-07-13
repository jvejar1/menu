package com.example.e440.menu;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by e440 on 12-07-18.
 */

public class StudentsAdapter extends RecyclerView.Adapter<StudentsAdapter.MyViewHolder> {
    private List<Student> studentList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView student_name_text_view, student_rut_text_view, student_school_name_text_view;

        public MyViewHolder(View view) {
            super(view);
            student_name_text_view = (TextView) view.findViewById(R.id.studentNameTextView);
            student_rut_text_view = (TextView) view.findViewById(R.id.studentRutTextView);
            student_school_name_text_view = (TextView) view.findViewById(R.id.studentServerIdTextView);

        }
    }


    public StudentsAdapter(List<Student> moviesList) {
        this.studentList = moviesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.students_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Student student = studentList.get(position);
        holder.student_name_text_view.setText(student.getFullName());
        holder.student_rut_text_view.setText("RUT: "+student.getRut());
      //  holder.student_school_name_text_view.setText(student.getSchool_id());
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }
}
