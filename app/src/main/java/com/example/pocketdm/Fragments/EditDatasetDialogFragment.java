package com.example.pocketdm.Fragments;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.example.pocketdm.Models.DatasetModel;
import com.example.pocketdm.R;
import com.example.pocketdm.Utilities.HelperSecretDb;
import com.example.pocketdm.Utilities.SQLUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

public class EditDatasetDialogFragment extends DialogFragment implements DialogInterface.OnCancelListener, DialogInterface.OnDismissListener {

    private MaterialButton editDatasetButton;
    private TextInputLayout nicknameInput, descriptionInput, versionInput;

    private static DatasetModel _datasetModel;
    public EditDatasetDialogFragment() {

    }
    public static EditDatasetDialogFragment newInstance(DatasetModel datasetModel) {
        EditDatasetDialogFragment fragment = new EditDatasetDialogFragment();
        _datasetModel = datasetModel;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_dataset_dialog, container, false);

        initViews(view);
        fillViews();
        return view;
    }

    private void fillViews() {
        nicknameInput.getEditText().setText(_datasetModel.getDatasetNickname());
        descriptionInput.getEditText().setText(_datasetModel.getDatasetDescription());
        versionInput.getEditText().setText(String.valueOf(_datasetModel.getDatasetVersion()));
    }

    private void initViews(View view) {
        nicknameInput = view.findViewById(R.id.edit_dataset_nickname_input);
        descriptionInput = view.findViewById(R.id.edit_dataset_description_input);
        versionInput = view.findViewById(R.id.edit_dataset_version_input);
        editDatasetButton = view.findViewById(R.id.home_edit_dataset_btn);
        editDatasetButton.setOnClickListener(v -> {
            HelperSecretDb hsd = new HelperSecretDb(getContext());
            SQLUtils sqlUtils = new SQLUtils(hsd.getWritableDatabase());
            ContentValues cv = new ContentValues();

            cv.put(HelperSecretDb.TBL_NICKNAME, nicknameInput.getEditText().getText().toString().replace(" ", "_"));
            cv.put(HelperSecretDb.TBL_DESCRIPTION, descriptionInput.getEditText().getText().toString());
            cv.put(HelperSecretDb.TBL_VERSION, versionInput.getEditText().getText().toString());
            sqlUtils.updateData(HelperSecretDb.HTBL_NAME, cv, "nickname = ?", new String[]{_datasetModel.getDatasetNickname()});
            dismiss();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if(dialog!=null)
        {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setAttributes(layoutParams);
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {

    }

    @Override
    public void onDismiss(DialogInterface dialog) {

    }
}