package com.example.pocketdm.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.pocketdm.Fragments.EditorFragment;
import com.example.pocketdm.Fragments.HomeFragment;
import com.example.pocketdm.Fragments.PredictorFragment;
import com.example.pocketdm.Fragments.SettingsFragment;
import com.example.pocketdm.Fragments.VisualizerFragment;
import com.example.pocketdm.Models.DatasetModel;
import com.example.pocketdm.R;
import com.example.pocketdm.Utilities.HelperDb;
import com.example.pocketdm.Utilities.HelperSecretDb;
import com.example.pocketdm.Utilities.SQLUtils;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BaseActivity extends AppCompatActivity {

    private FrameLayout fragmentsContainer;
    private BottomNavigationView bottomNavMenu;
    private MaterialToolbar topAppBar;

    public static DatasetModel datasetModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        initViews();
        initNavigation();

        HelperDb helperDb = new HelperDb(getApplicationContext());
        SQLUtils sqlUtils = new SQLUtils(helperDb.getWritableDatabase());
        sqlUtils.discardAllTemps();
    }

    private void initViews()
    {
        fragmentsContainer = findViewById(R.id.fragments_container);
        bottomNavMenu = findViewById(R.id.bottom_nav_menu);
        topAppBar = findViewById(R.id.topAppBar);
    }
    private void initNavigation() {
        bottomNavMenu.setOnItemSelectedListener(item -> {
            Fragment fragment;
            if(datasetModel != null)
            {
                if (item.getItemId() == R.id.bottom_nav_editor) {
                    fragment = EditorFragment.newInstance();
                    topAppBar.setTitle("Editor");
                    topAppBar.setTitle(topAppBar.getTitle()+"     "+datasetModel.getDatasetNickname());
                } else if (item.getItemId() == R.id.bottom_nav_visualizer) {
                    fragment = VisualizerFragment.newInstance();
                    topAppBar.setTitle("Visualizer");
                    topAppBar.setTitle(topAppBar.getTitle()+"     "+datasetModel.getDatasetNickname());
                } else if (item.getItemId() == R.id.bottom_nav_home) {
                    fragment = HomeFragment.newInstance();
                    topAppBar.setTitle("Home");
                    topAppBar.setTitle(topAppBar.getTitle()+"     "+datasetModel.getDatasetNickname());
                } else if (item.getItemId() == R.id.bottom_nav_predictor) {
                    fragment = PredictorFragment.newInstance();
                    topAppBar.setTitle("Predictor");
                    topAppBar.setTitle(topAppBar.getTitle()+"     "+datasetModel.getDatasetNickname());
                } else if (item.getItemId() == R.id.bottom_nav_settings) {
                    fragment = SettingsFragment.newInstance();
                    topAppBar.setTitle("Settings");
                    topAppBar.setTitle(topAppBar.getTitle()+"     "+datasetModel.getDatasetNickname());
                } else {
                    fragment = new HomeFragment();
                    topAppBar.setTitle("Home");
                    topAppBar.setTitle(topAppBar.getTitle()+"     "+datasetModel.getDatasetNickname());
                }
            }
            else {
                disableNavigation();
                fragment = new HomeFragment();
                topAppBar.setTitle("Home");
                disableNavigation();
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

    public void openDataset(String datasetNickname) {
        HelperSecretDb hdb = new HelperSecretDb(this);
        SQLUtils sqlUtils = new SQLUtils(hdb.getReadableDatabase());
        datasetModel = sqlUtils.getDatasetModel(datasetNickname);
        if (datasetModel == null) {
            Toast.makeText(this, "Dataset not found", Toast.LENGTH_SHORT).show();
        } else {
            topAppBar.setTitle(topAppBar.getTitle()+"     "+datasetModel.getDatasetNickname());
        }

        enableNavigation();
    }

    public void disableNavigation(){
        bottomNavMenu.getMenu().findItem(R.id.bottom_nav_editor).setEnabled(false);
        bottomNavMenu.getMenu().findItem(R.id.bottom_nav_visualizer).setEnabled(false);
        bottomNavMenu.getMenu().findItem(R.id.bottom_nav_home).setEnabled(false);
        bottomNavMenu.getMenu().findItem(R.id.bottom_nav_predictor).setEnabled(false);
        bottomNavMenu.getMenu().findItem(R.id.bottom_nav_settings).setEnabled(false);
    }

    public void enableNavigation(){
        bottomNavMenu.getMenu().findItem(R.id.bottom_nav_editor).setEnabled(true);
        bottomNavMenu.getMenu().findItem(R.id.bottom_nav_visualizer).setEnabled(true);
        bottomNavMenu.getMenu().findItem(R.id.bottom_nav_home).setEnabled(true);
        bottomNavMenu.getMenu().findItem(R.id.bottom_nav_predictor).setEnabled(true);
        bottomNavMenu.getMenu().findItem(R.id.bottom_nav_settings).setEnabled(true);
    }
}