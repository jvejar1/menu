package com.example.e440.menu;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.List;

/**
 * Created by e440 on 12-07-18.
 */

public class StudentsAdapter extends RecyclerView.Adapter<StudentsAdapter.MyViewHolder> {
    private List<Student> studentList;
    private List<RecyclerView.LayoutManager> evalsCountLayoutManagers;
    private List<StudentRow> studentRows;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView student_name_text_view, student_rut_text_view, student_school_name_text_view,student_course_text_view;
        public RecyclerView evaluationsCounts;
        public MyViewHolder(View view) {
            super(view);
            student_name_text_view = (TextView) view.findViewById(R.id.studentNameTextView);
            student_rut_text_view = (TextView) view.findViewById(R.id.studentRutTextView);
            //student_school_name_text_view = (TextView) view.findViewById(R.id.studentSchoolNameTextView);
            //student_course_text_view=view.findViewById(R.id.studentCourseTextView);
            evaluationsCounts =view.findViewById(R.id.evaluationsCounts);
        }
    }


    public StudentsAdapter(List<Student> moviesList, List<RecyclerView.LayoutManager> evalsCountsLayoutManagers, Context context) {
        this.studentList = moviesList;
        this.evalsCountLayoutManagers = evalsCountsLayoutManagers;
        this.context = context;
    }
    public StudentsAdapter(List<StudentRow> studentRows) {
        this.studentRows = studentRows;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.students_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Student student = studentRows.get(position).student;
        holder.student_name_text_view.setText(student.getFullName());
        holder.student_rut_text_view.setText("RUT: "+student.getRut());
      //  holder.student_school_name_text_view.setText(student.getSchool_name());
        //holder.student_course_text_view.setText(student.getCourseFullName());
        EvaluationsCountAdapter evaluationsCountAdapter = new EvaluationsCountAdapter(studentRows.get(position).evaluationCounts);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 4, RecyclerView.HORIZONTAL, false);
        holder.evaluationsCounts.setAdapter(evaluationsCountAdapter);
        CountItemDecoration countItemDecoration = new CountItemDecoration(20);
        for (int i=0; i< holder.evaluationsCounts.getItemDecorationCount(); i++){
            holder.evaluationsCounts.removeItemDecorationAt(i);
        }
        holder.evaluationsCounts.addItemDecoration(countItemDecoration);
        holder.evaluationsCounts.setLayoutManager(layoutManager);
    }

    @Override
    public void onViewRecycled(@NonNull StudentsAdapter.MyViewHolder holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return studentRows.size();
    }
}
class CountItemDecoration extends RecyclerView.ItemDecoration{

    private int space;

    public CountItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        //outRect.right = space*3;
        //outRect.bottom = space;
        // Add top margin only for the first item to avoid double space between items
    }
}
