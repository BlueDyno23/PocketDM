package com.example.pocketdm.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketdm.R;

public class PredictorColumnsAdapter extends RecyclerView.Adapter<PredictorColumnsAdapter.ViewHolder> {

    private String[] columns;
    private OnColumnCheckedListener onColumnCheckedListener;
    public interface OnColumnCheckedListener {
        void onColumnChecked(int position, boolean isChecked);
    }

    public PredictorColumnsAdapter(String[] columns, OnColumnCheckedListener onColumnCheckedListener) {
        this.columns = columns;
        this.onColumnCheckedListener = onColumnCheckedListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView columnLabel;
        public CheckBox columnCheckbox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            columnLabel = itemView.findViewById(R.id.predictorColumnName);
            columnCheckbox = itemView.findViewById(R.id.predictorColumnCheckbox);
            columnCheckbox.setOnCheckedChangeListener((v, isChecked) -> onColumnCheckedListener.onColumnChecked(getAdapterPosition(), isChecked));
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.columns_checkbox, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.columnLabel.setText(columns[position]);
    }

    @Override
    public int getItemCount() {
        return 0;
    }


}
