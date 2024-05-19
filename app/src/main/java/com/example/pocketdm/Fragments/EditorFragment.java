package com.example.pocketdm.Fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.pocketdm.Activities.BaseActivity;
import com.example.pocketdm.Adapters.TableAdapter;

import com.example.pocketdm.Enums.ColumnType;
import com.example.pocketdm.R;
import com.example.pocketdm.Utilities.HelperDb;
import com.example.pocketdm.Utilities.HelperSecretDb;
import com.example.pocketdm.Utilities.SQLUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

public class EditorFragment extends Fragment implements TableAdapter.OnCellClickedListener, View.OnClickListener {

    private RecyclerView gridRecyclerView;
    private TableAdapter tableAdapter;
    private ImageButton refreshBtn;
    private MaterialButton saveBtn, discardBtn;
    private String[][] data;
    private SharedPreferences sharedPreferences;
    private HelperDb helperDb;
    private String tempTableName;

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
        sharedPreferences = getContext().getSharedPreferences("SETTINGS", Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editor, container, false);
        initiateViews(view);
        helperDb = new HelperDb(getContext());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        tempTableName = BaseActivity.datasetModel.getDatasetNickname()+"_edit";
        discardTable();
        startTableEditing();
        updateTableGrid();
    }

    private void initiateViews(View view){
        gridRecyclerView = view.findViewById(R.id.grid_recycler_view);
        refreshBtn = view.findViewById(R.id.refresh_grid_button);

        saveBtn = view.findViewById(R.id.editor_save_btn);
        saveBtn.setOnClickListener(this);
        discardBtn = view.findViewById(R.id.editor_discard_btn);
        discardBtn.setOnClickListener(this);
        refreshBtn.setOnClickListener(this);

        prepareData();

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
        builder.setTitle("Warning");
        builder.setMessage("Leaving this page will discard all changes!\n\nPlease be patient while updating large datasets, and click the manual refresh button if auto-refresh not successful.");
        builder.setPositiveButton("Ok", null);
        builder.show();
    }

    private void prepareData() {
        if (BaseActivity.datasetModel != null) {
            data = BaseActivity.datasetModel.getData(getContext());
            tableAdapter = new TableAdapter(getContext(), data, sharedPreferences.getInt("MAX_ROW_COUNT", 100) + 1, this);
            gridRecyclerView.setAdapter(tableAdapter);
            gridRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), BaseActivity.datasetModel.getColumnsCount()));
        }
    }


    @Override
    public void onCellClicked(View view, int row, int col) {
        PopupMenu popup = new PopupMenu(getContext(), view);
        MenuInflater inflater = popup.getMenuInflater();
        if(row==0)
        {
            inflater.inflate(R.menu.editor_dropmenu_column, popup.getMenu());
        }
        else {
            inflater.inflate(R.menu.editor_dropmenu_row, popup.getMenu());
        }
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.action_delete_column){
                    deleteColumn(col);
                    return true;
                }
                else if(item.getItemId() == R.id.action_delete_row){
                    deleteRow(row);
                    return true;
                }
                else if(item.getItemId() == R.id.action_filter){
                    filterColumn(col);
                    return true;
                }
                else if(item.getItemId() == R.id.action_onehotencoding){
                    oneHotEncodingColumn(col);
                    return true;
                }
                return false;
            }
        });

        popup.show();
    }
    private void deleteColumn(int col){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
        builder.setTitle("Delete column");
        builder.setMessage("Are you sure?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SQLUtils sqlUtils = new SQLUtils(helperDb.getWritableDatabase());
                sqlUtils.dropColumn(tempTableName, sqlUtils.getColumnNames(tempTableName)[col]);
                updateTableGrid();
            }
        });
        builder.setNeutralButton("Cancel", null);
        builder.show();
    }
    private void deleteRow(int row){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
        builder.setTitle("Delete row");
        builder.setMessage("Are you sure?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SQLUtils sqlUtils = new SQLUtils(helperDb.getWritableDatabase());
                sqlUtils.deleteRowByRowNum(tempTableName, row);
                updateTableGrid();

            }
        });
        builder.setNeutralButton("Cancel", null);
        builder.show();
    }

    private void updateTableGrid(){
        SQLUtils sqlUtils = new SQLUtils(helperDb.getReadableDatabase());
        data = sqlUtils.toStringArray(tempTableName);

        tableAdapter.setData(data);
        gridRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), data[0].length));
        tableAdapter.notifyDataSetChanged();
    }

    private void filterColumn(int col){
        Toast.makeText(getContext(), "current an excess feature", Toast.LENGTH_SHORT).show();
    }
    private void oneHotEncodingColumn(int col) {
        SQLUtils sqlUtils = new SQLUtils(helperDb.getReadableDatabase());
        String columnName = sqlUtils.getColumnNames(tempTableName)[col];

        ColumnType columnType = sqlUtils.getColumnType(tempTableName, columnName);
        if (columnType != ColumnType.NUMERIC && columnType != ColumnType.TEXT) {
            ArrayList<String> values = new ArrayList<>();
            Cursor cursor = helperDb.getReadableDatabase().query(tempTableName, new String[]{columnName}, null, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    values.add(cursor.getString(cursor.getColumnIndex(columnName)));
                } while (cursor.moveToNext());
                cursor.close();
            }

            String[] uniqueValues = sqlUtils.findUniqueValues(tempTableName, columnName);
            for (String v : uniqueValues) {
                sqlUtils.addColumn(tempTableName, columnName + "_" + v, "INTEGER");
            }

            helperDb.getWritableDatabase().beginTransaction();
            try {
                for (int i = 0; i < values.size(); i++) {
                    String currentValue = values.get(i);
                    ContentValues cv = new ContentValues();
                    for (String u : uniqueValues) {
                        cv.put(columnName + "_" + u, currentValue.equals(u) ? 1 : 0);
                    }
                    helperDb.getWritableDatabase().update(tempTableName, cv, "ROWID = ?", new String[]{String.valueOf(i + 1)});
                }
                helperDb.getWritableDatabase().setTransactionSuccessful();
            } finally {
                helperDb.getWritableDatabase().endTransaction();
            }

            sqlUtils.dropColumn(tempTableName, columnName);
            updateTableGrid();
        } else {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
            builder.setTitle("Invalid Column for One Hot Encoding");
            builder.setMessage("Selected column cannot be one-hot-encoded\n(too many unique values or unsupported type)");
            builder.setPositiveButton("Ok", null);
            builder.show();
        }
    }


    private void startTableEditing(){
        tempTableName = BaseActivity.datasetModel.getDatasetNickname()+"_edit";
        SQLUtils sqlUtils = new SQLUtils(helperDb.getReadableDatabase());
        sqlUtils.duplicateTable(BaseActivity.datasetModel.getDatasetNickname(), tempTableName);
    }

    private void saveTable(){
        SQLUtils sqlUtils = new SQLUtils(helperDb.getWritableDatabase());
        sqlUtils.dropTable(BaseActivity.datasetModel.getDatasetNickname());


        HelperSecretDb helperSecretDb = new HelperSecretDb(getContext());
        helperSecretDb.getWritableDatabase().execSQL("UPDATE "+HelperSecretDb.HTBL_NAME+ " SET columns="+sqlUtils.getColumnCount(tempTableName)+", rows="+sqlUtils.getRowCount(tempTableName)+" WHERE nickname='"+BaseActivity.datasetModel.getDatasetNickname()+"'");

        sqlUtils.renameTable(tempTableName, BaseActivity.datasetModel.getDatasetNickname());

        startTableEditing();
    }

    private void discardTable(){
        SQLUtils sqlUtils = new SQLUtils(helperDb.getWritableDatabase());
        sqlUtils.dropTable(tempTableName);
        data = BaseActivity.datasetModel.getData(getContext());
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        discardTable();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.refresh_grid_button){
            updateTableGrid();
        }
        else if(v.getId() == R.id.editor_save_btn){
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
            builder.setTitle("Save");
            builder.setMessage("Are you sure you want to save all edits?\n\nOriginal table will be discarded.");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    saveTable();
                }
            });
            builder.setNeutralButton("Cancel", null);
            builder.show();
        }
        else if(v.getId() == R.id.editor_discard_btn){
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
            builder.setTitle("Discard");
            builder.setMessage("Are you sure you want to discard all edits?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    discardTable();
                    data = BaseActivity.datasetModel.getData(getContext());
                    tableAdapter.setData(data);
                    gridRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), data[0].length));
                    tableAdapter.notifyDataSetChanged();
                    startTableEditing();
                }
            });
            builder.setNeutralButton("Cancel", null);
            builder.show();
        }
    }
}