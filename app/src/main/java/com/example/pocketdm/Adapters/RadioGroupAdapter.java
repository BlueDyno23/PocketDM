package com.example.pocketdm.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketdm.Activities.BaseActivity;
import com.example.pocketdm.Enums.ColumnType;
import com.example.pocketdm.R;
import com.example.pocketdm.Utilities.HelperDb;
import com.example.pocketdm.Utilities.SQLUtils;

import java.util.ArrayList;

public class RadioGroupAdapter extends BaseAdapter
{
    // MIGHT BE USELESS ??
    private ArrayList<String> data;
    private Context context;
    private int selectedPosition = -1;
    public OnRadioSelectedListener onRadioSelectedListener;
    public RadioGroupAdapter(Context context, ArrayList<String> data, OnRadioSelectedListener onRadioSelectedListener) {
        this.context = context;
        this.data = data;
        this.onRadioSelectedListener = onRadioSelectedListener;
    }

    public interface OnRadioSelectedListener {
        void onRadioSelected(int position);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.radio_item, null);
            holder = new ViewHolder();
            holder.radioButton = view.findViewById(R.id.radioButton);
            view.setTag(holder);
        }
        else {
            holder = (ViewHolder) view.getTag();
        }

        holder.radioButton.setText(data.get(position));
        holder.radioButton.setChecked(position == selectedPosition);

        holder.radioButton.setOnClickListener(v -> {
            if (selectedPosition != position) {
                selectedPosition = position;
                notifyDataSetChanged();
                onRadioSelectedListener.onRadioSelected(position);
            }
        });
        return view;
    }

    static class ViewHolder {
        RadioButton radioButton;
    }
}