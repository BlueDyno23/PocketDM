package com.example.pocketdm.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pocketdm.R;
public class EditorFragment extends Fragment {
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editor, container, false);
        initiateViews(view);

        return view;
    }

    private void initiateViews(View view){

    }
}