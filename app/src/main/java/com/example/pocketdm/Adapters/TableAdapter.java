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

    // Constructor to initialize the adapter with context and dataset
    public TableAdapter(Context context, String[][] dataset) {
        mContext = context;
        mDataset = dataset;
    }

    // ViewHolder class to hold the views for each cell in the table
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
        // Populate each cell with data from the dataset
        int row = position / mDataset[0].length;
        int col = position % mDataset[0].length;

        // Customize row and column headers
        if (row == 0 || col == 0) {
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
        if (mDataset != null) {
            return mDataset.length * mDataset[0].length;
        }
        return 0;
    }
}
