package com.example.e440.menu.wally_original;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.e440.menu.R;


public class AssentFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters

    private OnFragmentInteractionListener mListener;

    public AssentFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_assent, container, false);
        view.findViewById(R.id.assentYesButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onYesButtonPressed(view);
            }
        });

        view.findViewById(R.id.assentNotButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNotButtonPressed(view);
            }
        });
        return view;

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onYesButtonPressed(View view) {
        if (mListener != null) {
            mListener.onYesAssent();
        }
    }
    public void onNotButtonPressed(View view) {
        if (mListener != null) {
            mListener.onNotAssent();
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
        void onYesAssent();
        void onNotAssent();
    }
}
