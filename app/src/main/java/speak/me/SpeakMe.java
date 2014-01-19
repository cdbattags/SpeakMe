package speak.me;
import java.util.HashMap;
import java.util.List;
import com.example.speakme.R;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;


public class SpeakMe extends Service {

	public static final String ACTION_PICK_PLUGIN = "speak.me.action.PICK_PLUGIN";

	private static final int ONGOING_NOTIFICATION_ID = 123123;

//	private Looper serviceLooper;
//	private ServiceHandler serviceHandler;
	private HashMap<Intent,String[]> commandMap = new HashMap<Intent, String[]>();

//	private final class ServiceHandler extends Handler {
//		public ServiceHandler(Looper looper) {
//			super(looper);
//		}
//		
//		@Override
//		public void handleMessage(Message msg) {
//			boolean closed = false;
//			//Listen for the "key Words"
//			while(!closed){
//				synchronized(this) {
//					//.. Here
//				}
//			}
//			stopSelf(msg.arg1);
//		}
//	}

 

	@Override
	public void onCreate() {
		// Our main listening for the "OK Speak Me" prompt should definitely
		// be done in the background
		HandlerThread thread = new HandlerThread("DefaultListenerThread",
				Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();
//		
//		serviceLooper = thread.getLooper();
//		serviceHandler = new ServiceHandler(serviceLooper);
//		
		// Find all plugins that have the intent filter that we have specified.
		PackageManager pacMan = this.getPackageManager();
		Intent baseIntent = new Intent(ACTION_PICK_PLUGIN);
		baseIntent.setFlags(Intent.FLAG_DEBUG_LOG_RESOLUTION);
		List<ResolveInfo> plugins = 
				pacMan.queryIntentServices(baseIntent, PackageManager.GET_RESOLVED_FILTER);
		for (int i = 0; i < plugins.size(); i++) {
			ResolveInfo info = plugins.get(i);
			ServiceInfo sinfo = info.serviceInfo; 
			//Log.d("SPEAK ME", "[sinfo.name]: " + sinfo.name);
			//Log.d("SPEAK ME", "[binding]: " + 
			//		bindService(baseIntent, mConnection, Context.BIND_AUTO_CREATE));
			try {
				Log.d("SM", "[packageName:] " + sinfo.packageName);
				Log.d("SM", "[className:] " + sinfo.name);
				ComponentName cn = new ComponentName(sinfo.packageName, sinfo.name);
				ServiceInfo meta = pacMan.getServiceInfo(cn,PackageManager.GET_META_DATA);
							//"speak.me.plugin", "speak.me.plugin.SpeakMePlugin"), PackageManager.GET_META_DATA);
				String[] keywords = meta.metaData.getString("speak.me.keywords").split(",");
				Intent intent = new Intent(ACTION_PICK_PLUGIN);
				intent.setComponent(cn);
				commandMap.put(intent, keywords);
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 
		}

		// Set notification bar stuff
		Intent notificationIntent = new Intent(getApplicationContext(), SpeakMeMainActivity.class);
		notificationIntent.putExtra("CommandMap", commandMap);
		PendingIntent pendingIntent = PendingIntent.getActivity
				(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
	
		
		// Run this app in the foreground
		Notification notification = new Notification.Builder(this.getApplicationContext())
						 .setContentTitle("SpeakMe Running... ")
				         .setContentText("Click to access the SpeakMe Activity")
				         .setSmallIcon(R.drawable.ic_launcher)
				         .setContentIntent(pendingIntent)
				         .build();
		startForeground(ONGOING_NOTIFICATION_ID, notification);
		
	}
	
	 @Override
	  public int onStartCommand(Intent intent, int flags, int startId) {
	      Toast.makeText(this, "SpeakMe starting", Toast.LENGTH_SHORT).show();

	      // For each start request, send a message to start a job and deliver the
	      // start ID so we know which request we're stopping when we finish the job
//	      Message msg = serviceHandler.obtainMessage();
//	      msg.arg1 = startId;
//	      serviceHandler.sendMessage(msg);

	      // If we get killed, after returning from here, restart
	      return START_STICKY;
	  }

	@Override
	public IBinder onBind(Intent arg0) {
		// This main application thread will not be bound
		return null;
	}
	
	@Override
	  public void onDestroy() {
	    Toast.makeText(this, "SpeakMe service done", Toast.LENGTH_SHORT).show();
	  }

}
