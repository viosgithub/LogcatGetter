package foo.sample.LogcatGetter;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LogcatGetter extends Activity implements OnClickListener {
	private ILogcatGetterService logcatGetterService = null;
	private boolean isUserStartedService = false;
	private boolean isUserStoppedService = false;
	private LogcatGetterServiceConnection serviceConnection = new LogcatGetterServiceConnection();
	private TextView tvStatus;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        findViewById(R.id.btnStart).setOnClickListener(this);
        findViewById(R.id.btnStop).setOnClickListener(this);
        tvStatus = (TextView)findViewById(R.id.textView2);
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
			Toast.makeText(getApplicationContext(), "既に停止しています", Toast.LENGTH_SHORT).show();
		}
		if(ret == 1)
		{
			Toast.makeText(getApplicationContext(), "書き込みを停止しました", Toast.LENGTH_SHORT).show();
			tvStatus.setText("停止中");
		}
		
	}

	private boolean isServiceRunning(String className)
	{
		Log.d("debug","isServiceRunnning");
		ActivityManager am = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceInfos = 
			am.getRunningServices(Integer.MAX_VALUE);
		for(int i=0;i<serviceInfos.size();i++)
		{
			if(serviceInfos.get(i).service.getClassName().equals(className))
			{
				Log.d("debug", "serviceFound");
				return true;
			}
		}
		return false;
	}
	private void startLogcatService()
	{
		Intent intent = new Intent(this,LogcatGetterService.class);
		if(!isServiceRunning("foo.sample.LogcatGetter.LogcatGetterService"))
		{
			startService(intent);
		}
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
		}
		EditText edt = (EditText)findViewById(R.id.editText1);
		try
		{
			int ret = logcatGetterService.saveLog(edt.getText().toString());
			if(ret <0)
			{
				Toast.makeText(getApplicationContext(), "ログ記録開始に失敗しました．すでに保存を開始している可能性があります", Toast.LENGTH_LONG).show();
			}
			else
			{
				Toast.makeText(getApplicationContext(), "ログの記録を開始しました", Toast.LENGTH_SHORT).show();
				tvStatus.setText("ログ保存中");
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
	protected final void onRestart()
	{
		super.onRestart();
	}
	@Override
	protected final void onStart()
	{
		super.onStart();
		Log.d("debug","onResume");
		if(logcatGetterService == null)
		{
			startLogcatService();
		}
		
	}
	@Override
	protected final void onResume()
	{
		super.onResume();
		Log.d("debug","onResume");
		
		/*
		if(logcatGetterService == null)
		{
			startLogcatService();
		}
		*/
			try {
				if(logcatGetterService!= null && logcatGetterService.isWritting())
				{
					tvStatus.setText("ログ保存中");
				}
					else
					{
						tvStatus.setText("停止中");
					}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		if(logcatGetterService == null)
		{
						Log.d("debug","logcatGetterService is null");
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