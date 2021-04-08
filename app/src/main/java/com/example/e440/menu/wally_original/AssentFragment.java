package com.example.e440.menu.wally_original;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.e440.menu.R;

import java.util.ArrayList;


public class AssentFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters

    private OnFragmentInteractionListener mListener;
    public AssentFragment(){

    }

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
    String blankImageBase64 ="data:image/jpeg;base64,/9j/4AAQSkZJRgABAgEBLAEsAAD/7QaCUGhvdG9zaG9wIDMuMAA4QklNA+0KUmVzb2x1dGlvbgAAAAAQASwAAAABAAEBLAAAAAEAAThCSU0EDRhGWCBHbG9iYWwgTGlnaHRpbmcgQW5nbGUAAAAABAAAAB44QklNBBkSRlggR2xvYmFsIEFsdGl0dWRlAAAAAAQAAAAeOEJJTQPzC1ByaW50IEZsYWdzAAAACQAAAAAAAAAAAQA4QklNBAoOQ29weXJpZ2h0IEZsYWcAAAAAAQAAOEJJTScQFEphcGFuZXNlIFByaW50IEZsYWdzAAAAAAoAAQAAAAAAAAACOEJJTQP1F0NvbG9yIEhhbGZ0b25lIFNldHRpbmdzAAAASAAvZmYAAQBsZmYABgAAAAAAAQAvZmYAAQChmZoABgAAAAAAAQAyAAAAAQBaAAAABgAAAAAAAQA1AAAAAQAtAAAABgAAAAAAAThCSU0D+BdDb2xvciBUcmFuc2ZlciBTZXR0aW5ncwAAAHAAAP////////////////////////////8D6AAAAAD/////////////////////////////A+gAAAAA/////////////////////////////wPoAAAAAP////////////////////////////8D6AAAOEJJTQQIBkd1aWRlcwAAAAAQAAAAAQAAAkAAAAJAAAAAADhCSU0EHg1VUkwgb3ZlcnJpZGVzAAAABAAAAAA4QklNBBoGU2xpY2VzAAAAAGkAAAAGAAAAAAAAAAAAAACWAAAA7QAAAAQAQwBDAEIAMgAAAAEAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAAAAAAA7QAAAJYAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAOEJJTQQREUlDQyBVbnRhZ2dlZCBGbGFnAAAAAQEAOEJJTQQUF0xheWVyIElEIEdlbmVyYXRvciBCYXNlAAAABAAAAAE4QklNBAwVTmV3IFdpbmRvd3MgVGh1bWJuYWlsAAAC5QAAAAEAAABwAAAARwAAAVAAAF0wAAACyQAYAAH/2P/gABBKRklGAAECAQBIAEgAAP/uAA5BZG9iZQBkgAAAAAH/2wCEAAwICAgJCAwJCQwRCwoLERUPDAwPFRgTExUTExgRDAwMDAwMEQwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwBDQsLDQ4NEA4OEBQODg4UFA4ODg4UEQwMDAwMEREMDAwMDAwRDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDP/AABEIAEcAcAMBIgACEQEDEQH/3QAEAAf/xAE/AAABBQEBAQEBAQAAAAAAAAADAAECBAUGBwgJCgsBAAEFAQEBAQEBAAAAAAAAAAEAAgMEBQYHCAkKCxAAAQQBAwIEAgUHBggFAwwzAQACEQMEIRIxBUFRYRMicYEyBhSRobFCIyQVUsFiMzRygtFDByWSU/Dh8WNzNRaisoMmRJNUZEXCo3Q2F9JV4mXys4TD03Xj80YnlKSFtJXE1OT0pbXF1eX1VmZ2hpamtsbW5vY3R1dnd4eXp7fH1+f3EQACAgECBAQDBAUGBwcGBTUBAAIRAyExEgRBUWFxIhMFMoGRFKGxQiPBUtHwMyRi4XKCkkNTFWNzNPElBhaisoMHJjXC0kSTVKMXZEVVNnRl4vKzhMPTdePzRpSkhbSVxNTk9KW1xdXl9VZmdoaWprbG1ub2JzdHV2d3h5ent8f/2gAMAwEAAhEDEQA/APVUkkklKSSSSUpJJJJSkkkklKSSSSUpJJJJSkkkklP/0PVUkkklKSSSSUpJJJJSkkkklKSSSSUpJJJJSkkkklP/0fVUkkklKSSSSUpJJJJSkkkklKSSSSUpJJJJSkkkklP/0vVUkkklKSSSSUpJJJJSkkkklKSSSSUpJJJJSkkkklP/0/VUl8qpJKfqpJfKqSSn6qSXyqkkp+qkl8qpJKfqpJfKqSSn6qSXyqkkp+qkl8qpJKf/2QA4QklNBCEaVmVyc2lvbiBjb21wYXRpYmlsaXR5IGluZm8AAAAAVQAAAAEBAAAADwBBAGQAbwBiAGUAIABQAGgAbwB0AG8AcwBoAG8AcAAAABMAQQBkAG8AYgBlACAAUABoAG8AdABvAHMAaABvAHAAIAA2AC4AMAAAAAEAOEJJTQQGDEpQRUcgUXVhbGl0eQAAAAAHAAgBAQABAQD/7gAhQWRvYmUAZEAAAAABAwAQAwIDBgAAAAAAAAAAAAAAAP/bAIQAAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQICAgICAgICAgICAwMDAwMDAwMDAwEBAQEBAQEBAQEBAgIBAgIDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMD/8IAEQgAlgDtAwERAAIRAQMRAf/EAF0AAQEAAAAAAAAAAAAAAAAAAAAKAQEAAAAAAAAAAAAAAAAAAAAAEAEAAAAAAAAAAAAAAAAAAACQEQEAAAAAAAAAAAAAAAAAAACQEgEAAAAAAAAAAAAAAAAAAACQ/9oADAMBAQIRAxEAAAC/gAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAH//2gAIAQIAAQUADr//2gAIAQMAAQUADr//2gAIAQEAAQUADr//2gAIAQICBj8ADr//2gAIAQMCBj8ADr//2gAIAQEBBj8ADr//2Q==";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    View inflatedView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        String mainText = getArguments().getString(MAIN_TEXT_ARG);

        String continueButtonText = getArguments().getString(CONTINUE_BUTTON_TEXT_ARG);
        String cancelButtonText = getArguments().getString(CANCEL_BUTTON_TEXT_ARG);
        View view = inflater.inflate(R.layout.fragment_assent, container, false);
        inflatedView = view;

        TextView mainTextView = view.findViewById(R.id.itemTextTextView);
        mainTextView.setText(mainText);

        Button continueButton = view.findViewById(R.id.itemNextButton);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onYesButtonPressed(view);
            }
        });
        continueButton.setText(continueButtonText);

        View bluetoothButton = view.findViewById(R.id.bluetoothLogoImageView);
        bluetoothButton.setOnClickListener(this);
        bluetoothButton.setOnLongClickListener(this);

        Button cancelButton = view.findViewById(R.id.itemFragmentBackButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNotButtonPressed(view);
            }
        });
        cancelButton.setText(cancelButtonText);
        return view;

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bluetoothLogoImageView){
            MyBluetoothService myBluetoothService = MyBluetoothService.GetInstance(getContext());
            ImageView localView = inflatedView.findViewById(R.id.bluetoothLogoImageView);
            ImageTask imageTask = new ImageTask(blankImageBase64, localView);
            myBluetoothService.sendMessage(imageTask);
        }
    }

    @Override
    public boolean onLongClick(View v) {

        final MyBluetoothService myBluetoothService = MyBluetoothService.GetInstance(getContext());
        final ArrayList<String> bondedDevicesNames = myBluetoothService.GetBondedDevicesNames();
        final ArrayList<String> bondedDevicesAdresses = myBluetoothService.GetBondedDevicesAddresses();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Seleccione un dispositivo Bluetooth emparejado:");

        builder.setItems(bondedDevicesNames.toArray(new String[bondedDevicesNames.size()]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String preferredBtDeviceAddress = bondedDevicesAdresses.get(which);
                SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance(getContext());
                sharedPreferencesManager.setSlaveBluetoothDeviceAddress(preferredBtDeviceAddress);
                myBluetoothService.finish();

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

        return false;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onYesButtonPressed(View view) {
        if (mListener != null) {
            mListener.onAllItemsFinished();
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
    public void onResume() {
        super.onResume();
        MyBluetoothService myBluetoothService = MyBluetoothService.GetInstance(getContext());
        ImageView localView = inflatedView.findViewById(R.id.bluetoothLogoImageView);
        ImageTask imageTask = new ImageTask(blankImageBase64, localView);
        myBluetoothService.sendMessage(imageTask);
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
        void onAllItemsFinished();
        void onCancel();
    }
}
