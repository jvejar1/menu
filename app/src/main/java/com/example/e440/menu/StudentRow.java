package com.example.e440.menu;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StudentRow {
    Student student;
    RecyclerView.LayoutManager evalsCountsLayoutManager;
    EvaluationCount[] evaluationCounts;
    View evaluationCountsView;
    StudentRow(Student student, RecyclerView.LayoutManager evalsCountsLayoutManager, EvaluationCount[] evaluationCounts, View evaluationCountsView){
        this.student = student;
        this.evalsCountsLayoutManager = evalsCountsLayoutManager;
        this.evaluationCounts = evaluationCounts;
        this.evaluationCountsView = evaluationCountsView;
    }
}
