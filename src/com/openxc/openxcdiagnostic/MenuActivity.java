package com.openxc.openxcdiagnostic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class MenuActivity extends Activity {


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this));

        gridview.setOnItemClickListener(new OnItemClickListener() {
            @Override
        	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            	v.setBackground(getResources().getDrawable(R.drawable.graybuttonpressedbackground));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	if (item.getItemId() == R.id.Diagnostic) {
    		startActivity(new Intent(this, DiagnosticActivity.class));
    	} else if (item.getItemId() == R.id.Menu) {
    		//do nothing
    	}
    	else if (item.getItemId() == R.id.Dashboard) {
    	    startActivity(new Intent(this, DashboardActivity.class));
            return true;
    	}
    	return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.options, menu);
        return true;
    }
}
