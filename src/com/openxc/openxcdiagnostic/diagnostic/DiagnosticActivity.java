package com.openxc.openxcdiagnostic.diagnostic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
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
import com.openxc.messages.DiagnosticRequest;
import com.openxc.messages.DiagnosticResponse;
import com.openxc.openxcdiagnostic.R;
import com.openxc.openxcdiagnostic.dash.DashboardActivity;
import com.openxc.openxcdiagnostic.menu.MenuActivity;

public class DiagnosticActivity extends Activity {

    private static String TAG = "VehicleDashboard";

    private VehicleManager mVehicleManager;
    private boolean mIsBound;
    private final Handler mHandler = new Handler();
    private Button sendRequestButton;
    private Button clearButton;
    private EditText mBusInputText;
    private EditText mIdInputText;
    private EditText mModeInputText;
    private EditText mPidInputText;
    private EditText mPayloadInputText;
    private EditText mFactorInputText;
    private EditText mOffsetInputText;
    private EditText mNameInputText;
    private List<EditText> textFields = new ArrayList<EditText>();
    
    DiagnosticResponse.Listener mResponseListener =
            new DiagnosticResponse.Listener() {
        public void receive(DiagnosticResponse response) {
            mHandler.post(new Runnable() {
                public void run() {
                    
                }
            });
        }
    };

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
    
    public Map<String, Object> generateMapFromTextFields() {
        Map<String, Object> map = new HashMap<>();
        map.put(DiagnosticRequest.BUS_KEY, Integer.valueOf(mBusInputText.getText().toString()));
        map.put(DiagnosticRequest.ID_KEY, Integer.valueOf(mIdInputText.getText().toString()));
        map.put(DiagnosticRequest.MODE_KEY, Integer.valueOf(mModeInputText.getText().toString()));
        map.put(DiagnosticRequest.PID_KEY,  Integer.valueOf(mPidInputText.getText().toString()));
        map.put(DiagnosticRequest.PAYLOAD_KEY, mPayloadInputText.getText().toString().getBytes());
        map.put(DiagnosticRequest.FACTOR_KEY, Float.valueOf(mFactorInputText.getText().toString()));
        map.put(DiagnosticRequest.OFFSET_KEY, Float.valueOf(mOffsetInputText.getText().toString()));
        map.put(DiagnosticRequest.NAME_KEY, mNameInputText.getText().toString());
        return map;
    }
    
    private void initButtons() {
        sendRequestButton = (Button) findViewById(
                R.id.sendRequestButton);
        sendRequestButton.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View v) {
        	    mVehicleManager.request(new DiagnosticRequest(generateMapFromTextFields()));
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
    	
    	mBusInputText = (EditText) findViewById(R.id.busInput);
    	textFields.add(mBusInputText);
    	mBusInputText.setOnEditorActionListener(new OnEditorActionListener() {
    		@Override
    		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
    			if (actionId == EditorInfo.IME_ACTION_DONE) {
    			    mBusInputText.setCursorVisible(false);
    			}
    			return false;
    		}
    	});
    	
    	mIdInputText = (EditText) findViewById(R.id.idInput);
        textFields.add(mIdInputText);
        mIdInputText.setOnEditorActionListener(new OnEditorActionListener() {
    		@Override
    		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
    			if (actionId == EditorInfo.IME_ACTION_DONE) {
    			    mIdInputText.setCursorVisible(false);
    			}
    			return false;
    		}
    	});
    	
    	mModeInputText = (EditText) findViewById(R.id.modeInput);
        textFields.add(mModeInputText);
        mModeInputText.setOnEditorActionListener(new OnEditorActionListener() {
    		@Override
    		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
    			if (actionId == EditorInfo.IME_ACTION_DONE) {
    			    mModeInputText.setCursorVisible(false);
    			}
    			return false;
    		}
    	});
    	
    	mPidInputText = (EditText) findViewById(R.id.pidInput);
        textFields.add(mPidInputText);
        mPidInputText.setOnEditorActionListener(new OnEditorActionListener() {
    		@Override
    		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
    			if (actionId == EditorInfo.IME_ACTION_DONE) {
    			    mPidInputText.setCursorVisible(false);
    			}
    			return false;
    		}
    	});
    	
    	mPayloadInputText = (EditText) findViewById(R.id.payloadInput);
        textFields.add(mPayloadInputText);
        mPayloadInputText.setOnEditorActionListener(new OnEditorActionListener() {
    		@Override
    		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
    			if (actionId == EditorInfo.IME_ACTION_DONE) {
    			    mPayloadInputText.setCursorVisible(false);
    			}
    			return false;
    		}
    	});
    	
    	mFactorInputText = (EditText) findViewById(R.id.factorInput);
        textFields.add(mFactorInputText);
        mFactorInputText.setOnEditorActionListener(new OnEditorActionListener() {
    		@Override
    		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
    			if (actionId == EditorInfo.IME_ACTION_DONE) {
    			    mFactorInputText.setCursorVisible(false);
    			}
    			return false;
    		}
    	});
    	
    	mOffsetInputText = (EditText) findViewById(R.id.offsetInput);
        textFields.add(mOffsetInputText);
        mOffsetInputText.setOnEditorActionListener(new OnEditorActionListener() {
    		@Override
    		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
    			if (actionId == EditorInfo.IME_ACTION_DONE) {
    			    mOffsetInputText.setCursorVisible(false);
    			}
    			return false;
    		}
    	});
    	
    	mNameInputText = (EditText) findViewById(R.id.nameInput);
        textFields.add(mNameInputText);
        mNameInputText.setOnEditorActionListener(new OnEditorActionListener() {
    		@Override
    		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
    			if (actionId == EditorInfo.IME_ACTION_DONE) {
    			    mNameInputText.setCursorVisible(false);
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
