package foo.sample.LogcatGetter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class LogcatGetterService extends Service {

    private static final int LOGCAT_SAVE_MAXLINES = 30000;
    private static final int LOGCAT_DISP_MAXLINES = 1000;

    private static final int LOGWRITE_ALREADY_WRITE = -1;
    private static final int LOGWRITE_OK = 1;

    public static boolean isFileWrite = false;
    public static boolean isTryWrite = false;
    public static String writeFileName = "none";

    private IBinder mBinder = new LogcatGetterServiceBinder();
    private List<String> mList = new ArrayList<String>();

    private BufferedWriter bwMain = null;
    private BufferedWriter bwEvents = null;
    private BufferedWriter bwRadio = null;

    private Thread mThread = null;
    private Object mObjThreadBread = new Object();
    private boolean mThreadBreak = false;

    @Override
        public void onCreate() {
            super.onCreate();
        }

    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.d("debug", "LogcatGetterService:onStart");
        if (mThread == null) {
            mThread = new Thread(new Runnable() {
                public void run() {
                    Process procMain,procEvents,procRadio;
                    BufferedReader readerMain = null, readerEvents = null, readerRadio = null;
                    synchronized (mObjThreadBread) {
                        mThreadBreak = false;
                    }
                    try {
                        procMain = Runtime.getRuntime().exec(
                            new String[] { "logcat", "-v", "long" });
                        procEvents = Runtime.getRuntime().exec(
                            new String[] { "logcat","-b","events", "-v", "long" });
                        procRadio = Runtime.getRuntime().exec(
                            new String[] { "logcat","-b","radio", "-v", "long" });
                        readerMain = new BufferedReader(new InputStreamReader(procMain
                                .getInputStream()), 4056);
                        readerEvents = new BufferedReader(new InputStreamReader(procEvents
                                .getInputStream()), 4056);
                        readerRadio = new BufferedReader(new InputStreamReader(procRadio
                                .getInputStream()), 4056);
                        String lineMain,lineEvents,lineRadio;
                        while (true) {
                            synchronized (mObjThreadBread) {
                                if (mThreadBreak) {
                                    break;
                                }
                            }
                            if (isTryWrite) {
                                isTryWrite = false;
                                bwMain = new BufferedWriter(
                                        new OutputStreamWriter(
                                            new FileOutputStream(
                                                Environment
                                                .getExternalStorageDirectory()
                                                + "/"
                                                + writeFileName
                                                + "main.txt"),
                                            "UTF-8"));
                                bwEvents = new BufferedWriter(
                                        new OutputStreamWriter(
                                            new FileOutputStream(
                                                Environment
                                                .getExternalStorageDirectory()
                                                + "/"
                                                + writeFileName
                                                + "radio.txt"),
                                            "UTF-8"));
                                bwRadio = new BufferedWriter(
                                        new OutputStreamWriter(
                                            new FileOutputStream(
                                                Environment
                                                .getExternalStorageDirectory()
                                                + "/"
                                                + writeFileName
                                                + "events.txt"),
                                            "UTF-8"));
                                isFileWrite = true;
                            }

                            lineMain = readerMain.readLine();
                            lineEvents = readerEvents.readLine();
                            lineRadio = readerRadio.readLine();
                            if(isFileWrite)
                            {
                                if(lineMain.length() != 0)
                                {
                                    bwMain.append(lineMain);
                                    bwMain.newLine();
                                }
                                if(lineEvents.length() != 0)
                                {
                                    bwEvents.append(lineEvents);
                                    bwEvents.newLine();
                                }
                                if(lineRadio.length() != 0)
                                {
                                    bwRadio.append(lineRadio);
                                    bwRadio.newLine();
                                }
                            }
                            if (lineMain.length() == 0 && lineEvents.length() == 0 && lineRadio.length() == 0) {
                                try {
									Thread.sleep(500);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
                                continue;
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (readerMain != null) {
                            try {
                                readerMain.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if(readerEvents != null)
                        {
                        	try {
								readerEvents.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
                        }
                        if(readerRadio != null)
                        {
                        	try {
								readerRadio.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
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
    private void closeFiles()
    {
        try {
            if (bwMain != null) {
                bwMain.close();
            }
            if (bwEvents != null) {
                bwEvents.close();
            }
            if (bwRadio != null) {
                bwRadio.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public boolean onUnbind(Intent intent)
    {
    	super.onUnbind(intent);
    	Log.d("debug","onUbind");
    	Log.d("debug","isFileWrite:"+isFileWrite);
    	return true;
    }
    @Override
    public void onRebind(Intent intent)
    {
    	super.onRebind(intent);
    	Log.d("debug","Rebind");
    	Log.d("debug","isFileWrite:"+isFileWrite);
    }
    @Override
        public void onDestroy() {
            super.onDestroy();
            if(isFileWrite)
            {
            NotificationManager nm = 
            	(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            Notification n = new Notification();
            n.icon = R.drawable.icon;
            n.tickerText = "LogGetterサービスの終了";
            n.setLatestEventInfo(getApplicationContext(), "LogcatGetter", "なんとなくログの記録を終了しました", contentIntent());
            nm.notify(1,n);
            }
            closeFiles();
        }
    
    private PendingIntent contentIntent()
    {
    	Intent intent = 
    		new Intent(getApplicationContext(),LogcatGetter.class);
    	PendingIntent pi = 
    		PendingIntent.getActivity(this, 0, intent, 0);
    	return pi;
    }

    @Override
        public IBinder onBind(Intent arg0) {
            return mBinder;
        }

    private class LogcatGetterServiceBinder extends ILogcatGetterService.Stub {
        public List<String> getDispData() {
            synchronized (mList) {
                int listsize = mList.size();
                if (listsize < LOGCAT_DISP_MAXLINES) {
                    return new ArrayList<String>(mList);
                } else {
                    int start = listsize - LOGCAT_DISP_MAXLINES;
                    return new ArrayList<String>(mList.subList(start, listsize));
                }
            }
        }

        public int saveLog(String FileName) {
            Log.d("debug", "LogcatGetterService:saveLog");
            writeFileName = FileName;
            if (isFileWrite) {
                return LOGWRITE_ALREADY_WRITE;
            }

            isTryWrite = true;
            return LOGWRITE_OK;
        }

        public int stopWrite() {
            Log.d("debug", "LogcatGetterService:stopWrite");
            if (!isFileWrite && !isTryWrite) {
                return -1;
            }
            isFileWrite = false;
            isTryWrite = false;
            closeFiles();
            return 1;

        }

        public boolean isWritting() {
        	Log.d("debug","isWritting:"+isFileWrite);
            return isFileWrite;
        }
        public void setLogBreak() {
            synchronized (mObjThreadBread) {
                mThreadBreak = true;
            }
        }

		@Override
		public String getWriteFilename() throws RemoteException {
			// TODO Auto-generated method stub
        	return writeFileName;
		}

    }

}
