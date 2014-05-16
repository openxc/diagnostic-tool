package com.openxc.openxcdiagnostic;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
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
import android.widget.LinearLayout;

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
                GraphViewSeries exampleSeries = new GraphViewSeries(new GraphViewData[] {
                        new GraphViewData(1, 2.0d)
                        , new GraphViewData(2, 1.5d)
                        , new GraphViewData(3, 2.5d)
                        , new GraphViewData(4, 1.0d)
                    });
                     
                    GraphView graphView = new LineGraphView(
                        MenuActivity.this // context
                        , "GraphViewDemo" // heading
                    );
                    graphView.addSeries(exampleSeries); // data
                     
                    LinearLayout layout = (LinearLayout) findViewById(R.id.graph);
                    layout.addView(graphView);
                    MenuActivity.this.setContentView(R.layout.graph);
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
