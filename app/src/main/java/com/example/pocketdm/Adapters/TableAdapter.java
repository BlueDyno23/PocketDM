package com.example.pocketdm.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pocketdm.R;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.ViewHolder> {

    private Context mContext;
    private String[][] mDataset;
    private int maxRowCount; // TODO maxRowCount not being used

    public TableAdapter(Context context, String[][] dataset, int maxRowCount) {
        mContext = context;
        mDataset = dataset;
        this.maxRowCount = Math.min(maxRowCount, dataset.length);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.cell_text_view);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_data_cell, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int row = position / mDataset[0].length;
        int col = position % mDataset[0].length;

        if (row == 0) {
            holder.textView.setBackgroundResource(R.drawable.header_cell_bg);
            holder.textView.setTextColor(mContext.getResources().getColor(android.R.color.system_on_primary_container_light));
            holder.textView.setText(mDataset[row][col]); // Assuming headers are provided in dataset
        } else {
            holder.textView.setBackgroundResource(R.drawable.data_cell_bg);
            holder.textView.setTextColor(mContext.getResources().getColor(android.R.color.system_primary_light));
            holder.textView.setText(mDataset[row][col]);
        }
    }

    @Override
    public int getItemCount() {
        int rowCount = getRowCount();
        return rowCount * mDataset[0].length;
    }

    public int getRowCount() {
        return maxRowCount;
    }
}