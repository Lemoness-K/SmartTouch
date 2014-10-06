package sw.srph.sapprovider.service;

import sw.applist.AppList;
import sw.call.CallModel;
import sw.call.CallSetting;
import sw.navigation.NaviMain;

import com.srph.simplesapprovider.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;

import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnTouchListener{
	
	private int requestCode = 1;
	private int requestCallCode = 2;
		
	Button btn_set_app; 		// AppList 설정으로 가는 버튼	
	Button btn_set_bluetooth;	// Bluetooth 실행하는 버튼
	Button btn_setting_call;	// 주소록추가로 가는 버튼
	
	Button btn_call1;			// 전화등록후 실행하면 지정된 번호로 가는 버튼1
	Button btn_call2;			// 전화등록후 실행하면 지정된 번호로 가는 버튼2
	Button btn_call3;			// 전화등록후 실행하면 지정된 번호로 가는 버튼3
	Button btn_call4;			// 전화등록후 실행하면 지정된 번호로 가는 버튼4
	
	TextView call_name1;		// 전화등록후 리스트에 뜨는 이름1
	TextView call_name2;		// 전화등록후 리스트에 뜨는 이름2
	TextView call_name3;		// 전화등록후 리스트에 뜨는 이름3
	TextView call_name4;		// 전화등록후 리스트에 뜨는 이름4
	
	TextView call_phone1;		// 전화등록후 리스트에 뜨는 번호1
	TextView call_phone2;		// 전화등록후 리스트에 뜨는 번호2
	TextView call_phone3;		// 전화등록후 리스트에 뜨는 번호3
	TextView call_phone4;		// 전화등록후 리스트에 뜨는 번호4
	
	PackageManager packageManager;	// 설치된 어플리케이션 정보를 Manager 하는 것
		
	ImageView img_navi;			// 설정된 네비게이션 이미지
	TextView navi_name;			// 설정된 네비게이션 이름
	
	ImageView img_music;		// 설정된 음악 이미지
	TextView music_name;		// 설정된 음악 이름
	
	Button btn_play_navi;		// 설정된 네비게이션 실행 버튼
	Button btn_play_music;		// 설정된 음악 실행 버튼
	
	CheckBox check_call1;		// 설정된 번호1 실행 버튼
	CheckBox check_call2;		// 설정된 번호2 실행 버튼
	CheckBox check_call3;		// 설정된 번호3 실행 버튼
	CheckBox check_call4;		// 설정된 번호4 실행 버튼
	
	private static PowerManager.WakeLock mWakeLock;	// 디바이스 화면켜기 
	
	private Intent sapintent;	// intent 변수
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// activity_main.xml에 있는 레이아웃 불러오기
		btn_set_app = (Button) findViewById(R.id.btn_EditApp);
        btn_set_bluetooth = (Button) findViewById(R.id.btn_set_bluetooth);
        btn_setting_call = (Button) findViewById(R.id.btn_setting_call);
        
        btn_play_navi = (Button) findViewById(R.id.btn_play_navi);
        btn_play_music = (Button) findViewById(R.id.btn_play_music);
        
        img_music = (ImageView) findViewById(R.id.imageView_music_icon);
        img_navi = (ImageView) findViewById(R.id.imageView_navi_icon);
        
        navi_name = (TextView) findViewById(R.id.textView_navi_name);
        music_name = (TextView) findViewById(R.id.textView_music_name);
        
        btn_call1 = (Button) findViewById(R.id.btn_call1);
        btn_call2 = (Button) findViewById(R.id.btn_call2);
        btn_call3 = (Button) findViewById(R.id.btn_call3);
        btn_call4 = (Button) findViewById(R.id.btn_call4);
        
        call_name1 = (TextView) findViewById(R.id.textView_callName1);
        call_name2 = (TextView) findViewById(R.id.textView_callName2);
        call_name3 = (TextView) findViewById(R.id.textView_callName3);
        call_name4 = (TextView) findViewById(R.id.textView_callName4);
        
        call_phone1 = (TextView) findViewById(R.id.textView_callPhone1);
        call_phone2 = (TextView) findViewById(R.id.textView_callPhone2);
        call_phone3 = (TextView) findViewById(R.id.textView_callPhone3);
        call_phone4 = (TextView) findViewById(R.id.textView_callPhone4);        
        
        btn_set_app.setOnTouchListener(this);
        
        btn_setting_call.setOnTouchListener(this);
        
        btn_set_bluetooth.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				
//				if(btService == null) {
//		            btService = new BluetoothService(this, mHandler);
//		        }
				
				String setting = Settings.ACTION_BLUETOOTH_SETTINGS;
				Intent intent = new Intent(setting);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

				Context c = getApplicationContext();

				c.startActivity(intent);
				
			}
		});
        
        
        btn_play_navi.setOnTouchListener(this);
        btn_play_music.setOnTouchListener(this);
        
        btn_call1.setOnTouchListener(this);
        btn_call2.setOnTouchListener(this);
        btn_call3.setOnTouchListener(this);
        btn_call4.setOnTouchListener(this);
        
        // 지금 디바이스 정보를 가지고 올수 있게 한다.
        packageManager = getPackageManager();
        
        // SharedPreferneces에 저장된 pref 값을 가지고 온다.
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        // pref 에서도 String navi가 있는지 여부를 검사한다.
        if(pref.getString("navi", "").equals("")){
        	
        }else{
        	// 저장된 navi 값이 있으면 setNaviView에 값을 넘겨준다. pacakagname과 appName을 넘겨준다. 
        	setNaviView(pref.getString("navi", ""), pref.getString("appName_navi", ""));
        }

        // pref 에서도 String  music가 있는지 여부를 검사한다.
		if(pref.getString("music", "").equals("")){
			
		}else{
			// 저장된 navi 값이 있으면 setMusicView에 값을 넘겨준다. pacakagname과 appName을 넘겨준다.
			setMusicView(pref.getString("music", ""), pref.getString("appName_music", ""));
		}
		
		// Shared에 있는 값을 너어준다.
		setCall();
        
	}
	
	
	// 액티비티 실행후 결과값
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==RESULT_OK) {
			if(requestCode == 1){				
					if(data.getStringExtra("navi").equals("")){
						
					}else{
						setNaviView(data.getStringExtra("navi"), data.getStringExtra("appName_navi"));
					}
					
					if(data.getStringExtra("music").equals("")){
						
					}else{
						setMusicView(data.getStringExtra("music"),data.getStringExtra("appName_music"));
					}			
			}else{
				
			}
			
			if(requestCallCode == 2){
				setCall();

				
			}else{
				
			}
		}
	}
	
	// Device가 꺼져있으면 다시 켜주는 Method
	@SuppressWarnings("deprecation")
	static void acquireWakeLock(Context context) {
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(
				PowerManager.FULL_WAKE_LOCK
				| PowerManager.ACQUIRE_CAUSES_WAKEUP, context.getClass().getName());

		if (mWakeLock != null) {
			mWakeLock.acquire();
		}
	}
	
	
	// Navi에 관련된 Layout 설정하는 Method
	// PackageName과 AppName을 받게된다. 그값으로 Icon을 가지고 올 수 있다. 
	public void setNaviView(String packageName, String appname ){
		Drawable appIcon;
		try {
			appIcon =  packageManager.getApplicationIcon(packageName);
			appIcon.setBounds(0, 0, 100, 100);
			img_navi.setImageDrawable(appIcon);
			navi_name.setText(appname);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
		
	}
	
	// Music에 관련된 Layout 설정하는 Method
	// PackageName과 AppName을 받게된다. 그값으로 Icon을 가지고 올 수 있다.
	public void setMusicView(String packageName, String appname ){
		Drawable appIcon;
		try {
			appIcon =  packageManager.getApplicationIcon(packageName);
			appIcon.setBounds(0, 0, 100, 100);
			img_music.setImageDrawable(appIcon);
			music_name.setText(appname);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
		
	}
	
	// App 목록을 가지고 오는 AppList class로 인텐트를 실행시킨다.
	public void setting_app(){
		Intent intent = new Intent(getBaseContext(), AppList.class);
		startActivityForResult(intent, requestCode);
	}
	
	// Call에 관련된 주소록에서 전화를 할 번호와 정보를 추가시키는것.
	@SuppressLint("InflateParams")
	public void setting_call(){
		// 다이얼로그를 먼저 띄어준다.
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);				
		View dialogView = getLayoutInflater().inflate(R.layout.call_dialog, null );
		dialog.setView(dialogView);
		
		// call_dailog.xml에 있는 레이아웃을 불러온다.
		check_call1 = (CheckBox) dialogView.findViewById(R.id.CheckBox_call1);
        check_call2 = (CheckBox) dialogView.findViewById(R.id.CheckBox_call2);
        check_call3 = (CheckBox) dialogView.findViewById(R.id.CheckBox_call3);
        check_call4 = (CheckBox) dialogView.findViewById(R.id.CheckBox_call4);
        
        // check_call1 번째 박스에 대한 ClickListener
        // 클릭을 하면 checkCall() 메서드로 가는데 이 메서드는 다른 체크박스가 체크되어있는지의 여부를 검사한다.
        // 4개 박스중 하나만 체크되어 있도록 하도록 도와준다.
        check_call1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				checkCall(v);
			}
		});
        check_call2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				checkCall(v);
			}
		});
        check_call3.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				checkCall(v);
			}
		});
        check_call4.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				checkCall(v);
			}
		});
		// dialog 에서 확인 버튼을 누르면 실행되는 Event 설정
		dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {					
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// CallSetting class란 주소록을 불러오는 class 이다.
				Intent intent = new Intent(getBaseContext(), CallSetting.class);
				String call = null;
				// checkBox에 체크여부를 검사해서 intent객체와 같이 CallSetting 클래스로 넘겨준다.
				if(check_call1.isChecked()){
					call = "1";
				}else if (check_call2.isChecked()){
					call = "2";
				}else if(check_call3.isChecked()){
					call = "3";
				}else if(check_call4.isChecked()){
					call = "4";
				}
				intent.putExtra("call_setting",call);
				startActivityForResult(intent, requestCallCode);
			}
		});
		// Dialog취소했을때 생기는 Event 설정
		dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		
		// Dialog 실행
		dialog.show();
		
		
	}	
	
	// checkBox 여부.
	// CallModel.checkbox_call 은 static 변수로 그 이전의 checkbox값을 가지고 있는다.
	// 체크가 되어있으면 그이전의 checkBox 값을 false로 하고
	// 지금의 CheckBox View를 true로 유지해준다.
	public void checkCall(View v){
		if(CallModel.check_call){
			CallModel.checkbox_call.setChecked(false);
			CallModel.checkbox_call = (CheckBox) v;	
		}else{
			CallModel.checkbox_call = (CheckBox) v;
			CallModel.check_call = true;
		}	
	}
	
	// 설정된 Navigation Application을 실행하는 Method
	// SharedPreferences에서 값을 불러와서 intent를 실행시킨다.
	// 지금은 T_Map과의 연동밖에 하지않는 상태라 NaviMain으로 설정을 강제로 해놨다.
	public void startNavi(){
		SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
		if(pref.getString("navi", "").equals("")){			
		} else {			
			sapintent = new Intent(MainActivity.this, NaviMain.class);
			sapintent.setAction(Intent.ACTION_MAIN);			
			startActivity(sapintent);
		}
	}
	
	// 설정된 Music Application을 실행하는 Method
	// SharedPreferences에서 값을 불러와서 intent를 실행시킨다.
	public void startMusic(){
		SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
		if(pref.getString("music", "").equals("")){
			
		} else {
			sapintent = getPackageManager().getLaunchIntentForPackage(pref.getString("music", ""));			
			sapintent.setAction(Intent.ACTION_MAIN);			
			startActivity(sapintent);
		}
	}
	
	// 지정된 번호가 들어오면 그 번호로 Call을 해주는 Method이다.
	public void startCall(String str)
	{    
      if( str.equals("noData") )
      {
         Toast.makeText(this, "지정된 연락처가 없습니다.", Toast.LENGTH_SHORT).show();
         return;
      }else{
    	  Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+str));
          startActivity(intent);  
      }
      
      
   }
	
	// 처음 Create될때 Call Layout 부분을 초기화 시켜주는 메서드
	public void setCall(){
		SharedPreferences contract = getSharedPreferences("contract", MODE_PRIVATE);
		
		String call1 = contract.getString("contract1", "noData");
		String name1 = contract.getString("contract_name1", "noData");
		
		String call2 = contract.getString("contract2", "noData");
		String name2 = contract.getString("contract_name2", "noData");
		
		String call3 = contract.getString("contract3", "noData");
		String name3 = contract.getString("contract_name3", "noData");
		
		String call4 = contract.getString("contract4", "noData");
		String name4 = contract.getString("contract_name4", "noData");
		
		if(call1.equals("noData")){
			
		}else{
			call_name1.setText(name1);
			call_phone1.setText(call1);
		}
		
		if(call2.equals("noData")){
							
		}else{
			call_name2.setText(name2);
			call_phone2.setText(call2);
		}
		
		if(call3.equals("noData")){
			
		}else{
			call_name3.setText(name3);
			call_phone3.setText(call3);
		}
		
		if(call4.equals("noData")){
			
		}else{
			call_name4.setText(name4);
			call_phone4.setText(call4);
		}
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().hide();
        return true;
    }
	
	
	// implements TouchListener을 하면 생성되는 Method 
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		Button b = (Button) v;
        
        int action=event.getAction();
		// TODO Auto-generated method stub
		if(action==MotionEvent.ACTION_DOWN)
        {
			// 터치 다운이 되면 백그라운드를 바꿔준다.
            b.setBackgroundResource(R.drawable.btn_appstart_down);            
        }
		// 터치 업이 되면 백그라운드를 바꿔준다.
		// 그것에 따른 버튼 id 값에 따라 메서드를 그에 맞는 걸로 실행시켜준다.
        else if(action==MotionEvent.ACTION_UP){
        	SharedPreferences contract = getSharedPreferences("contract", MODE_PRIVATE);
        	b.setBackgroundResource(R.drawable.btn_appstart_up); 
    		switch (v.getId()) {
    		
    		case R.id.btn_call1:
    			String strContract1 = contract.getString("contract1", "noData");
    			startCall(strContract1);
    			break;
    		case R.id.btn_call2:
    			String strContract2 = contract.getString("contract2", "noData");
    			startCall(strContract2);
    			break;
    		case R.id.btn_call3:
    			String strContract3 = contract.getString("contract3", "noData");
    			startCall(strContract3);
    			break;
    		case R.id.btn_call4:
    			String strContract4 = contract.getString("contract4", "noData");
    			startCall(strContract4);
    			break;
    		
    		case R.id.btn_EditApp:
    			setting_app();
    			break;
    		case R.id.btn_setting_call:
    			setting_call();
    			break;
    		case R.id.btn_play_navi:
    			startNavi();
    			break;
    		case R.id.btn_play_music:
    			startMusic();
    			break;
    			
    		default:
    			break;
    		}        	
        }
        return true;
		
	}

}
