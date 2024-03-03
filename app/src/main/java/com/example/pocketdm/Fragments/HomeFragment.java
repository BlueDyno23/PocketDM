package com.example.pocketdm.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pocketdm.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeFragment extends Fragment implements View.OnClickListener {

    FloatingActionButton fabAddDataset;
    RecyclerView datasetItemsRecyclerView;

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
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.fab_add_dataset) {
            AddDatasetDialogFragment addDatasetDialogFragment = AddDatasetDialogFragment.newInstance();
            addDatasetDialogFragment.show(getChildFragmentManager(), "AddDatasetDialogFragment");
        }
    }
}