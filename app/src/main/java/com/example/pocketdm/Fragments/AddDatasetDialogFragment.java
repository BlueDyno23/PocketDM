package com.example.pocketdm.Fragments;

import android.app.Dialog;
import android.content.ContentValues;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.pocketdm.R;
import com.example.pocketdm.Utilities.FileUtils;
import com.example.pocketdm.Utilities.HelperDb;
import com.example.pocketdm.Utilities.HelperSecretDb;
import com.example.pocketdm.Utilities.SQLUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;


public class AddDatasetDialogFragment extends DialogFragment implements View.OnClickListener {

    private TextInputLayout nicknameInput;
    private TextInputLayout nameInput;
    private TextInputLayout descriptionInput;
    private TextInputLayout versionInput;
    private TextInputLayout columnsInput;
    private TextInputLayout rowsInput;
    private MaterialButton addDatasetButton;
    private MaterialButton uploadDatasetButton;
    private ActivityResultLauncher<String> filePickerLauncher;

    SQLUtils sqlUtils;
    HelperDb helperDb;

    public AddDatasetDialogFragment() {
    }

    public static AddDatasetDialogFragment newInstance() {
        AddDatasetDialogFragment fragment = new AddDatasetDialogFragment();
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        helperDb = new HelperDb(getContext());

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
        columnsInput = view.findViewById(R.id.add_dataset_columns_input);
        rowsInput = view.findViewById(R.id.add_dataset_rows_input);
        addDatasetButton = view.findViewById(R.id.add_dataset_add_btn);
        uploadDatasetButton = view.findViewById(R.id.add_dataset_upload_btn);

        addDatasetButton.setOnClickListener(this);
        uploadDatasetButton.setOnClickListener(this);

        filePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (FileUtils.getFileName(getActivity(), uri) != null && FileUtils.getFileName(getActivity(), uri).toLowerCase().endsWith(".csv")) {
                        sqlUtils = new SQLUtils(helperDb.getWritableDatabase());

                        String name = nicknameInput.getEditText().getText().toString().replace(" ", "_");
                        sqlUtils.fillTable(getContext(), name, uri);

                        rowsInput.getEditText().setText(String.valueOf(sqlUtils.getRowCount(name)));
                        columnsInput.getEditText().setText(String.valueOf(sqlUtils.getColumnCount(name)));
                        nameInput.getEditText().setText(FileUtils.getFileName(getActivity(), uri).split("\\.")[0]);

                    } else {
                        Toast.makeText(getContext(), "Please select a CSV file", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addDatasetToDb(){
        HelperSecretDb hsd = new HelperSecretDb(getContext());
        SQLUtils utils = new SQLUtils(hsd.getWritableDatabase());

        ContentValues cv = new ContentValues();
        cv.put(HelperSecretDb.TBL_NAME, nameInput.getEditText().getText().toString().replace(" ", "_"));
        cv.put(HelperSecretDb.TBL_NICKNAME, nicknameInput.getEditText().getText().toString().replace(" ", "_"));
        cv.put(HelperSecretDb.TBL_DESCRIPTION, descriptionInput.getEditText().getText().toString());
        cv.put(HelperSecretDb.TBL_VERSION, versionInput.getEditText().getText().toString());
        cv.put(HelperSecretDb.TBL_COLUMNS, columnsInput.getEditText().getText().toString());
        cv.put(HelperSecretDb.TBL_ROWS, rowsInput.getEditText().getText().toString());

        utils.insertData(HelperSecretDb.HTBL_NAME, cv);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.add_dataset_add_btn) {
            // TODO verify user actually wanted to save the dataset
            addDatasetToDb();
            dismiss();
        }
        else if (v.getId() == R.id.add_dataset_upload_btn) {
            filePickerLauncher.launch("*/*");
        }
    }
}