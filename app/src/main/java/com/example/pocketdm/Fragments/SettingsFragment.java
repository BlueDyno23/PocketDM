package com.example.pocketdm.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pocketdm.R;
import com.google.android.material.button.MaterialButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment implements View.OnClickListener {
    EditText maxRowsEditText;
    MaterialButton settingsApplyBtn;
    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        initiateViews(view);
        return view;
    }

    private void initiateViews(View view) {
        maxRowsEditText = view.findViewById(R.id.editMaxRowCount);
        settingsApplyBtn = view.findViewById(R.id.settingsApplyBtn);
        settingsApplyBtn.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("SETTINGS", Context.MODE_PRIVATE);
        maxRowsEditText.setHint(String.valueOf(sharedPreferences.getInt("MAX_ROW_COUNT", 100)));
    }
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.settingsApplyBtn){
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("SETTINGS", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putInt("MAX_ROW_COUNT", Integer.parseInt(maxRowsEditText.getText().toString()));
            editor.apply();

            Toast.makeText(getContext(), "Settings applied", Toast.LENGTH_SHORT).show();
        }
    }
}