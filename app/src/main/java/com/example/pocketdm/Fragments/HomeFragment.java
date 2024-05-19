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
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.pocketdm.Activities.BaseActivity;
import com.example.pocketdm.Adapters.DatasetItemsAdapter;
import com.example.pocketdm.Models.DatasetModel;
import com.example.pocketdm.R;
import com.example.pocketdm.Utilities.HelperDb;
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
    private ImageButton refreshBtn;
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
        refreshBtn = view.findViewById(R.id.refresh_list_button);
        refreshBtn.setOnClickListener(this);
        datasetItemsRecyclerView = view.findViewById(R.id.dataset_items_recyclerView);

        getDatasetModelsList();
        datasetItemsAdapter = new DatasetItemsAdapter(datasetModelList, this);
        datasetItemsRecyclerView.setAdapter(datasetItemsAdapter);

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.fab_add_dataset) {
            AddDatasetDialogFragment addDatasetDialogFragment = AddDatasetDialogFragment.newInstance();
            addDatasetDialogFragment.show(getChildFragmentManager(), "AddDatasetDialogFragment");
        }
        else if(v.getId() == R.id.refresh_list_button) {
            getDatasetModelsList();
            datasetItemsAdapter.notifyDataSetChanged();
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
        EditDatasetDialogFragment editDatasetDialogFragment = EditDatasetDialogFragment.newInstance(datasetModelList.get(position));
        editDatasetDialogFragment.show(getChildFragmentManager(), "EditDatasetDialogFragment");
    }

    @Override
    public void onDeleteClicked(int position) {
        HelperSecretDb hsb = new HelperSecretDb(getContext());
        SQLUtils sqlUtils = new SQLUtils(hsb.getWritableDatabase());

        sqlUtils.deleteData(HelperSecretDb.HTBL_NAME, "name = ?", new String[]{datasetModelList.get(position).getDatasetName()});

        sqlUtils = new SQLUtils(new HelperDb(getContext()).getWritableDatabase());
        sqlUtils.dropTable(datasetModelList.get(position).getDatasetNickname());
    }
}