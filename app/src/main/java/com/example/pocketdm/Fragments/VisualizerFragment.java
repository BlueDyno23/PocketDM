package com.example.pocketdm.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pocketdm.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VisualizerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VisualizerFragment extends Fragment {


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
        return inflater.inflate(R.layout.fragment_visualizer, container, false);
    }
}