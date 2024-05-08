package com.example.pocketdm.Adapters;

import android.content.Context;
import android.renderscript.ScriptGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketdm.R;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class InputsAdapter extends RecyclerView.Adapter<InputsAdapter.ViewHolder> {

    private String[] data;
    private Context context;

    public InputsAdapter(Context context, String[] data){
        this.data = data;
        this.context = context;
    }
    @NonNull
    @Override
    public InputsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inputs_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InputsAdapter.ViewHolder holder, int position) {
        holder.textInputLayout.setHint(data[position]);
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextInputLayout textInputLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textInputLayout = itemView.findViewById(R.id.predictor_input_field);
        }
    }
}
