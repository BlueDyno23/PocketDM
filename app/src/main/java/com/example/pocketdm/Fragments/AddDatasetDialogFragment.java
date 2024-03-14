package com.example.pocketdm.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
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
import com.example.pocketdm.Utilities.SqlUtils;
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
    private SqlUtils sqlutils;

    private Uri selectedDatasetUri;
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

        sqlutils = SqlUtils.getInstance(getContext());

        filePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (FileUtils.getFileName(getActivity(), uri) != null && FileUtils.getFileName(getActivity(), uri).toLowerCase().endsWith(".csv")) {
                        sqlutils.saveDatasetToSqlDb(getContext(), uri, String.valueOf(nicknameInput.getEditText().getText()));
                        fillFields(FileUtils.getFileName(getContext(), uri));
                        selectedDatasetUri = uri;
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
            if(checkFieldsAreFilled()){

                sqlutils.saveDatasetToSqlDb(getContext(), selectedDatasetUri, String.valueOf(nicknameInput.getEditText().getText()));
                dismiss();
            }
        }
        else if (v.getId() == R.id.add_dataset_upload_btn) {
            filePickerLauncher.launch("*/*");
        }
    }

    private void fillFields(String datasetName){
        Cursor cursor = sqlutils.getReadableDatabase().query(datasetName, null, null, null, null, null, null);

        datasetPathInput.getEditText().setText(selectedDatasetUri.toString());
        nameInput.getEditText().setText(FileUtils.getFileName(getContext(), selectedDatasetUri));
        columnsInput.getEditText().setText(cursor.getColumnCount());
        rowsInput.getEditText().setText(cursor.getCount());

    }

    private boolean checkFieldsAreFilled() {
        if(nicknameInput.getEditText().getText().toString().isEmpty() ||
                nameInput.getEditText().getText().toString().isEmpty() ||
                descriptionInput.getEditText().getText().toString().isEmpty() ||
                versionInput.getEditText().getText().toString().isEmpty() ||
                datasetPathInput.getEditText().getText().toString().isEmpty() ||
                columnsInput.getEditText().getText().toString().isEmpty() ||
                rowsInput.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);

        sqlutils.deleteDatasetFromSqlDb(String.valueOf(nicknameInput.getEditText().getText()));
    }
}