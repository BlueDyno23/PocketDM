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
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pocketdm.Activities.BaseActivity;
import com.example.pocketdm.Adapters.InputsAdapter;
import com.example.pocketdm.Adapters.PredictorColumnsAdapter;
import com.example.pocketdm.Adapters.RadioGroupAdapter;
import com.example.pocketdm.MachineLearning.KNearestNeighbor;
import com.example.pocketdm.R;
import com.example.pocketdm.Utilities.HelperDb;
import com.example.pocketdm.Utilities.SQLUtils;
import com.google.android.material.slider.Slider;

import java.util.ArrayList;

public class PredictorFragment extends Fragment implements PredictorColumnsAdapter.OnColumnCheckedListener, RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    RecyclerView columnsRecyclerView, inputsRecyclerView;
    RadioGroup indexColumnRadioGroup;
    Slider trainPercentageSlider;
    TextView outputLabel;
    Button predictorClassifyBtn, predictorRegressionBtn;

    PredictorColumnsAdapter columnsAdapter;
    InputsAdapter inputsAdapter;
    ArrayList<String> selectedColumns;

    private String predictedColumn;
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
        selectedColumns = new ArrayList<>();

        columnsRecyclerView = view.findViewById(R.id.predictor_columns_check);
        inputsRecyclerView = view.findViewById(R.id.predictor_inputs);
        indexColumnRadioGroup = view.findViewById(R.id.predictor_index_column);
        indexColumnRadioGroup.setOnCheckedChangeListener(this);
        trainPercentageSlider = view.findViewById(R.id.predictor_percentage_slider);
        outputLabel = view.findViewById(R.id.predictor_output_text);
        predictorClassifyBtn = view.findViewById(R.id.predictor_classify_btn);
        predictorClassifyBtn.setOnClickListener(this);
        predictorRegressionBtn = view.findViewById(R.id.predictor_regression_btn);
        predictorRegressionBtn.setOnClickListener(this);

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

            inputsAdapter = new InputsAdapter(getContext(), getSelectedColumnsNoPredicted());
            inputsRecyclerView.setAdapter(inputsAdapter);
            inputsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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
        inputsAdapter.notifyDataSetChanged();
        updateRadioGroup();
    }

    private void updateRadioGroup(){
        indexColumnRadioGroup.removeAllViews();
        for (int i = 0; i < selectedColumns.size(); i++) {
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setText(selectedColumns.get(i));
            indexColumnRadioGroup.addView(radioButton);
        }
    }

    private ArrayList<String> getSelectedColumnsNoPredicted(){
        if(predictedColumn == null) return selectedColumns;

        ArrayList<String> selectedColumnsNoPredicted = new ArrayList<>();
        for (int i = 0; i < selectedColumns.size(); i++) {
            if(!selectedColumns.get(i).equals(predictedColumn)){
                selectedColumnsNoPredicted.add(selectedColumns.get(i));
            }
        }
        return selectedColumnsNoPredicted;
    }

    // for the radio group
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        RadioButton button = (RadioButton)group.findViewById(checkedId);
        String column = button.getText().toString();

        predictedColumn = column;

        inputsAdapter = new InputsAdapter(getContext(), getSelectedColumnsNoPredicted());
        inputsRecyclerView.setAdapter(inputsAdapter);

    }

    private boolean allInputsFilled(){
        for (int i = 0; i < inputsAdapter.getItemCount(); i++) {
            if(inputsAdapter.getInputValue(i) == null || inputsAdapter.getInputValue(i).isEmpty()){
                return false;
            }
        }
        return true;
    }

    private String[] getInputs(){
        String[] inputs = new String[inputsAdapter.getItemCount()];
        for (int i = 0; i < inputsAdapter.getItemCount(); i++) {
            inputs[i] = inputsAdapter.getInputValue(i);
        }
        return inputs;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==getView().findViewById(R.id.predictor_classify_btn).getId()) {
            if (allInputsFilled()) {
                String[] inputs = getInputs();
                double[] array = convertStringArrayToDoubleArray(inputs);

                HelperDb helperDb = new HelperDb(getContext());
                SQLUtils sqlUtils = new SQLUtils(helperDb.getReadableDatabase());

                KNearestNeighbor knn = new KNearestNeighbor(3, helperDb, BaseActivity.datasetModel.getDatasetNickname(), predictedColumn);
                String result = knn.predict(array, predictedColumn);
                Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                outputLabel.setText(result);
            } else {
                Toast.makeText(getContext(), "Please fill all inputs", Toast.LENGTH_SHORT).show();
            }
        }
        else if(v.getId()==getView().findViewById(R.id.predictor_regression_btn).getId()) {
            if (allInputsFilled()) {
                String[] inputs = getInputs();

            } else {
                Toast.makeText(getContext(), "Please fill all inputs", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private double[] convertStringArrayToDoubleArray(String[] inputs){
        double[] array = new double[inputs.length];
        for (int i = 0; i < inputs.length; i++) {
            array[i] = Double.parseDouble(inputs[i].replace("\"",""));
        }

        return array;
    }
}