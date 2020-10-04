package com.example.e440.menu.wally_original;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.e440.menu.R;

import org.w3c.dom.Text;


public class AssentFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters

    private OnFragmentInteractionListener mListener;

    public AssentFragment(String mainText, String cancelButtonText, String continueButtonText) {
        Bundle bundle = new Bundle();
        bundle.putString(MAIN_TEXT_ARG, mainText);
        bundle.putString(CANCEL_BUTTON_TEXT_ARG, cancelButtonText);
        bundle.putString(CONTINUE_BUTTON_TEXT_ARG, continueButtonText);

        this.setArguments(bundle);
        // Required empty public constructor
    };

    static String MAIN_TEXT_ARG= "main_text";
    static String CANCEL_BUTTON_TEXT_ARG = "cancel_button_text";
    static String CONTINUE_BUTTON_TEXT_ARG = "continue_button_text";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        String mainText = getArguments().getString(MAIN_TEXT_ARG);

        String continueButtonText = getArguments().getString(CONTINUE_BUTTON_TEXT_ARG);
        String cancelButtonText = getArguments().getString(CANCEL_BUTTON_TEXT_ARG);
        View view = inflater.inflate(R.layout.fragment_assent, container, false);


        TextView mainTextView = view.findViewById(R.id.assentStatement);
        mainTextView.setText(mainText);

        Button continueButton = view.findViewById(R.id.assentYesButton);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onYesButtonPressed(view);
            }
        });
        continueButton.setText(continueButtonText);

        Button cancelButton = view.findViewById(R.id.assentNotButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNotButtonPressed(view);
            }
        });
        cancelButton.setText(cancelButtonText);
        return view;

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onYesButtonPressed(View view) {
        if (mListener != null) {
            mListener.onContinue();
        }
    }
    public void onNotButtonPressed(View view) {
        if (mListener != null) {
            mListener.onCancel();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onContinue();
        void onCancel();
    }
}
