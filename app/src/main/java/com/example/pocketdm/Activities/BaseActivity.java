package com.example.pocketdm.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.example.pocketdm.Fragments.EditorFragment;
import com.example.pocketdm.Fragments.HomeFragment;
import com.example.pocketdm.Fragments.PredictorFragment;
import com.example.pocketdm.Fragments.SettingsFragment;
import com.example.pocketdm.Fragments.VisualizerFragment;
import com.example.pocketdm.Models.DatasetModel;
import com.example.pocketdm.R;
import com.example.pocketdm.Utilities.SqlUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class BaseActivity extends AppCompatActivity {

    private FrameLayout fragmentsContainer;
    private BottomNavigationView bottomNavMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        initFillDatasetModelList();
        initViews();
        initNavigation();
    }

    private void initFillDatasetModelList() {

    }

    private void initViews()
    {
        fragmentsContainer = findViewById(R.id.fragments_container);
        bottomNavMenu = findViewById(R.id.bottom_nav_menu);
    }
    private void initNavigation() {
        bottomNavMenu.setOnItemSelectedListener(item -> {
            Fragment fragment;
            if (item.getItemId() == R.id.bottom_nav_editor) {
                fragment = EditorFragment.newInstance();
            } else if (item.getItemId() == R.id.bottom_nav_visualizer) {
                fragment = VisualizerFragment.newInstance();
            } else if (item.getItemId() == R.id.bottom_nav_home) {
                fragment = HomeFragment.newInstance();
            } else if (item.getItemId() == R.id.bottom_nav_predictor) {
                fragment = PredictorFragment.newInstance();
            } else if (item.getItemId() == R.id.bottom_nav_settings) {
                fragment = SettingsFragment.newInstance();
            } else {
                fragment = new HomeFragment();
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(fragmentsContainer.getId(), fragment)
                    .commit();
            return true;
        });
        bottomNavMenu.setSelectedItemId(R.id.bottom_nav_home);
        getSupportFragmentManager().beginTransaction()
                .replace(fragmentsContainer.getId(), new HomeFragment())
                .commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}