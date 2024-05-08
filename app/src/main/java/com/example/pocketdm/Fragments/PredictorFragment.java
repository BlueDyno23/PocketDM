package com.example.pocketdm.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.pocketdm.Activities.BaseActivity;
import com.example.pocketdm.Adapters.PredictorColumnsAdapter;
import com.example.pocketdm.Adapters.RadioGroupAdapter;
import com.example.pocketdm.R;
import com.example.pocketdm.Utilities.HelperDb;
import com.example.pocketdm.Utilities.SQLUtils;
import com.google.android.material.slider.Slider;

import java.util.ArrayList;

public class PredictorFragment extends Fragment implements PredictorColumnsAdapter.OnColumnCheckedListener, RadioGroupAdapter.OnRadioSelectedListener {

    RecyclerView columnsRecyclerView, inputsRecyclerView;
    RadioGroup indexColumnRadioGroup;
    Slider trainPercentageSlider;
    TextView outputLabel;
    Button predictorClassifyBtn, predictorRegressionBtn;

    PredictorColumnsAdapter columnsAdapter;
    RadioGroupAdapter radioGroupAdapter;
    ArrayList<String> selectedColumns;
    public PredictorFragment() {
    }

    public static PredictorFragment newInstance() {
        PredictorFragment fragment = new PredictorFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_predictor, container, false);
        initiateViews(view);
        return view;
    }

    private void initiateViews(View view) {
        selectedColumns = new ArrayList<String>();

        columnsRecyclerView = view.findViewById(R.id.predictor_columns_check);
        inputsRecyclerView = view.findViewById(R.id.predictor_inputs);
        indexColumnRadioGroup = view.findViewById(R.id.predictor_index_column);
        trainPercentageSlider = view.findViewById(R.id.predictor_percentage_slider);
        outputLabel = view.findViewById(R.id.predictor_output_text);
        predictorClassifyBtn = view.findViewById(R.id.predictor_classify_btn);
        predictorRegressionBtn = view.findViewById(R.id.predictor_regression_btn);

        prepareData();
    }

    private void prepareData() {
        if(BaseActivity.datasetModel != null) {
            HelperDb helperDb = new HelperDb(getContext());
            SQLUtils sqlUtils = new SQLUtils(helperDb.getReadableDatabase());
            String[] columns = sqlUtils.getColumnNames(BaseActivity.datasetModel.getDatasetNickname());
            
            columnsAdapter = new PredictorColumnsAdapter(columns, this);
            columnsRecyclerView.setAdapter(columnsAdapter);
            columnsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            radioGroupAdapter = new RadioGroupAdapter(getContext(), selectedColumns, this);
        }
    }

    @Override
    public void onColumnChecked(int position, boolean isChecked) {
        if(isChecked){
            selectedColumns.add(columnsAdapter.columns[position]);
        }
        else{
            selectedColumns.remove(columnsAdapter.columns[position]);
        }
        updateRadioGroup();
    }

    private void updateRadioGroup(){ // if doesnt work, pass data as parameter?
        indexColumnRadioGroup.removeAllViews();
        for (int i = 0; i < radioGroupAdapter.getCount(); i++) {
            View view = radioGroupAdapter.getView(i, null, indexColumnRadioGroup);
            indexColumnRadioGroup.addView(view);
        }
    }

    @Override
    public void onRadioSelected(int position) {

    }
}