package com.example.e440.menu;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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


    void updateNumbers(int total,int saved, int to_send){

        if(to_send>0){
            send_results_button.setEnabled(true);
        }
        TextView total_text_view=inflatedView.findViewById(R.id.evalsCountTextView);
        TextView saved_text_view=inflatedView.findViewById(R.id.savedEvalsTextView);
        TextView to_send_text_view=inflatedView.findViewById(R.id.evalsToSendTextView);
        total_text_view.setText(Integer.toString(total));
        saved_text_view.setText(Integer.toString(saved));
        to_send_text_view.setText(Integer.toString(to_send));

    }
    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.sendResultsButton){
            ResultSendJobService.ResultsSender resultsSender=new ResultSendJobService.ResultsSender(getContext(),this);
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
            int[] result=new int[]{total_evaluations,saved_evaluations};
            return result;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            int[] result=(int[])o;
            int evals_count=result[0];
            int saved_evals_count= result[1];
            int evals_to_send_count=evals_count-saved_evals_count;
            updateNumbers(evals_count,saved_evals_count,evals_to_send_count);

        }
    }

}
