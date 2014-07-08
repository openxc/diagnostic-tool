package com.openxc.openxcdiagnostic.diagnostic.output;

import java.util.ArrayList;

import com.openxc.openxcdiagnostic.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class TableAdapter extends ArrayAdapter<OutputRow> {
    
    private final ArrayList<OutputRow> mRows;

    public TableAdapter(Context context, ArrayList<OutputRow> rows) {
        super(context, R.layout.diagoutputrow, rows);
        mRows = rows;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return mRows.get(position).getView();
    }
}
