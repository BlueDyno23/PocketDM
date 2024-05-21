package com.example.pocketdm.Fragments;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Scatter;
import com.example.pocketdm.Activities.BaseActivity;
import com.example.pocketdm.Adapters.ColumnsCheckboxAdapter;
import com.example.pocketdm.Enums.ColumnType;
import com.example.pocketdm.R;
import com.example.pocketdm.Utilities.HelperDb;
import com.example.pocketdm.Utilities.SQLUtils;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class VisualizerFragment extends Fragment implements View.OnClickListener, ColumnsCheckboxAdapter.OnColumnCheckedListener {

    private AnyChartView anyChartView;
    private MaterialButton plotBtn;
    private HelperDb helperDb;
    private ColumnsCheckboxAdapter categorialsAdapter, numericalsAdapter;
    private RecyclerView categorialsRecyclerView, numericalsRecyclerView;

    private ArrayList<String> selectedNumerics = new ArrayList<>();
    private ArrayList<String> selectedCategorials = new ArrayList<>();
    public VisualizerFragment() {
        // Required empty public constructor
    }

    public static VisualizerFragment newInstance() {
        VisualizerFragment fragment = new VisualizerFragment();
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
        View view = inflater.inflate(R.layout.fragment_visualizer, container, false);
        helperDb = new HelperDb(getContext());
        initializeViews(view);

        return view;
    }

    private void initializeViews(View view) {
        anyChartView = view.findViewById(R.id.chartView);

        categorialsRecyclerView = view.findViewById(R.id.visualizer_categorial_rv);
        numericalsRecyclerView = view.findViewById(R.id.visualizer_numerical_rv);

        ArrayList<String> categorials = new ArrayList<>();
        ArrayList<String> numericals = new ArrayList<>();
        SQLUtils sqlUtils = new SQLUtils(helperDb.getReadableDatabase());

        for (String v : sqlUtils.getColumnNames(BaseActivity.datasetModel.getDatasetNickname())) {
            if (sqlUtils.getColumnType(BaseActivity.datasetModel.getDatasetNickname(), v) == ColumnType.CATEGORICAL) {
                categorials.add(v);
            } else if (sqlUtils.getColumnType(BaseActivity.datasetModel.getDatasetNickname(), v) == ColumnType.NUMERIC) {
                {
                    numericals.add(v);
                }
            }
        }

        plotBtn = view.findViewById(R.id.plotBtn);
        plotBtn.setOnClickListener(this);

        categorialsAdapter = new ColumnsCheckboxAdapter(getContext(), categorials.toArray(new String[0]), this);
        numericalsAdapter = new ColumnsCheckboxAdapter(getContext(), numericals.toArray(new String[0]), this);
        categorialsRecyclerView.setAdapter(categorialsAdapter);
        categorialsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        numericalsRecyclerView.setAdapter(numericalsAdapter);
        numericalsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

    }

    @Override
    public void onClick (View v){
        if (v.getId() == plotBtn.getId()) {
            plot();
        }
    }

    private void plot() {
        Scatter scatter = AnyChart.scatter();

        if (selectedNumerics.size() != 2) {
            Toast.makeText(getContext(), "Please select two numerical columns", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedCategorials.size() == 0) {
            Toast.makeText(getContext(), "Please select at least one categorical column", Toast.LENGTH_SHORT).show();
            return;
        }
        SQLUtils sqlUtils = new SQLUtils(helperDb.getReadableDatabase());

        String[] uniques = sqlUtils.findUniqueValues(BaseActivity.datasetModel.getDatasetNickname(), selectedCategorials.get(0));

        scatter.title(BaseActivity.datasetModel.getDatasetNickname() + " Scatter Plot");
        scatter.xAxis(0).title(selectedNumerics.get(0));
        scatter.yAxis(0).title(selectedNumerics.get(1));

        /*int i = 0;
        for (String category : uniques) {
            List<DataEntry> data = new ArrayList<>();
            String[] columns = {selectedCategorials.get(0), selectedNumerics.get(0), selectedNumerics.get(1)};
            Cursor cursor = helperDb.getReadableDatabase().query(BaseActivity.datasetModel.getDatasetNickname(), columns, null, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    double xValue = cursor.getDouble(1); // Get first numeric value
                    double yValue = cursor.getDouble(2); // Get second numeric value
                    data.add(new ValueDataEntry(xValue, yValue));
                } while (cursor.moveToNext());
                cursor.close();
            }

            Set set = Set.instantiate();
            set.data(data);
            Mapping mapping = set.mapAs("{ x: 'x', value: 'y' }");

            scatter.marker(mapping).name(category);
        }*/

        for(String u : uniques)
        {
            List<DataEntry> data = new ArrayList<>();
            String[] columns = {selectedCategorials.get(0), selectedNumerics.get(0), selectedNumerics.get(1)};
            Cursor cursor = helperDb.getReadableDatabase().query(BaseActivity.datasetModel.getDatasetNickname(), columns, null, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    double xValue = cursor.getDouble(1); // Get first numeric value
                    double yValue = cursor.getDouble(2); // Get second numeric value
                    if(cursor.getString(0).equals(u)){
                        data.add(new ValueDataEntry(xValue, yValue));
                    }
                } while (cursor.moveToNext());
                cursor.close();
            }
            com.anychart.core.scatter.series.Marker marker = scatter.marker(data);
            marker.name(u);
        }

        anyChartView.setChart(scatter);
    }


    @Override
    public void onColumnChecked(View view, int position, boolean isChecked, String columnName) {
        SQLUtils sqlUtils = new SQLUtils(helperDb.getReadableDatabase());
        if(sqlUtils.getColumnType(BaseActivity.datasetModel.getDatasetNickname(), columnName) == ColumnType.CATEGORICAL){
            if(isChecked){
                selectedCategorials.add(columnName);
                if(selectedCategorials.size()>1){
                    Toast.makeText(getContext(), "Please select only one categorical column", Toast.LENGTH_SHORT).show();
                    categorialsAdapter.uncheckById(position);
                    selectedCategorials.remove(columnName);
                }
            }else{
                selectedCategorials.remove(columnName);
            }
        }else if(sqlUtils.getColumnType(BaseActivity.datasetModel.getDatasetNickname(), columnName) == ColumnType.NUMERIC){
            if(isChecked){
                selectedNumerics.add(columnName);
                if(selectedNumerics.size()>2){
                    Toast.makeText(getContext(), "Please select only two numerical columns", Toast.LENGTH_SHORT).show();
                    numericalsAdapter.uncheckById(position);
                    selectedNumerics.remove(columnName);
                }
            }else{
                selectedNumerics.remove(columnName);
            }
        }
    }
}

