package com.example.pocketdm.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pocketdm.Activities.BaseActivity;
import com.example.pocketdm.Adapters.InputsAdapter;
import com.example.pocketdm.Adapters.ColumnsCheckboxAdapter;
import com.example.pocketdm.Enums.ColumnType;
import com.example.pocketdm.MachineLearning.KNearestNeighbor;
import com.example.pocketdm.R;
import com.example.pocketdm.Utilities.HelperDb;
import com.example.pocketdm.Utilities.SQLUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.slider.Slider;

import java.util.ArrayList;

public class PredictorFragment extends Fragment implements ColumnsCheckboxAdapter.OnColumnCheckedListener, RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    RecyclerView columnsRecyclerView, inputsRecyclerView;
    RadioGroup indexColumnRadioGroup;
    Slider trainPercentageSlider;
    TextView outputLabel;
    Button predictorClassifyBtn, predictorRegressionBtn;

    ColumnsCheckboxAdapter columnsAdapter;
    InputsAdapter inputsAdapter;
    ArrayList<String> selectedColumns;
    HelperDb helperDb;
    private String predictedColumn;

    public PredictorFragment() {}

    public static PredictorFragment newInstance() {
        return new PredictorFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_predictor, container, false);
        initiateViews(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
        builder.setTitle("Info");
        builder.setMessage("The current prediction algorithm using KNN therefore please only work with numerical values as parameters.");
        builder.setPositiveButton("Ok", null);
        builder.show();
    }

    private void initiateViews(View view) {
        helperDb = new HelperDb(getContext());

        selectedColumns = new ArrayList<>();

        columnsRecyclerView = view.findViewById(R.id.predictor_columns_check);
        inputsRecyclerView = view.findViewById(R.id.predictor_inputs);
        indexColumnRadioGroup = view.findViewById(R.id.predictor_index_column);
        indexColumnRadioGroup.setOnCheckedChangeListener(this);
        outputLabel = view.findViewById(R.id.predictor_output_text);
        predictorClassifyBtn = view.findViewById(R.id.predictor_classify_btn);
        predictorClassifyBtn.setOnClickListener(this);
        // predictorRegressionBtn = view.findViewById(R.id.predictor_regression_btn);
        // predictorRegressionBtn.setOnClickListener(this);

        prepareData();
    }

    private void prepareData() {
        if (BaseActivity.datasetModel != null) {
            SQLUtils sqlUtils = new SQLUtils(helperDb.getReadableDatabase());
            String[] columns = sqlUtils.getColumnNames(BaseActivity.datasetModel.getDatasetNickname());

            columnsAdapter = new ColumnsCheckboxAdapter(getContext(), columns, this);
            columnsRecyclerView.setAdapter(columnsAdapter);
            columnsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            inputsAdapter = new InputsAdapter(getContext(), getSelectedColumnsNoPredicted());
            inputsRecyclerView.setAdapter(inputsAdapter);
            inputsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }
    }

    @Override
    public void onColumnChecked(View view,int position, boolean isChecked, String columnName) {
        if (isChecked) {
            selectedColumns.add(columnsAdapter.columns[position]);
        } else {
            selectedColumns.remove(columnsAdapter.columns[position]);
        }
        inputsAdapter.notifyDataSetChanged();
        updateRadioGroup();
    }

    private void updateRadioGroup() {
        ArrayList<String> columnsToShow = getSelectedColumnsNoPredicted();
        indexColumnRadioGroup.removeAllViews();

        SQLUtils sqlUtils = new SQLUtils(helperDb.getReadableDatabase());

        for (int i = 0; i < columnsToShow.size(); i++) {
            String column = columnsToShow.get(i);
            ColumnType columnType = sqlUtils.getColumnType(BaseActivity.datasetModel.getDatasetNickname(), column);

            if (!(columnType == ColumnType.CATEGORICAL || columnType == ColumnType.BINARY || columnType == ColumnType.BINARY_TEXT)) {
                continue;
            }

            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setText(column);
            radioButton.setId(i);
            final String columnFinal = column;
            if (column.equals(predictedColumn)) {
                radioButton.setChecked(true);
            }
            radioButton.setOnClickListener(v -> {
                predictedColumn = columnFinal;
                inputsAdapter = new InputsAdapter(getContext(), getSelectedColumnsNoPredicted());
                inputsRecyclerView.setAdapter(inputsAdapter);
            });
            indexColumnRadioGroup.addView(radioButton);
        }
    }



    private ArrayList<String> getSelectedColumnsNoPredicted() {
        if (predictedColumn == null) return selectedColumns;

        ArrayList<String> selectedColumnsNoPredicted = new ArrayList<>();
        for (String column : selectedColumns) {
            if (!column.equals(predictedColumn)) {
                selectedColumnsNoPredicted.add(column);
            }
        }
        return selectedColumnsNoPredicted;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        RadioButton button = group.findViewById(checkedId);
        predictedColumn = button.getText().toString();
        inputsAdapter = new InputsAdapter(getContext(), getSelectedColumnsNoPredicted());
        inputsRecyclerView.setAdapter(inputsAdapter);
    }

    private boolean allInputsFilled() {
        for (int i = 0; i < inputsAdapter.getItemCount(); i++) {
            if (inputsAdapter.getInputValue(i) == null || inputsAdapter.getInputValue(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private String[] getInputs() {
        String[] inputs = new String[inputsAdapter.getItemCount()];
        for (int i = 0; i < inputsAdapter.getItemCount(); i++) {
            inputs[i] = inputsAdapter.getInputValue(i);
        }
        return inputs;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == predictorClassifyBtn.getId()) {
            if (allInputsFilled() && predictedColumn != null) {
                String[] inputs = getInputs();
                double[] array = convertStringArrayToDoubleArray(inputs);

                SQLUtils sqlUtils = new SQLUtils(helperDb.getWritableDatabase());
                ColumnType columnType = sqlUtils.getColumnType(BaseActivity.datasetModel.getDatasetNickname(), predictedColumn);
                if (!(columnType == ColumnType.CATEGORICAL || columnType == ColumnType.BINARY || columnType == ColumnType.BINARY_TEXT)) {
                    Toast.makeText(getContext(), "Please select a categorical (or binary) column", Toast.LENGTH_SHORT).show();
                    return;
                }
                sqlUtils.duplicateTable(BaseActivity.datasetModel.getDatasetNickname(), BaseActivity.datasetModel.getDatasetNickname() + "_predict_tmp");
                for (String column : sqlUtils.getColumnNames(BaseActivity.datasetModel.getDatasetNickname())) {
                    if (!selectedColumns.contains(column)) {
                        sqlUtils.dropColumn(BaseActivity.datasetModel.getDatasetNickname() + "_predict_tmp", column);
                    }
                }

                KNearestNeighbor knn = new KNearestNeighbor(3, helperDb, BaseActivity.datasetModel.getDatasetNickname() + "_predict_tmp", predictedColumn);
                String result = knn.predict(array, predictedColumn);

                sqlUtils.dropTable(BaseActivity.datasetModel.getDatasetNickname() + "_predict_tmp");
                Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                outputLabel.setText(result);
            } else {
                Toast.makeText(getContext(), "Please fill all inputs, or select a valid column to predict", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private double[] convertStringArrayToDoubleArray(String[] inputs) {
        double[] array = new double[inputs.length];
        for (int i = 0; i < inputs.length; i++) {
            array[i] = Double.parseDouble(inputs[i].replace("\"", ""));
        }
        return array;
    }
}
