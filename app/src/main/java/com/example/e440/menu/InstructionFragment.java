package com.example.e440.menu;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by e440 on 13-05-18.
 */

public class InstructionFragment extends Fragment {
    private backFromInstructionListener mCallback;
    public interface backFromInstructionListener{
        void backFromInstruction();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity;
        activity=(Activity) context;

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (backFromInstructionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    View inflatedView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflatedView = inflater.inflate(R.layout.instruction_fragment, container, false);

        Bundle args=getArguments();
        String text= args.getString("text");
        int drawable_id1=args.getInt("drawable1",-1);
        int drawable_id2=args.getInt("drawable2",-1);
        if (drawable_id1!=-1){
            ImageView imageView=inflatedView.findViewById(R.id.instructionImageView1);
            imageView.setImageResource(drawable_id1);
        }
        if(drawable_id2!=-1){
            ImageView imageView=inflatedView.findViewById(R.id.instructionImageView2);
            imageView.setImageResource(drawable_id2);
        }
        TextView corsiInstructionTextView=inflatedView.findViewById(R.id.instructionTextView);
        corsiInstructionTextView.setText(text);
        Button corsiStartButton = inflatedView.findViewById(R.id.startButton);
        corsiStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.backFromInstruction();

            }
        });
        return inflatedView;


    }


}


