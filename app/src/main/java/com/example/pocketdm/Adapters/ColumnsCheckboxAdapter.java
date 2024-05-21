package com.example.pocketdm.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketdm.Activities.BaseActivity;
import com.example.pocketdm.R;
import com.example.pocketdm.Utilities.HelperDb;
import com.example.pocketdm.Utilities.SQLUtils;

public class ColumnsCheckboxAdapter extends RecyclerView.Adapter<ColumnsCheckboxAdapter.ViewHolder> {

    public String[] columns;
    private Context context;
    private OnColumnCheckedListener onColumnCheckedListener;
    public interface OnColumnCheckedListener {
        void onColumnChecked(View view ,int position, boolean isChecked, String columnName);
    }

    public ColumnsCheckboxAdapter(Context context, String[] columns, OnColumnCheckedListener onColumnCheckedListener) {
        this.columns = columns;
        this.onColumnCheckedListener = onColumnCheckedListener;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView columnLabel, columnType;
        public CheckBox columnCheckbox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            columnLabel = itemView.findViewById(R.id.predictorColumnName);
            columnType = itemView.findViewById(R.id.predictorColumnType);
            columnCheckbox = itemView.findViewById(R.id.predictorColumnCheckbox);
            columnCheckbox.setOnCheckedChangeListener((v, isChecked) -> onColumnCheckedListener.onColumnChecked(itemView,getAdapterPosition(), isChecked, columns[getAdapterPosition()]));
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
        holder.columnType.setText(new SQLUtils(new HelperDb(context).getReadableDatabase()).getColumnType(BaseActivity.datasetModel.getDatasetNickname(), columns[position]).toString());
    }

    @Override
    public int getItemCount() {
        return columns.length;
    }

    public void uncheckById(int id) {
        onColumnCheckedListener.onColumnChecked(null, id, false, columns[id]);
        notifyDataSetChanged();
    }

    public void checkById(int id) {
        onColumnCheckedListener.onColumnChecked(null, id, true, columns[id]);
        notifyDataSetChanged();
    }

    public void checkAll() {
        for (int i = 0; i < columns.length; i++) {
            onColumnCheckedListener.onColumnChecked(null, i, true, columns[i]);
        }
        notifyDataSetChanged();
    }
    public void uncheckAll() {
        for (int i = 0; i < columns.length; i++) {
            onColumnCheckedListener.onColumnChecked(null, i, false, columns[i]);
        }
        notifyDataSetChanged();
    }

}
