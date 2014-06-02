package com.openxc.openxcdiagnostic.diagnostic;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.openxc.VehicleManager;
import com.openxc.openxcdiagnostic.R;
import com.openxc.openxcdiagnostic.dash.DashboardActivity;
import com.openxc.openxcdiagnostic.menu.MenuActivity;

public class DiagnosticActivity extends Activity {

    private static String TAG = "VehicleDashboard";

    private VehicleManager mVehicleManager;
    private boolean mIsBound;
    private Button sendRequestButton;
    private Button clearButton;
    private List<EditText> textFields = new ArrayList<EditText>();

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            Log.i(TAG, "Bound to VehicleManager");
            mVehicleManager = ((VehicleManager.VehicleBinder)service
                    ).getService();
            mIsBound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            Log.w(TAG, "VehicleService disconnected unexpectedly");
            mVehicleManager = null;
            mIsBound = false;
        }
    };
    
    private void initButtons() {
        sendRequestButton = (Button) findViewById(
                R.id.sendRequestButton);
        sendRequestButton.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View v) {
        	}
        });
        
        clearButton = (Button) findViewById(R.id.clearButton);
        clearButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i=0; i < textFields.size(); i++) {
                    textFields.get(i).setText("");
                }
            }
        });
    }
    
    private void setKeyboardGoneDefault() {
    	getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
    
    private void initTextFields() {
    	
    	final EditText busInputText = (EditText) findViewById(R.id.busInput);
    	textFields.add(busInputText);
    	busInputText.setOnEditorActionListener(new OnEditorActionListener() {
    		@Override
    		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
    			if (actionId == EditorInfo.IME_ACTION_DONE) {
    				busInputText.setCursorVisible(false);
    			}
    			return false;
    		}
    	});
    	
    	final EditText idInputText = (EditText) findViewById(R.id.idInput);
        textFields.add(idInputText);
    	idInputText.setOnEditorActionListener(new OnEditorActionListener() {
    		@Override
    		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
    			if (actionId == EditorInfo.IME_ACTION_DONE) {
    				idInputText.setCursorVisible(false);
    			}
    			return false;
    		}
    	});
    	
    	final EditText modeInputText = (EditText) findViewById(R.id.modeInput);
        textFields.add(modeInputText);
    	modeInputText.setOnEditorActionListener(new OnEditorActionListener() {
    		@Override
    		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
    			if (actionId == EditorInfo.IME_ACTION_DONE) {
    				modeInputText.setCursorVisible(false);
    			}
    			return false;
    		}
    	});
    	
    	final EditText pidInputText = (EditText) findViewById(R.id.pidInput);
        textFields.add(pidInputText);
    	pidInputText.setOnEditorActionListener(new OnEditorActionListener() {
    		@Override
    		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
    			if (actionId == EditorInfo.IME_ACTION_DONE) {
    				pidInputText.setCursorVisible(false);
    			}
    			return false;
    		}
    	});
    	
    	final EditText payloadInputText = (EditText) findViewById(R.id.payloadInput);
        textFields.add(payloadInputText);
    	payloadInputText.setOnEditorActionListener(new OnEditorActionListener() {
    		@Override
    		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
    			if (actionId == EditorInfo.IME_ACTION_DONE) {
    				payloadInputText.setCursorVisible(false);
    			}
    			return false;
    		}
    	});
    	
    	final EditText factorInputText = (EditText) findViewById(R.id.factorInput);
        textFields.add(factorInputText);
    	factorInputText.setOnEditorActionListener(new OnEditorActionListener() {
    		@Override
    		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
    			if (actionId == EditorInfo.IME_ACTION_DONE) {
    				factorInputText.setCursorVisible(false);
    			}
    			return false;
    		}
    	});
    	
    	final EditText offsetInputText = (EditText) findViewById(R.id.offsetInput);
        textFields.add(offsetInputText);
    	offsetInputText.setOnEditorActionListener(new OnEditorActionListener() {
    		@Override
    		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
    			if (actionId == EditorInfo.IME_ACTION_DONE) {
    				offsetInputText.setCursorVisible(false);
    			}
    			return false;
    		}
    	});
    	
    	final EditText nameInputText = (EditText) findViewById(R.id.nameInput);
        textFields.add(nameInputText);
    	nameInputText.setOnEditorActionListener(new OnEditorActionListener() {
    		@Override
    		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
    			if (actionId == EditorInfo.IME_ACTION_DONE) {
    				nameInputText.setCursorVisible(false);
    			}
    			return false;
    		}
    	});	
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diagnostic);
        Log.i(TAG, "Vehicle dashboard created");

        setKeyboardGoneDefault();
        initButtons();
        initTextFields();
    }

    @Override
    public void onResume() {
        super.onResume();
        bindService(new Intent(this, VehicleManager.class),
                mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mIsBound) {
            Log.i(TAG, "Unbinding from vehicle service");
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.Diagnostic) {
               //do nothing
               startActivity(new Intent(this, DiagnosticActivity.class));
        } else if (item.getItemId() == R.id.Menu) {
             startActivity(new Intent(this, MenuActivity.class));
             return true;
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
