package com.example.e440.menu;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Response;

/**
 * Created by e440 on 19-07-18.
 */

public class HomeFragment extends Fragment implements View.OnClickListener,ResultsSenderListener{
    View inflatedView;
    Button send_results_button;
    DatabaseManager databaseManager;
    EvaluationsInformer evaluationsInformer;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.home_fragment, container, false);
        send_results_button=inflatedView.findViewById(R.id.sendResultsButton);
        send_results_button.setEnabled(false);
        send_results_button.setOnClickListener(this);
        databaseManager=DatabaseManager.getInstance(getActivity().getApplicationContext());

        evaluationsInformer=new EvaluationsInformer();
        evaluationsInformer.execute();
        return inflatedView;
    }


    void updateNumbers(int total,int saved, int to_send, int acesCount, int wallyCount, int corsisCount, int hnfCount, int fonotestCount){

        if(to_send>0){
            send_results_button.setEnabled(true);
        }

        TextView total_text_view=inflatedView.findViewById(R.id.evalsCountTextView);
        TextView saved_text_view=inflatedView.findViewById(R.id.savedEvalsTextView);
        TextView to_send_text_view=inflatedView.findViewById(R.id.evalsToSendTextView);
        total_text_view.setText(Integer.toString(total));
        saved_text_view.setText(Integer.toString(saved));
        to_send_text_view.setText(Integer.toString(to_send));

        TextView acesCountTextView = inflatedView.findViewById(R.id.acesCountTextView);
        acesCountTextView.setText(Integer.toString(acesCount));

        TextView wallyCountTextView = inflatedView.findViewById(R.id.wallyCountTextView);
        wallyCountTextView.setText(Integer.toString(wallyCount));

        TextView corsisCountTextView = inflatedView.findViewById(R.id.corsisCountTextView);
        corsisCountTextView.setText(Integer.toString(corsisCount));

        TextView hnfCountTextView = inflatedView.findViewById(R.id.hnfCountTextView);
        hnfCountTextView.setText(Integer.toString(hnfCount));

        TextView fonotestCountTextView = inflatedView.findViewById(R.id.fonotestCountTextView);
        fonotestCountTextView.setText(Integer.toString(fonotestCount));



    }
    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.sendResultsButton){
            Handler handler = new Handler(Looper.getMainLooper());
            ResultSendJobService.ResultsSender resultsSender=new ResultSendJobService.ResultsSender(getContext(),this, handler);
            resultsSender.execute();
            view.setEnabled(false);
        }
    }

    @Override
    public void OnSendingFinish(int total_sended_count, int errors_count) {
        evaluationsInformer=new EvaluationsInformer();
        evaluationsInformer.execute();
    }
    @Override
    public void onProgressUpdate(int progress) {

    }


    class EvaluationsInformer extends AsyncTask<Object,Object,Object> {
        protected Object doInBackground(Object... objects) {

            int total_evaluations=databaseManager.testDatabase.daoAccess().fetchEvaluationsCount();
            int saved_evaluations=databaseManager.testDatabase.daoAccess().fetchReceivedEvaluationsCount();
            int wally_evaluations=databaseManager.testDatabase.daoAccess().fetchTestEvaluationsCount("wally");
            int aces_evaluations=databaseManager.testDatabase.daoAccess().fetchTestEvaluationsCount("ace");
            int corsis_evaluations=databaseManager.testDatabase.daoAccess().fetchTestEvaluationsCount("corsi");
            int hnf_evaluations=databaseManager.testDatabase.daoAccess().fetchTestEvaluationsCount("hnf");
            int fonotest_evaluations=databaseManager.testDatabase.daoAccess().fetchTestEvaluationsCount("fonotest");


            int[] result=new int[]{
                    total_evaluations,
                    saved_evaluations,
                    aces_evaluations,wally_evaluations,corsis_evaluations,hnf_evaluations,fonotest_evaluations};
            return result;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            int[] result=(int[])o;
            int evals_count=result[0];
            int saved_evals_count= result[1];
            int acesEvaluationsCount = result[2];
            int wallyEvaluationsCount = result[3];
            int corsisEvaluationsCount = result[4];
            int hnfEvaluationsCount = result[5];
            int fonotestEvaluationsCount = result[6];
            int evals_to_send_count=evals_count-saved_evals_count;
            updateNumbers(evals_count,saved_evals_count,evals_to_send_count, acesEvaluationsCount, wallyEvaluationsCount, corsisEvaluationsCount, hnfEvaluationsCount, fonotestEvaluationsCount);

        }
    }

}
