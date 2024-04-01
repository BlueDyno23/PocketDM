package com.example.pocketdm.Fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pocketdm.Activities.BaseActivity;
import com.example.pocketdm.Adapters.DatasetItemsAdapter;
import com.example.pocketdm.Models.DatasetModel;
import com.example.pocketdm.R;
import com.example.pocketdm.Utilities.HelperSecretDb;
import com.example.pocketdm.Utilities.SQLUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements View.OnClickListener, DatasetItemsAdapter.OnItemClickListener {

    private FloatingActionButton fabAddDataset;
    private RecyclerView datasetItemsRecyclerView;
    private DatasetItemsAdapter datasetItemsAdapter;
    private List<DatasetModel> datasetModelList;
    private SQLiteDatabase database;
    private SQLUtils sqlUtils;
    public HomeFragment() {
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initiateViews(view);
        return view;
    }

    private void initiateViews(View view) {
        fabAddDataset = view.findViewById(R.id.fab_add_dataset);
        fabAddDataset.setOnClickListener(this);
        datasetItemsRecyclerView = view.findViewById(R.id.dataset_items_recyclerView);

        getDatasetModelsList();
        datasetItemsAdapter = new DatasetItemsAdapter(datasetModelList);
        datasetItemsRecyclerView.setAdapter(datasetItemsAdapter);

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.fab_add_dataset) {
            AddDatasetDialogFragment addDatasetDialogFragment = AddDatasetDialogFragment.newInstance();
            addDatasetDialogFragment.show(getChildFragmentManager(), "AddDatasetDialogFragment");
        }
    }

    private void getDatasetModelsList() {
        HelperSecretDb hsb = new HelperSecretDb(getContext());
        SQLUtils sqlUtils = new SQLUtils(hsb.getReadableDatabase());

        datasetModelList = sqlUtils.getDatasetModels(HelperSecretDb.HTBL_NAME);

        datasetItemsAdapter = new DatasetItemsAdapter(datasetModelList, this);
        datasetItemsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        datasetItemsRecyclerView.setAdapter(datasetItemsAdapter);
    }


    @Override
    public void onOpenClicked(int position) {
        BaseActivity baseActivity = (BaseActivity) getActivity();
        baseActivity.openDataset(datasetModelList.get(position).getDatasetNickname());
    }

    @Override
    public void onEditClicked(int position) {

    }

    @Override
    public void onDeleteClicked(int position) {
        HelperSecretDb hsb = new HelperSecretDb(getContext());
        SQLUtils sqlUtils = new SQLUtils(hsb.getWritableDatabase());

        sqlUtils.deleteData(HelperSecretDb.HTBL_NAME, "name = ?", new String[]{datasetModelList.get(position).getDatasetName()});

        sqlUtils = new SQLUtils(hsb.getWritableDatabase());
        sqlUtils.dropTable(datasetModelList.get(position).getDatasetNickname());
    }
}