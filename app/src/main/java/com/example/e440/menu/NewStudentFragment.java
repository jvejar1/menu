package com.example.e440.menu;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by e440 on 24-04-18.
 */

public class NewStudentFragment extends Fragment {
    View inflatedView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflatedView = inflater.inflate(R.layout.new_student_fragment, container, false);

        return inflatedView;
    }
}
