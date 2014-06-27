package com.openxc.openxcdiagnostic.menu;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.openxc.openxcdiagnostic.R;
import com.openxc.openxcdiagnostic.dash.DashboardActivity;
import com.openxc.openxcdiagnostic.diagnostic.DiagnosticActivity;
import com.openxc.openxcdiagnostic.dash.GridManager;
import com.openxc.openxcdiagnostic.util.ActivityLauncher;

public class MenuActivity extends Activity {
	
    private static String TAG = "Menu";
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new GridImageAdapter(this, GridManager.MenuThumbIDs, GridManager.MenuButtonUnpressedImgID));
        gridview.setBackgroundColor(Color.BLACK);
        gridview.setOnItemClickListener(new OnItemClickListener() {
            @Override
        	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            	flipButtonBackground(v);
            	Intent intent = new Intent(MenuActivity.this, GrapherActivity.class);
            	Bundle info = new Bundle();
            	info.putInt("pos", position);
            	intent.putExtras(info);
            	MenuActivity.this.startActivity(intent);
            	Log.i(TAG, "Launching Grapher Activity");
            }
        });
        
    }    
    
    private void flipButtonBackground(View v) {
    	Drawable pressed = getResources().getDrawable(GridManager.MenuButtonPressedImgID);
    	Drawable unpressed = getResources().getDrawable(GridManager.MenuButtonUnpressedImgID);
    	if (GridManager.drawablesAreEqual(v.getBackground(), pressed)) {
    		v.setBackground(unpressed);
    	} else if (GridManager.drawablesAreEqual(v.getBackground(), unpressed)) {
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
        ActivityLauncher.launchActivity(this, item.getItemId());
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.options, menu);
        return true;
    }
}
