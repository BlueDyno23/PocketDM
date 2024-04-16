package com.example.pocketdm.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pocketdm.Activities.BaseActivity;
import com.example.pocketdm.Adapters.TableAdapter;

import com.example.pocketdm.R;
public class EditorFragment extends Fragment {

    private RecyclerView gridRecyclerView;
    private TableAdapter tableAdapter;
    private String[][] data;
    SharedPreferences sharedPreferences;

    public EditorFragment() {
        // Required empty public constructor
    }

    public static EditorFragment newInstance() {
        EditorFragment fragment = new EditorFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getContext().getSharedPreferences("SETTINGS", Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editor, container, false);
        initiateViews(view);

        return view;
    }

    private void initiateViews(View view){
        gridRecyclerView = view.findViewById(R.id.grid_recycler_view);
        prepareData();
    }

    private void prepareData(){
        if(BaseActivity.datasetModel != null)
        {
            data = BaseActivity.datasetModel.getData(getContext());
            tableAdapter = new TableAdapter(getContext(), data, sharedPreferences.getInt("MAX_ROW_COUNT", 100)+1);
            gridRecyclerView.setAdapter(tableAdapter);
            gridRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), BaseActivity.datasetModel.getColumnsCount()));
        }
    }
}