package foo.sample.LogcatGetter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class LogcatGetterService extends Service{
	
	private static final int LOGCAT_SAVE_MAXLINES = 30000;
	private static final int LOGCAT_DISP_MAXLINES = 1000;
	
	private static final int LOGWRITE_ALREADY_WRITE = -1;
	private static final int LOGWRITE_OK = 1;
	
	public boolean isFileWrite = false;
	public boolean isTryWrite = false;
	public String writeFileName = "none";

	private IBinder mBinder = new LogcatGetterServiceBinder();
	private List<String> mList = new ArrayList<String>();
	
	private BufferedWriter bw = null;
	
	
	private Thread mThread = null;
	private Object mObjThreadBread = new Object();
	private boolean mThreadBreak = false;
	
	@Override
	public void onCreate()
	{
		super.onCreate();
	}
	public boolean isWritting()
	{
		return isFileWrite;
	}
	public void onStart(Intent intent,int startId)
	{
		super.onStart(intent, startId);
		Log.d("debug", "LogcatGetterService:onStart");
		if(mThread == null)
		{
			mThread = new Thread(new Runnable() {
				public void run() {
					Process proc = null;
					BufferedReader reader = null;
					synchronized(mObjThreadBread)
					{
						mThreadBreak = false;
					}
					try
					{
						proc = Runtime.getRuntime().exec(new String[] {"logcat","-v","long"});
						reader = new BufferedReader(new InputStreamReader(proc.getInputStream()),4056);
						String line;
						while(true)
						{
							synchronized(mObjThreadBread)
							{
								if(mThreadBreak)
								{
									break;
								}
							}
							if (isTryWrite)
							{
								isTryWrite = false;
								bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("/sdcard/" + writeFileName),"UTF-8"));
								isFileWrite = true;
							}
							
							line = reader.readLine();
							if(line.length() == 0)
							{
								try
								{
									if(isFileWrite)
									{
									bw.flush();
									}
									Thread.sleep(200);
								}
								catch(InterruptedException e)
								{
								}
								continue;
							}
							if(isFileWrite)
							{
								bw.append(line);
								bw.newLine();
							}
							synchronized(mList)
							{
								if(mList.size() >= LOGCAT_SAVE_MAXLINES)
								{
									mList.remove(0);
								}
								mList.add(line);
							}
						}
					}
					catch(IOException e)
					{
						e.printStackTrace();
					}
					finally
					{
						if(reader != null)
						{
							try
							{
								reader.close();
							}
							catch(IOException e)
							{
								e.printStackTrace();
							}
						}
						mThread = null;
					}
					
				}
			});
			mThread.start();
		}
	}
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		isTryWrite = isFileWrite = false;
		if(bw != null)
		{
			try {
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}
	private class LogcatGetterServiceBinder extends ILogcatGetterService.Stub
	{
		public List<String> getDispData()
		{
			synchronized(mList)
			{
				int listsize = mList.size();
				if(listsize < LOGCAT_DISP_MAXLINES)
				{
					return new ArrayList<String>(mList);
				}
				else
				{
					int start = listsize - LOGCAT_DISP_MAXLINES;
					return new ArrayList<String>(mList.subList(start, listsize));
				}
			}
		}
		public int saveLog(String FileName)
		{
			Log.d("debug","LogcatGetterService:saveLog");
			writeFileName = FileName;
			if(isFileWrite)
			{
				return LOGWRITE_ALREADY_WRITE; 
			}
			
			isTryWrite = true;
			return LOGWRITE_OK;
		}
		public int stopWrite()
		{
			Log.d("debug","LogcatGetterService:stopWrite");
			if(!isFileWrite && !isTryWrite)
			{
				return -1;
			}
			isFileWrite = false;
			isTryWrite = false;
			try {
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return 1;
			
		}
		public void setLogBreak()
		{
			synchronized(mObjThreadBread)
			{
				mThreadBreak = true;
			}
		}
		
	}

}
