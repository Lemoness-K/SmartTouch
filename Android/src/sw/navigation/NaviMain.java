package sw.navigation;

import java.util.ArrayList;

import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapTapi;
import com.skp.Tmap.TMapView;
import com.skp.Tmap.TMapView.OnLongClickListenerCallback;
import com.srph.simplesapprovider.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.RelativeLayout;

/**
 * 집, 회사, 즐겨찾기 경로 안내를 시작하기 위해 
 * Tmap API를 이용하여 구현한 클래스
 */
public class NaviMain extends Activity implements OnTouchListener{

	private TMapPoint m_point;
	private TMapView tmap;
	Context c = getApplication();
	private TMapTapi tapi;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_navi);
		
		// 맵뷰를 표시하기 위한 레이아웃
		RelativeLayout mapView = (RelativeLayout) findViewById(R.id.MapLayout);

		// 경로 안내를 위한 TmapTapi 객체 생성
		tapi = new TMapTapi(this);

		// 지도 표시를 위한 TmapView 객체 생성
		tmap = new TMapView(this);
		
		// 이전의 즐겨찾기 경로의 위도, 경도 받아오기
		SharedPreferences p_lat = getSharedPreferences("p_lat", MODE_PRIVATE);
		SharedPreferences p_lng = getSharedPreferences("p_lng", MODE_PRIVATE);
		
		// 즐겨찾기 경로의 정보가 없을 경우 Default로 지정해둔다.
		if( p_lat.getFloat("p_lat", 0) == 0)
			m_point = new TMapPoint(37.566474, 126.985022);		
		else
			m_point = new TMapPoint(p_lat.getFloat("p_lat", 0), p_lng.getFloat("p_lng", 0));
		
		// TmapMarkerItem을 TmapView에 표시
		Bitmap bitmap = BitmapFactory.decodeResource(c.getResources(),R.drawable.icon_marker32);
		
		TMapMarkerItem m_marker = new TMapMarkerItem();
		m_marker.setTMapPoint(m_point);
		m_marker.setIcon(bitmap);
		
		tmap.addMarkerItem("marker_id", m_marker);

		mapView.addView(tmap);

		// Tmap 초기화
		// 지도를 사용하기 위한 ApiKey 와
		// 경로 안내를 사용하기 위한 BizappID 등록
		tmap.setSKPMapApiKey("00d48bbf-4aa6-3092-a586-b610c51df9df");
		tmap.setSKPMapBizappId("fed99a9eb919");

		tmap.setLanguage(tmap.LANGUAGE_KOREAN);
		tmap.setIconVisibility(false);
		tmap.setZoomLevel(15);
		tmap.setMapType(tmap.MAPTYPE_STANDARD);
		tmap.setCompassMode(false);
		tmap.setTrackingMode(true);
		
		tmap.setCenterPoint(m_point.getLongitude(), m_point.getLatitude());
		
		tmap.setLocationPoint(m_point.getLongitude(), m_point.getLatitude());
		
		// TmapView를 길게 터치하여 즐겨찾기 지점을 저장하고
		// 기존의 TmapMarkerItem을 교체한다 
		tmap.setOnLongClickListenerCallback(new OnLongClickListenerCallback() {

			@Override
			public void onLongPressEvent(ArrayList<TMapMarkerItem> arg0,
					ArrayList<TMapPOIItem> arg1, TMapPoint arg2) {
				// TODO Auto-generated method stub
				
				final TMapPoint mapPoint = arg2;
				
				AlertDialog.Builder dialog = new AlertDialog.Builder(c);
				
				dialog.setMessage("이 곳을 즐겨찾기 경로로 설정하시겠습니까?");
				
				dialog.setNeutralButton("취소", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				});
				
				dialog.setPositiveButton("확인", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Log.i("latlng",mapPoint.getLatitude()+", "+mapPoint.getLongitude());
						
						SharedPreferences lat, lng;
						lat = getSharedPreferences("p_lat", MODE_PRIVATE);
						lng = getSharedPreferences("p_lng", MODE_PRIVATE);
						
						SharedPreferences.Editor edit_lat = lat.edit();
						SharedPreferences.Editor edit_lng = lng.edit();
						
						edit_lat.putFloat("p_lat", (float)mapPoint.getLatitude());
						edit_lat.commit();
						edit_lng.putFloat("p_lng", (float)mapPoint.getLongitude());
						edit_lng.commit();
						
						tmap.removeMarkerItem("marker_id");
						
						Bitmap bitmap = BitmapFactory.decodeResource(c.getResources(),R.drawable.icon_marker32);
						
						TMapMarkerItem m_marker = new TMapMarkerItem();
						m_marker.setTMapPoint(mapPoint);
						m_marker.setIcon(bitmap);
						
						tmap.setCenterPoint(mapPoint.getLongitude(), mapPoint.getLatitude(), true);
						tmap.addMarkerItem("marker_id", m_marker);
					}
				});
				
				dialog.show();
			}
		});

		// 집, 회사, 즐겨찾기 경로 버튼에 대한
		// TouchListener 지정
		Button home, comp, freq;

		home = (Button) findViewById(R.id.home_button);
		comp = (Button) findViewById(R.id.comp_button);
		freq = (Button) findViewById(R.id.freq_button);

		home.setOnTouchListener(this);
		
		comp.setOnTouchListener(this);
		
		freq.setOnTouchListener(this);

	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getActionBar().setTitle("T Map");
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#4ad99c")));
        return true;
    }

	/** 
 	* 집, 회사, 즐겨찾기 경로에 대한 onTouch 메소드
 	* Overriding
 	*/
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		Button b = (Button) v;
		
        int action=event.getAction();
		// TODO Auto-generated method stub
		if(action==MotionEvent.ACTION_DOWN)
        {
            b.setBackgroundResource(R.drawable.btn_appstart_down);            
        }
        else if(action==MotionEvent.ACTION_UP){
        	b.setBackgroundResource(R.drawable.btn_appstart_up); 
        	
        	// 각각의 버튼에 따라 
        	// invokeGoHome, invokeGoCompany, invokeRoute 메소드와 연결한다.
        	// 이 메소드들은 실제 단말기에 설치되어 있는 Tmap을 직접 실행하여
        	// Tmap 내 설정되어 있는 집, 회사 정보에 따라 안내를 시작한다.
        	
    		switch (v.getId()) {
    		
    		case R.id.home_button:
    			tapi.invokeGoHome();
    			break;
    		case R.id.comp_button:
    			tapi.invokeGoCompany();    			
    			break;
    		case R.id.freq_button:
    			SharedPreferences p_lat = getSharedPreferences("p_lat", MODE_PRIVATE);
				SharedPreferences p_lng = getSharedPreferences("p_lng", MODE_PRIVATE);
				
				tapi.invokeRoute("", p_lng.getFloat("p_lng", 0), p_lat.getFloat("p_lat", 0));
    			break;	
    		default:
    			break;
    		}        	
        }
        return true;
	}
}
