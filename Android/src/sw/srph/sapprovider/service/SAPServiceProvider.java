package sw.srph.sapprovider.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import sw.applist.AppModel;
import sw.navigation.NaviMain;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RecentTaskInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.accessory.SA;
import com.samsung.android.sdk.accessory.SAAgent;
import com.samsung.android.sdk.accessory.SAPeerAgent;
import com.samsung.android.sdk.accessory.SASocket;
import com.skp.Tmap.TMapTapi;
import com.skp.Tmap.TMapView;

// 기어와 통신하는 프로토콜
public class SAPServiceProvider extends SAAgent {

	public static final String TAG = "SAPServiceProvider";	// 태그이름

	public final static int SERVICE_CONNECTION_RESULT_OK = 0;

	public final static int SAP_SERVICE_CHANNEL_ID = 124; // SAP 서비스 채널 ID

	HashMap<Integer, SAPServiceProviderConnection> connectionMap = null;	

	private final IBinder mIBinder = new LocalBinder();

	private Intent sapintent;

	private AppModel model;
	private boolean check = false;

	// 노래 제목 받아올 준비
	public static final String SERVICECMD = "com.android.music.musicservicecommand";
	public static final String CMDNAME = "command";
	public static final String CMDTOGGLEPAUSE = "togglepause";
	public static final String CMDSTOP = "stop";
	public static final String CMDPAUSE = "pause";
	public static final String CMDPREVIOUS = "previous";
	public static final String CMDNEXT = "next";
	

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			String cmd = intent.getStringExtra("command");
			Log.d("mIntentReceiver.onReceive ", action + " / " + cmd);
			String artist = intent.getStringExtra("artist");
			String album = intent.getStringExtra("album");
			String track = intent.getStringExtra("track");
			Log.d("Music", artist + ":" + album + ":" + track);
		}
	};
	// 준비끝
	
	// 서비스 준비단계
	public class LocalBinder extends Binder {
		public SAPServiceProvider getService() {
			Log.d("SAP PROVIDER", "LOCAL BINDER GET SERVICE");
			return SAPServiceProvider.this;
		}
	}
	
	// 생성자
	public SAPServiceProvider() {
		super(TAG, SAPServiceProviderConnection.class);
		Log.d("SAP PROVIDER", "SERVICE CONSTRUCTOR");
	}
	
	
	// SAP service Connection 하는 부분
	public class SAPServiceProviderConnection extends SASocket {
		private int mConnectionId;

		public SAPServiceProviderConnection() {
			super(SAPServiceProviderConnection.class.getName());
			Log.d("SAP PROVIDER", "SAP CONNECTION CONSTRUCTOR");
		}

		@Override
		public void onError(int channelId, String errorString, int error) {
			Log.e(TAG, "Connection is not alive ERROR: " + errorString + "  "
					+ error);

			Log.d("SAP PROVIDER", "SAP CONNECTION ERROR " + errorString
					+ " || " + error);
		}
		
		// 기어에서 안드로이드로 값이 넘어옴
		// 미리 설정이된 channelID값과 데이터가 넘어온다.
		@Override
		public void onReceive(int channelId, byte[] data) {
			
			// 요청이왔을때 화면을 키게된다.
			MainActivity.acquireWakeLock(getApplicationContext());
			final String message;

			final String response = new String(data);

			message = response.concat(" : success");

			Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show();
			
			final SAPServiceProviderConnection uHandler = connectionMap
					.get(Integer.parseInt(String.valueOf(mConnectionId)));
			if (uHandler == null) {
				Log.e(TAG,
						"Error, can not get HelloAccessoryProviderConnection handler");
				return;
			}

			if (response.equals("")) {

			}
			
			// response 안에 요청에 따라 행하여 지는 부분이 달라진다.
			/* Navi Control 하는 부분 */
			else if (response.equals("Play_Navi")) {
				startNavi();
			} else if (response.equals("Play_Music")) {
				startMusic();
			} else if (response.equals("Navi_Home")) {
				startHome();
			} else if (response.equals("Navi_Company")) {
				startCom();
			} else if (response.equals("Navi_Fav")) {

			}

			/* Call Control 하는부분  */
			else if (response.equals("Call_Fav1")) {
				startCall("1");
			} else if (response.equals("Call_Fav2")) {
				startCall("2");
			} else if (response.equals("Call_Fav3")) {
				startCall("3");
			} else if (response.equals("Call_Fav4")) {
				startCall("4");
			} else if (response.equals("Call_Fav4")) {
				startCall("4");
			}
			/* Music Control 하는부분  */
			else if (response.equals("Vol_Down")) {
				setVolume(-1);
			} else if (response.equals("Vol_Up")) {
				setVolume(1);
			} else if (response.equals("Music_Prev")) {				
				putCommand(KeyEvent.KEYCODE_MEDIA_NEXT);
			} else if (response.equals("Music_Next")) {
				putCommand(KeyEvent.KEYCODE_MEDIA_PREVIOUS);
			} else if (response.equals("Music_Playpause")) {
				putCommand(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
			}

			/* Bluetooth Control하는 부분 */
			else if (response.equals("Reset_Blue")) {
				settingBluetooth();

				model.start_com = false;
				model.start_fav = false;
				model.start_home = false;
				check = false;
			}
		
			// 값에 대한 success한 결과를 넘겨준다.
			new Thread(new Runnable() {
				public void run() {
					try {						
		
						uHandler.send(SAP_SERVICE_CHANNEL_ID, message.getBytes());
											
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
		
		// 통신이 끊꼇을때의 메소드
		@Override
		protected void onServiceConnectionLost(int errorCode) {
			Log.e(TAG, "onServiceConectionLost  for peer = " + mConnectionId
					+ "error code =" + errorCode);

			if (connectionMap != null) {
				connectionMap.remove(mConnectionId);
			}
		}
	}
	
	// Navi를 시작하는 단계
	public void startNavi() {
		// 미리 저장된 값을 불러온다.
		SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
		if (pref.getString("navi", "").equals("")) {
		} else {
			// 이미 네비게이션이 동작을 하는지 안하는지에대해 check를 한다.
			if (check == false) {
				// NaviMain 클래스가 작동한다.
				check = true;
				sapintent = new Intent(getBaseContext(), NaviMain.class);
				sapintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(sapintent);
			} else {
				// 네비게이션이 동작을 하면 다른 어플리케이션을 동작하였다 실행하면
				// ActivityManager가 지금 사용하는 서비스들을 수집한후 
				// RecentTaskInfo를 통해 네비게이션 pacakage값과 비교를 한 후
				// Manager를 통해 앞으로 다시 불러온다.
				ActivityManager am = (ActivityManager) getSystemService(Activity.ACTIVITY_SERVICE);
				List<RecentTaskInfo> recentTasks = am.getRecentTasks(
						Integer.MAX_VALUE, 1);
				RecentTaskInfo taskInfo = null;

				int taskId = 0;
				for (int i = 0; i < recentTasks.size(); i++) {
					if (recentTasks.get(i).baseIntent.getComponent()
							.getPackageName()
							.equals(pref.getString("navi", "")))
						taskInfo = recentTasks.get(i);
				}

				taskId = taskInfo.persistentId;
				am.moveTaskToFront(taskId, 0);
			}
		}
	}
	
	// 집으로 가는 T-Map에 관한 메서드
	public void startHome() {
		SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
		if (pref.getString("navi", "").equals("")) {
		} else {

			if (model.start_home == false) {
				model.start_com = false;
				model.start_fav = false;

				model.start_home = true;

				String packageNavi = pref.getString("navi", "");
				sapintent = getPackageManager().getLaunchIntentForPackage(
						packageNavi);

				sapintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

				TMapView tmap = new TMapView(getBaseContext());
				TMapTapi tapi = new TMapTapi(getBaseContext());
				tmap.setSKPMapApiKey("00d48bbf-4aa6-3092-a586-b610c51df9df");
				tmap.setSKPMapBizappId("fed99a9eb919");

				tapi.invokeGoHome();

			} else {
				model.start_com = false;
				model.start_fav = false;

				ActivityManager am = (ActivityManager) getSystemService(Activity.ACTIVITY_SERVICE);
				List<RecentTaskInfo> recentTasks = am.getRecentTasks(
						Integer.MAX_VALUE, 1);
				RecentTaskInfo taskInfo = null;

				int taskId = 0;
				for (int i = 0; i < recentTasks.size(); i++) {
					if (recentTasks.get(i).baseIntent.getComponent()
							.getPackageName()
							.equals(pref.getString("navi", "")))
						taskInfo = recentTasks.get(i);
				}

				taskId = taskInfo.persistentId;
				am.moveTaskToFront(taskId, 0);
			}
		}
	}
	
	// startHome과 똑같은 구조이다. 단지 model.start_com 빼고 모두 false를 가지게 된다.
	public void startCom() {

		Log.d("ms", "" + model.start_com);
		SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
		if (pref.getString("navi", "").equals("")) {
		} else {
			if (model.start_com == false) {
				model.start_home = false;
				model.start_fav = false;

				model.start_com = true;

				String packageNavi = pref.getString("navi", "");
				sapintent = getPackageManager().getLaunchIntentForPackage(
						packageNavi);

				sapintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				// sapintent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

				TMapView tmap = new TMapView(getBaseContext());
				TMapTapi tapi = new TMapTapi(getBaseContext());
				tmap.setSKPMapApiKey("00d48bbf-4aa6-3092-a586-b610c51df9df");
				tmap.setSKPMapBizappId("fed99a9eb919");

				tapi.invokeGoCompany();

			} else {
				model.start_home = false;
				model.start_fav = false;

				ActivityManager am = (ActivityManager) getSystemService(Activity.ACTIVITY_SERVICE);
				List<RecentTaskInfo> recentTasks = am.getRecentTasks(
						Integer.MAX_VALUE, 1);
				RecentTaskInfo taskInfo = null;

				int taskId = 0;
				for (int i = 0; i < recentTasks.size(); i++) {
					if (recentTasks.get(i).baseIntent.getComponent()
							.getPackageName()
							.equals(pref.getString("navi", "")))
						taskInfo = recentTasks.get(i);
				}
				// //////////////////////////// 로그 잡기
				Log.e("msg", taskInfo.baseIntent.getComponent()
						.getPackageName());
				taskId = taskInfo.persistentId;
				am.moveTaskToFront(taskId, 0);
			}
		}
	}
	
	// 위의 두 내용과 같습니다.
	public void startFav() {
		SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
		if (pref.getString("navi", "").equals("")) {
		} else {

			if (model.start_fav == false) {
				model.start_home = false;
				model.start_com = false;

				model.start_fav = true;

				String packageNavi = pref.getString("navi", "");
				sapintent = getPackageManager().getLaunchIntentForPackage(
						packageNavi);

				sapintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

				TMapView tmap = new TMapView(getBaseContext());
				TMapTapi tapi = new TMapTapi(getBaseContext());
				tmap.setSKPMapApiKey("00d48bbf-4aa6-3092-a586-b610c51df9df");
				tmap.setSKPMapBizappId("fed99a9eb919");

				SharedPreferences p_lat = getSharedPreferences("p_lat",
						MODE_PRIVATE);
				SharedPreferences p_lng = getSharedPreferences("p_lng",
						MODE_PRIVATE);

				tapi.invokeRoute("", p_lng.getFloat("p_lng", 0),
						p_lat.getFloat("p_lat", 0));
			} else {
				model.start_com = false;
				model.start_home = false;

				ActivityManager am = (ActivityManager) getSystemService(Activity.ACTIVITY_SERVICE);
				List<RecentTaskInfo> recentTasks = am.getRecentTasks(
						Integer.MAX_VALUE, 1);
				RecentTaskInfo taskInfo = null;

				int taskId = 0;
				for (int i = 0; i < recentTasks.size(); i++) {
					if (recentTasks.get(i).baseIntent.getComponent()
							.getPackageName()
							.equals(pref.getString("navi", "")))
						taskInfo = recentTasks.get(i);
				}

				Log.e("msg", taskInfo.baseIntent.getComponent()
						.getPackageName());
				taskId = taskInfo.persistentId;
				am.moveTaskToFront(taskId, 0);
			}
		}
	}
	
	// 음악을 실행하는 메서드
	// 미리 저장된 값의 Package 이름을 따와서 해당앱을 실행시킨다.
	public void startMusic() {
		SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
		if (pref.getString("music", "").equals("")) {

		} else {
			
			String packageMusic = pref.getString("music", "");
			sapintent = getPackageManager().getLaunchIntentForPackage(
					packageMusic);
			sapintent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(sapintent);
		}
	}
	
	// Music 오디오 서비스와 다음곡 이전곡 하는 부분
	private void putCommand(int event) {
		try {
			IBinder iBinder = (IBinder) Class
					.forName("android.os.ServiceManager")
					.getDeclaredMethod("checkService", String.class)
					.invoke(null, Context.AUDIO_SERVICE);
			Object audioService = Class
					.forName("android.media.IAudioService$Stub")
					.getDeclaredMethod("asInterface", IBinder.class)
					.invoke(null, iBinder);

			KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, event);
			Class.forName("android.media.IAudioService")
					.getDeclaredMethod("dispatchMediaKeyEvent", KeyEvent.class)
					.invoke(audioService, keyEvent);

			KeyEvent keyEventUp = new KeyEvent(KeyEvent.ACTION_UP, event);
			Class.forName("android.media.IAudioService")
					.getDeclaredMethod("dispatchMediaKeyEvent", KeyEvent.class)
					.invoke(audioService, keyEventUp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	// 음악 볼륨을 조절하는 부분
	@SuppressWarnings("static-access")
	private void setVolume(int i) {

		AudioManager audiomanager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		int mCurvol = audiomanager.getStreamVolume(audiomanager.STREAM_MUSIC);
		if (mCurvol + i > audiomanager
				.getStreamMaxVolume(audiomanager.STREAM_MUSIC)) {
			mCurvol = audiomanager
					.getStreamMaxVolume(audiomanager.STREAM_MUSIC);
		} else {
			audiomanager.setStreamVolume(audiomanager.STREAM_MUSIC,
					mCurvol += i, audiomanager.FLAG_PLAY_SOUND);
		}
	}

	public void startCall(String str) {
		String call = "contract" + str;
		SharedPreferences contract = getSharedPreferences("contract",
				MODE_PRIVATE);
		String tmp = contract.getString(call, "noData");

		if (tmp.equals("noData")) {
			Toast.makeText(getApplicationContext(),
					str + "번째 " + "등록된 연락처가 없습니다.", Toast.LENGTH_SHORT).show();
		} else {
			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
					+ tmp));
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}
	}

	public void settingBluetooth() {
		String setting = Settings.ACTION_BLUETOOTH_SETTINGS;
		Intent intent = new Intent(setting);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "onCreate of smart view Provider Service");

		SA mAccessory = new SA();
		try {
			mAccessory.initialize(this);
			Log.d("SAP PROVIDER", "ON CREATE TRY BLOCK");
		} catch (SsdkUnsupportedException e) {
			// Error Handling
			Log.d("SAP PROVIDER", "ON CREATE TRY BLOCK ERROR UNSUPPORTED SDK");
		} catch (Exception e1) {
			Log.e(TAG, "Cannot initialize Accessory package.");
			e1.printStackTrace();

			stopSelf();
		}

		model = new AppModel();

	}

	@Override
	protected void onServiceConnectionRequested(SAPeerAgent peerAgent) {
		acceptServiceConnectionRequest(peerAgent);
		Log.d("SAP PROVIDER",
				"CONNECTION REQUESTED : " + peerAgent.getAppName());
	}

	@Override
	protected void onFindPeerAgentResponse(SAPeerAgent arg0, int arg1) {
		Log.d(TAG, "onFindPeerAgentResponse  arg1 =" + arg1);
	}

	@SuppressLint("UseSparseArrays")
	@Override
	protected void onServiceConnectionResponse(SASocket thisConnection,
			int result) {
		if (result == CONNECTION_SUCCESS) {
			if (thisConnection != null) {
				SAPServiceProviderConnection myConnection = (SAPServiceProviderConnection) thisConnection;

				if (connectionMap == null) {
					connectionMap = new HashMap<Integer, SAPServiceProviderConnection>();
				}

				myConnection.mConnectionId = (int) (System.currentTimeMillis() & 255);

				Log.d(TAG, "onServiceConnection connectionID = "
						+ myConnection.mConnectionId);

				connectionMap.put(myConnection.mConnectionId, myConnection);

				Toast.makeText(getBaseContext(), "CONNECTION SUCCESS", Toast.LENGTH_LONG).show();
				/* 연결 이후 노래 제목 받아올 부분 */
				IntentFilter iF = new IntentFilter();
				iF.addAction("com.android.music.metachanged");
				iF.addAction("com.android.music.playstatechanged");
				iF.addAction("com.android.music.playbackcomplete");
				iF.addAction("com.android.music.queuechanged");

				registerReceiver(mReceiver, iF);

			} else {
				Log.e(TAG, "SASocket object is null");
			}
		} else if (result == CONNECTION_ALREADY_EXIST) {
			Log.e(TAG, "onServiceConnectionResponse, CONNECTION_ALREADY_EXIST");
		} else {
			Log.e(TAG, "onServiceConnectionResponse result error =" + result);
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		Log.d("SAP PROVIDER", "onBIND");
		return mIBinder;
	}

	public String getDeviceInfo() {
		String manufacturer = Build.MANUFACTURER;

		String model = Build.MODEL;

		return manufacturer + " " + model;
	}

}
