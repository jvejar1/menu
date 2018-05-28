package com.example.e440.menu;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.wifi.p2p.nsd.WifiP2pServiceInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.ContentFrameLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by e440 on 08-05-18.
 */

public class DisplaySituationFragment extends Fragment {


    ReturnToWallyTestListener mCallback;
    public interface ReturnToWallyTestListener{
        void returnToWally();


    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity;
        activity=(Activity) context;


        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (ReturnToWallyTestListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement Listener");
        }
    }

    View inflatedView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.display_situation, container, false);
        Bundle args = getArguments();
        long wsituation_id=args.getLong("wsituation_id");

        SituationLoader situationLoader=new SituationLoader();
        situationLoader.execute(wsituation_id);



        Button backToTestButton=inflatedView.findViewById(R.id.backToWallyTestButton);
        backToTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mCallback.returnToWally();

            }
        });
        return inflatedView;
    }

    public class SituationLoader extends AsyncTask<Long,Void,WSituation>{

        @Override
        protected WSituation doInBackground(Long... integers) {
            long wsituation_id=(long)integers[0];
            WSituation wsituation=DatabaseManager.getInstance(getContext()).testDatabase.daoAccess().fetchWSituationById(wsituation_id);
            return wsituation;
        }

        @Override
        protected void onPostExecute(WSituation wSituation) {
            super.onPostExecute(wSituation);

            byte[] image_bytes = wSituation.getImage_bytes();
            Bitmap bm =Utilities.convertBytesArrayToBitmap(image_bytes);
            ImageView imageView=inflatedView.findViewById(R.id.SituationImageView);
            imageView.setImageBitmap(bm);
        }
    }
}
