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

import java.util.ArrayList;
import java.util.Vector;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.ViewHolder> {

    private Context mContext;
    private String[][] mDataset;
    private int maxRowCount;
    private OnCellClickedListener onCellClickedListener;

    private ArrayList<Integer> highlightedRows;
    private ArrayList<Integer> highlightedColumns;
    private int highlightedCellRow = -1;
    private int highlightedCellCol = -1;


    public interface OnCellClickedListener {
        boolean onCellClicked(View view, int row, int col);
    }

    public TableAdapter(Context context, String[][] dataset, int maxRowCount, OnCellClickedListener onCellClickedListener) {
        mContext = context;
        mDataset = dataset;
        this.maxRowCount = Math.min(maxRowCount, dataset.length);
        this.onCellClickedListener = onCellClickedListener;

        highlightedRows = new ArrayList<>();
        highlightedColumns = new ArrayList<>();
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
        }
        else {
            holder.textView.setBackgroundResource(R.drawable.data_cell_bg);
            holder.textView.setTextColor(mContext.getResources().getColor(android.R.color.system_primary_light));
        }

        if(highlightedRows.contains(row)) {
            holder.textView.setBackgroundResource(R.drawable.data_cell_bg_highlighted);
        }
        if(highlightedColumns.contains(col) && row!=0) {
            holder.textView.setBackgroundResource(R.drawable.data_column_cell_bg_highlighted);
        }
        if((highlightedCellRow != -1 && highlightedCellCol != -1) && highlightedCellRow == row && highlightedCellCol == col) {
            holder.textView.setBackgroundResource(R.drawable.cell_bg_highlighted);
        }

        holder.textView.setText(mDataset[row][col]);
        holder.textView.setOnLongClickListener(v -> onCellClickedListener.onCellClicked(v, row, col));
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

    public void addHighlightedRow(int row) {
        highlightedRows.add(row);
        notifyDataSetChanged();
    }

    public void addHighlightedColumn(int col) {
        highlightedColumns.add(col);
        notifyDataSetChanged();
    }

    public void resetHighlightedRows() {
        highlightedRows.clear();
        notifyDataSetChanged();
    }

    public void resetHighlightedColumns() {
        highlightedColumns.clear();
        notifyDataSetChanged();
    }

    public void setHighlightedCell(int row, int col) {
        highlightedCellRow = row;
        highlightedCellCol = col;
        notifyDataSetChanged();
    }

    public void resetHighlightedCell() {
        highlightedCellRow = -1;
        highlightedCellCol = -1;
        notifyDataSetChanged();
    }
}
