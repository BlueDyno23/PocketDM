package com.example.pocketdm.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketdm.Models.DatasetModel;
import com.example.pocketdm.R;
import com.google.android.material.button.MaterialButton;

import org.w3c.dom.Text;

import java.util.List;

public class DatasetItemsAdapter extends RecyclerView.Adapter<DatasetItemsAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onOpenClicked(int position);
        void onEditClicked(int position);
        void onDeleteClicked(int position);
    }
    private List<DatasetModel> datasetModelList;
    private OnItemClickListener listener;

    public DatasetItemsAdapter(List<DatasetModel> datasetModelList, OnItemClickListener listener) {
        this.datasetModelList = datasetModelList;
        this.listener = listener;
    }
    @NonNull
    @Override
    public DatasetItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dataset_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DatasetItemsAdapter.ViewHolder holder, int position) {
        DatasetModel item = datasetModelList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return datasetModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nicknameTextView;
        private TextView nameTextView;
        private TextView descriptionTextView;
        private TextView versionTextView;
        private TextView columnsTextView;
        private TextView rowsTextView;
        private MaterialButton openButton;
        private MaterialButton editButton;
        private MaterialButton deleteButton;


        public ViewHolder(View itemView) {
            super(itemView);
            nicknameTextView = itemView.findViewById(R.id.dataset_item_nickname);
            nameTextView = itemView.findViewById(R.id.dataset_item_name);
            descriptionTextView = itemView.findViewById(R.id.dataset_item_description);
            versionTextView = itemView.findViewById(R.id.dataset_item_version);
            columnsTextView = itemView.findViewById(R.id.dataset_item_columns);
            rowsTextView = itemView.findViewById(R.id.dataset_item_rows);
            openButton = itemView.findViewById(R.id.dataset_item_open);
            editButton = itemView.findViewById(R.id.dataset_item_edit);
            deleteButton = itemView.findViewById(R.id.dataset_item_delete);

            openButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onOpenClicked(position);
                }
            });

            editButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onEditClicked(position);
                }
            });

            deleteButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onDeleteClicked(position);
                }
            });
        }

        public void bind(DatasetModel item) {
            nicknameTextView.setText(item.getDatasetNickname());
            nameTextView.setText(item.getDatasetName());
            descriptionTextView.setText(item.getDatasetDescription());
            versionTextView.setText("v" + String.valueOf(item.getDatasetVersion())); // Convert to String
            columnsTextView.setText("columns\t"+String.valueOf(item.getColumnsCount())); // Convert to String
            rowsTextView.setText("rows\t"+String.valueOf(item.getRowsCount())); // Convert to String
        }

    }
}
