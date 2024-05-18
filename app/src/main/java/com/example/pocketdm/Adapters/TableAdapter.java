package com.example.pocketdm.Adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pocketdm.R;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.ViewHolder> {

    private Context mContext;
    private String[][] mDataset;
    private int maxRowCount;
    private OnCellClickedListener onCellClickedListener;

    public interface OnCellClickedListener {
        void onCellClicked(View view, int row, int col);
    }

    public TableAdapter(Context context, String[][] dataset, int maxRowCount, OnCellClickedListener onCellClickedListener) {
        mContext = context;
        mDataset = dataset;
        this.maxRowCount = Math.min(maxRowCount, dataset.length);
        this.onCellClickedListener = onCellClickedListener;
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
        int columnsCount = mDataset[0].length;
        int row = position / columnsCount;
        int col = position % columnsCount;

        if (row == 0) {
            holder.textView.setBackgroundResource(R.drawable.header_cell_bg);
            holder.textView.setTextColor(mContext.getResources().getColor(android.R.color.system_on_primary_container_light));
            holder.textView.setCompoundDrawableTintList(ColorStateList.valueOf(mContext.getResources().getColor(android.R.color.system_on_primary_container_light)));
        } else {
            holder.textView.setBackgroundResource(R.drawable.data_cell_bg);
            holder.textView.setTextColor(mContext.getResources().getColor(android.R.color.system_primary_light));
        }

        holder.textView.setText(mDataset[row][col]);
        holder.textView.setOnClickListener(v -> onCellClickedListener.onCellClicked(v, row, col));
    }

    @Override
    public int getItemCount() {
        if (mDataset == null || mDataset.length == 0 || mDataset[0].length == 0) {
            return 0;
        }
        int columnsCount = mDataset[0].length;
        return maxRowCount * columnsCount;
    }

    public void setData(String[][] data) {
        mDataset = data;
        this.maxRowCount = Math.min(maxRowCount, data.length);
        notifyDataSetChanged();
    }
}
