package com.example.e440.menu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class EvaluationsCountAdapter extends RecyclerView.Adapter<EvaluationsCountAdapter.ViewHolder>  {

    private EvaluationCount[] evaluationCounts;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView evaluationsCountTextTextView;
        private final TextView evaluationsCountTextView;
        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            evaluationsCountTextTextView = (TextView) view.findViewById(R.id.evaluationsCountText);
            evaluationsCountTextView = (TextView) view.findViewById(R.id.evaluationsCount);
        }

    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView.
     */
    public EvaluationsCountAdapter(EvaluationCount[] dataSet) {
        evaluationCounts = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.evaluations_count_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.evaluationsCountTextTextView.setText(evaluationCounts[position].getText());
        viewHolder.evaluationsCountTextView.setText(evaluationCounts[position].getCount());
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return evaluationCounts.length;
    }


}
