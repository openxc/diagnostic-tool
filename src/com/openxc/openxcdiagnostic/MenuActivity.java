package com.openxc.openxcdiagnostic;

import com.openxc.openxcdiagnostic.resources.ResourceManager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
        gridview.setAdapter(new GridImageAdapter(this, ResourceManager.MenuThumbIDs, ResourceManager.MenuButtonUnpressedImgID));
        gridview.setBackgroundColor(Color.BLACK);
        gridview.setOnItemClickListener(new OnItemClickListener() {
            @Override
        	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            	flipButtonBackground(v);
            }
        });
    }
    
    private void flipButtonBackground(View v) {
    	Drawable pressed = getResources().getDrawable(ResourceManager.MenuButtonPressedImgID);
    	Drawable unpressed = getResources().getDrawable(ResourceManager.MenuButtonUnpressedImgID);
    	if (ResourceManager.drawablesAreEqual(v.getBackground(), pressed)) {
    		v.setBackground(unpressed);
    	} else if (ResourceManager.drawablesAreEqual(v.getBackground(), unpressed)) {
    		v.setBackground(pressed);
    	}    	
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
