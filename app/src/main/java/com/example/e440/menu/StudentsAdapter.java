package com.example.e440.menu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Created by e440 on 12-07-18.
 */

public class StudentsAdapter extends RecyclerView.Adapter<StudentsAdapter.MyViewHolder> {
    private List<Student> studentList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView student_name_text_view, student_rut_text_view, student_school_name_text_view,student_course_text_view;
        public TextView acesCountTextView;
        public TextView wallyCountTextView;
        public TextView corsiCountTextView;
        public TextView hnfCountTextView;
        public TextView fonotestCountTextView;
        public MyViewHolder(View view) {
            super(view);
            student_name_text_view = (TextView) view.findViewById(R.id.studentNameTextView);
            student_rut_text_view = (TextView) view.findViewById(R.id.studentRutTextView);
            //student_school_name_text_view = (TextView) view.findViewById(R.id.studentSchoolNameTextView);
            //student_course_text_view=view.findViewById(R.id.studentCourseTextView);
            wallyCountTextView = view.findViewById(R.id.wallyCountTextView);
            acesCountTextView = view.findViewById(R.id.acesCountTextView);
            corsiCountTextView = view.findViewById(R.id.corsisCountTextView);
            hnfCountTextView = view.findViewById(R.id.hnfCountTextView);
            fonotestCountTextView = view.findViewById(R.id.fonotestCountTextView);

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
      //  holder.student_school_name_text_view.setText(student.getSchool_name());
        //holder.student_course_text_view.setText(student.getCourseFullName());
        holder.acesCountTextView.setText(""+student.getAces_count());
        holder.wallyCountTextView.setText(""+student.getWally_count());
        holder.corsiCountTextView.setText(""+student.getCorsis_count());
        holder.hnfCountTextView.setText(""+student.getHnf_count());
        holder.fonotestCountTextView.setText(""+student.getHnf_count());
        String evaluationsCounts = "";
        evaluationsCounts += "A: "+student.getAces_count()+ "    W: "+student.getWally_count()+"    C: "+student.getCorsis_count()+"    HNF: " + student.getHnf_count() + "    FON: " + student.getFonotest_count();
        //holder.wallyCountTextView.setText(evaluationsCounts);
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }
}
