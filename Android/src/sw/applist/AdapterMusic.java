package sw.applist;

import java.util.List;

import com.srph.simplesapprovider.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

@SuppressLint("InflateParams")

// 음악 리스트에 들어갈 음악 Adapter부분
// 음악List와 네비List가 다뤄지는 부분이 달라서 따로
// 같은 ListItemView를 사용하더라도 Music Adpater와 Navi Adapter를 분리해놨다.

public class AdapterMusic extends BaseAdapter {
	// 패키지 리스트
	List<PackageInfo> packageList;
	Activity context;
	PackageManager packageManager;
	String appName;

	public AdapterMusic(Activity context, List<PackageInfo> packageList,
			PackageManager packageManager) {
		super();
		this.context = context;
		this.packageList = packageList;
		this.packageManager = packageManager;
	}
	
	public AdapterMusic(Activity context, List<PackageInfo> packageList,
			PackageManager packageManager, String appName) {
		super();
		this.context = context;
		this.packageList = packageList;
		this.packageManager = packageManager;
		this.appName = appName;
	}

	private class ViewHolder {
		TextView apkName;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return packageList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return packageList.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	// 리스트에 들어갈 하나의 열(View)
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		LayoutInflater inflater = context.getLayoutInflater();
		CheckBox checkbox = null;

		if (convertView == null) {
			// apklist_item을 inflate 해준다.
			convertView = inflater.inflate(R.layout.apklist_item, null);
			holder = new ViewHolder();
			holder.apkName = (TextView) convertView.findViewById(R.id.appname);		
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		// 그 포지션에 대한 packageinfo를 불러온다.
		final PackageInfo packageInfo = (PackageInfo) getItem(position);
		
		// AppIcon 을 불러온다.
		Drawable appIcon = packageManager.getApplicationIcon(packageInfo.applicationInfo);
		
		final String appName = packageManager.getApplicationLabel(packageInfo.applicationInfo).toString();		
		
		// 여러개의 체크박스 중 하나의 체크박스만 체크 되어야 하는 부분이다.
		checkbox = (CheckBox) convertView.findViewById(R.id.checkbox_app);
		
		checkbox.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(AppModel.check_music){
					AppModel.checkbox_music.setChecked(false);
					AppModel.checkbox_music = (CheckBox) v;					
				}else{
					AppModel.checkbox_music = (CheckBox) v;					
					AppModel.check_music = true;
				}				
				AppModel.music = packageInfo.packageName;
				AppModel.appName_music = appName;
			}
		});
		
		// icon과 appName을 셋팅해주는 부분
		appIcon.setBounds(0, 0, 100, 100);
		holder.apkName.setCompoundDrawables(appIcon, null, null, null);
		holder.apkName.setCompoundDrawablePadding(15);		
		holder.apkName.setText(appName);

		return convertView;
	}

}
