package uva.verspeek.hearthstone.activities;


import uva.verspeek.hearthstone.models.Constants;
import uva.verspeek.hearthstone.tools.Utils;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.verspeek.hearthstone.R;
import com.shephertz.app42.gaming.multiplayer.client.WarpClient;
import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
import com.shephertz.app42.gaming.multiplayer.client.events.ConnectEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.ConnectionRequestListener;

public class MainActivity extends Activity implements ConnectionRequestListener {

	private WarpClient theClient;
	private EditText nameEditText;
    private ProgressDialog progressDialog;
    private boolean isConnected = false;
	private int selectedMonster;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		nameEditText = (EditText)findViewById(R.id.nameEditText);
		selectedMonster = -1;
		init();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if(theClient!=null && isConnected){
			theClient.disconnect();
		}
	}
	
	public void onMonsterClicked(View view){
		ImageView character1 =(ImageView) findViewById(R.id.imageView1);
		ImageView character2 =(ImageView) findViewById(R.id.imageView2);
		ImageView character3 =(ImageView) findViewById(R.id.imageView3);
		ImageView character4 =(ImageView) findViewById(R.id.imageView4);
		
		character1.setScaleType(ScaleType.CENTER);
		character2.setScaleType(ScaleType.CENTER);
		character3.setScaleType(ScaleType.CENTER);
		character4.setScaleType(ScaleType.CENTER);
		
		switch (view.getId()) {

			case R.id.imageView1:
				selectedMonster = 1;
				character1.setScaleType(ScaleType.FIT_XY);
				break;
			case R.id.imageView2:
				character2.setScaleType(ScaleType.FIT_XY);
				selectedMonster = 2;
			break;
			case R.id.imageView3:
				character3.setScaleType(ScaleType.FIT_XY);
				selectedMonster = 3;
			break;
			case R.id.imageView4:
				character4.setScaleType(ScaleType.FIT_XY);
				selectedMonster = 4;
			break;
		}
	}
	
	public void onPlayGameClicked(View view){
		if(nameEditText.getText().length()==0){
			Utils.showToastAlert(this, getApplicationContext().getString(R.string.enterName));
			return;
		}
		if(selectedMonster!=-1){
			theClient.addConnectionRequestListener(this); 
			String userName = nameEditText.getText()+"_@"+selectedMonster;
			Utils.userName = userName;
			Log.d("Name to Join ", ""+userName);
			theClient.connectWithUserName(userName);
			progressDialog =  ProgressDialog.show(this, "", "connecting to server");
		}else{
			Utils.showToastAlert(this, getApplicationContext().getString(R.string.alertSelect));
		}
	}
	
	private void init(){
		WarpClient.initialize(Constants.apiKey, Constants.secretKey);
        try {
            theClient = WarpClient.getInstance();
        } catch (Exception ex) {
        	Utils.showToastAlert(this, "Exception in Initilization");
        }
    }
	
	@Override
	public void onConnectDone(final ConnectEvent event) {
		Log.d("onConnectDone", event.getResult()+"");
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(progressDialog!=null){
					progressDialog.dismiss();
				}
			}
		});
		if(event.getResult() == WarpResponseResultCode.SUCCESS){// go to room  list 
			isConnected = true;
			Intent intent = new Intent(MainActivity.this, RoomlistActivity.class);
			startActivity(intent);
		}else{
			Utils.showToastOnUIThread(MainActivity.this, "connection failed");
		}
	}
	
	@Override
	public void onDisconnectDone(final ConnectEvent event) {
		Log.d("onDisconnectDone", event.getResult()+"");
	}

    @Override
	public void onInitUDPDone(byte arg0) {
		
	}
    
    @Override
    public void onDestroy(){
    	super.onDestroy();
    	android.os.Process.killProcess(android.os.Process.myPid());
    }
    
}
