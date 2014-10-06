package sw.applist;

import java.util.ArrayList;
import java.util.List;

import com.srph.simplesapprovider.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class AppList extends Activity {
	
	PackageManager packageManager;
	
	
	ListView ListView_navi;		// navigation의 ListView
	ListView ListView_music;	// music의 ListView
	
	Button btn_ok;				// 확인 버튼
	
	String appName_navi;		
	String appName_music;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.applist);
		
		packageManager = getPackageManager();
		// 해당 디바이스에 Installed된 어플리케이션 패키지들을 가져온다. 
        List<PackageInfo> packageList = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS);
		
        // ListNavi에 들어갈 패키지정보
		List<PackageInfo> Listnavi = new ArrayList<PackageInfo>();
		
		// ListMusic에 들어갈 패키지정보
		List<PackageInfo> Listmusic = new ArrayList<PackageInfo>();
		btn_ok = (Button) findViewById(R.id.btn_AppListOK);
		
		// For문을 돌며 Label들을 비교해서 해당된 어플리케이션이 있으면 그에 맞는 List에 넣어준다.
		for(PackageInfo pi : packageList){
			
			if (packageManager.getApplicationLabel(pi.applicationInfo)
					.toString().equals("T map")) {
				Listnavi.add(pi);			
				
			}
			
			if (packageManager.getApplicationLabel(pi.applicationInfo)
					.toString().equals("U+ Navi Real")) {
				Listnavi.add(pi);
				
			}
			
			if (packageManager.getApplicationLabel(pi.applicationInfo)
					.toString().equals("지도")) {
				Listnavi.add(pi);
				
			}
			
			if (packageManager.getApplicationLabel(pi.applicationInfo)
					.toString().equals("MelOn")) {
				Listmusic.add(pi);
				
			}
			
			if (packageManager.getApplicationLabel(pi.applicationInfo)
					.toString().equals("뮤직")) {
				Listmusic.add(pi);
				
			}
			
			if (packageManager.getApplicationLabel(pi.applicationInfo)
					.toString().equals("Mnet")) {
				Listmusic.add(pi);
				
			}
			
			if (packageManager.getApplicationLabel(pi.applicationInfo)
					.toString().equals("카카오뮤직")) {
				Listmusic.add(pi);
				
			}
		}
		
		ListView_navi = (ListView) findViewById(R.id.list_navi);
		ListView_music = (ListView) findViewById(R.id.list_music);
		
		// ListView에 맞는 각각의 Adater를 설정해준다.
		ListView_navi.setAdapter(
				new AdapterNavi(this, Listnavi, packageManager , appName_navi));		
		ListView_music.setAdapter(
				new AdapterMusic(this, Listmusic, packageManager , appName_music));
		
		// Intent에 선택된 App값을 넣어주면서 반환한다.
		// 또한 SharedPreferneces에 navi와 music 정보를 설정해준다.
		btn_ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = getIntent();
				intent.putExtra("navi",AppModel.navi);
	            intent.putExtra("music", AppModel.music );
	            
	            intent.putExtra("appName_navi",AppModel.appName_navi);
	            intent.putExtra("appName_music", AppModel.appName_music );
	            
	            SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
		        SharedPreferences.Editor editor = pref.edit();
		        editor.putString("navi", AppModel.navi);
		        editor.putString("music", AppModel.music);
		        
		        editor.putString("appName_navi",AppModel.appName_navi);
		        editor.putString("appName_music", AppModel.appName_music );
		        
		        editor.commit();
	            
	            setResult(RESULT_OK,intent);				
				finish();
			}
		});
		
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // ActionBar 부분을 셋팅한다.
        getActionBar().setTitle("앱설정");
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#4ad99c")));
        return true;
    }

}
