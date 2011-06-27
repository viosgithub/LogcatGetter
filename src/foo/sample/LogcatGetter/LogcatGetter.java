package foo.sample.LogcatGetter;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class LogcatGetter extends Activity implements OnClickListener {
	private ILogcatGetterService logcatGetterService = null;
	private boolean isUserStartedService = false;
	private boolean isUserStoppedService = false;
	private LogcatGetterServiceConnection serviceConnection = new LogcatGetterServiceConnection();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        findViewById(R.id.btnStart).setOnClickListener(this);
        findViewById(R.id.btnStop).setOnClickListener(this);
    }
    
	@Override
	public void onClick(View v) {
		if(v == findViewById(R.id.btnStart))
		{
			saveLog();
		}
		else if(v == findViewById(R.id.btnStop))
		{
			stopSaveLog();
		}
	}
	private void stopSaveLog() {
		int ret = -1;
		try {
		ret = logcatGetterService.stopWrite();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(ret == -1)
		{
			Toast.makeText(getApplicationContext(), "Already Stop Writing", Toast.LENGTH_SHORT).show();
		}
		if(ret == 1)
		{
			Toast.makeText(getApplicationContext(), "Stop Writing", Toast.LENGTH_SHORT).show();
		}
		
	}

	private void startLogcatService()
	{
		Intent intent = new Intent(this,LogcatGetterService.class);
		startService(intent);
		bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
		Log.d("debug","start Logcat Service");
	}
	private void stopLogcatService()
	{
		try
		{
			if(isUserStoppedService)
			{
				logcatGetterService.setLogBreak();
			}
			if(logcatGetterService != null)
			{
				serviceConnection.onServiceDisconnected(null);
				unbindService(serviceConnection);
			}
		}
		catch(RemoteException e)
		{
			e.printStackTrace();
		}
	}
	private void saveLog()
	{
		Log.d("debug","push save button");
		if(logcatGetterService == null)
		{
			startLogcatService();
			return;
		}
		EditText edt = (EditText)findViewById(R.id.editText1);
		try
		{
			int ret = logcatGetterService.saveLog(edt.getText().toString());
			if(ret <0)
			{
				Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_SHORT).show();
			}
			else
			{
				Toast.makeText(getApplicationContext(), "write start", Toast.LENGTH_SHORT).show();
			}
		}
		catch(RemoteException e)
		{
			e.printStackTrace();
		}
	}
	private void updateLogDisp()
	{
	}
	@Override
	protected final void onResume()
	{
		super.onResume();
		if(logcatGetterService == null)
		{
			startLogcatService();
		}
	}
	@Override
	protected final void onDestroy()
	{
		super.onDestroy();
		stopLogcatService();
	}
	private class LogcatGetterServiceConnection implements ServiceConnection
	{

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			//logcatGetterService = ILogcatGetterService.Stub.asInterface(service);
			logcatGetterService = ILogcatGetterService.Stub.asInterface(service);
			Log.d("debug","onServiceConnected");
			try
			{
				Thread.sleep(500);
			}
			catch(InterruptedException e)
			{
				Log.d("debug","onServiceConnected,InterreupedException");
				e.printStackTrace();
			}
				LogcatGetter.this.updateLogDisp();
				if(isUserStartedService)
				{
					Toast.makeText(LogcatGetter.this, "StartedService", Toast.LENGTH_SHORT);
					isUserStartedService = false;
				}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.d("debug","onServiceDisConnected");
			logcatGetterService = null;
			if(isUserStoppedService)
			{
				Toast.makeText(LogcatGetter.this, "StopedSaving", Toast.LENGTH_SHORT);
				isUserStoppedService = false;
			}
		}
	}
}