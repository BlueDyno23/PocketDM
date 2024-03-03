package com.example.pocketdm.Fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.pocketdm.Models.DatasetModel;
import com.example.pocketdm.R;
import com.example.pocketdm.Utilities.FileUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;


public class AddDatasetDialogFragment extends DialogFragment implements View.OnClickListener {

    private TextInputLayout nicknameInput;
    private TextInputLayout nameInput;
    private TextInputLayout descriptionInput;
    private TextInputLayout versionInput;
    private TextInputLayout datasetPathInput;
    private TextInputLayout columnsInput;
    private TextInputLayout rowsInput;
    private MaterialButton addDatasetButton;
    private MaterialButton uploadDatasetButton;
    private ActivityResultLauncher<String> filePickerLauncher;
    public AddDatasetDialogFragment() {
    }

    public static AddDatasetDialogFragment newInstance() {
        AddDatasetDialogFragment fragment = new AddDatasetDialogFragment();
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if(dialog!=null)
        {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            // Set dialog window layout parameters to match parent
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setAttributes(layoutParams);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_dataset_dialog, container, false);
        initateViews(view);
        return view;
    }

    private void initateViews(View view) {
        nicknameInput = view.findViewById(R.id.add_dataset_nickname_input);
        nameInput = view.findViewById(R.id.add_dataset_name_input);
        descriptionInput = view.findViewById(R.id.add_dataset_description_input);
        versionInput = view.findViewById(R.id.add_dataset_version_input);
        datasetPathInput = view.findViewById(R.id.add_dataset_path_input);
        columnsInput = view.findViewById(R.id.add_dataset_columns_input);
        rowsInput = view.findViewById(R.id.add_dataset_rows_input);
        addDatasetButton = view.findViewById(R.id.add_dataset_add_btn);
        uploadDatasetButton = view.findViewById(R.id.add_dataset_upload_btn);

        addDatasetButton.setOnClickListener(this);
        uploadDatasetButton.setOnClickListener(this);

        filePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (FileUtils.getFileName(getActivity(), uri) != null && FileUtils.getFileName(getActivity(), uri).toLowerCase().endsWith(".csv")) {

                    } else {
                        Toast.makeText(getContext(), "Please select a CSV file", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.add_dataset_add_btn) {
            // TODO
            dismiss();
        }
        else if (v.getId() == R.id.add_dataset_upload_btn) {

            dismiss();
        }
    }
}