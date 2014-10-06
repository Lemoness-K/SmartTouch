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
public class AdapterNavi extends BaseAdapter {

	List<PackageInfo> packageList;
	Activity context;
	PackageManager packageManager;
	CheckBox checkbox = null;
	String appName;
	
	 public AdapterNavi(Activity context, List<PackageInfo> packageList, PackageManager packageManager) {
		super();
		this.context = context;
		this.packageList = packageList;
		this.packageManager = packageManager;
	}
	
	public AdapterNavi(Activity context, List<PackageInfo> packageList, PackageManager packageManager, String name) {
		super();
		this.context = context;
		this.packageList = packageList;
		this.packageManager = packageManager;
		this.appName = name;
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		LayoutInflater inflater = context.getLayoutInflater();
		if (convertView == null) {
			
			convertView = inflater.inflate(R.layout.apklist_item, null);
			holder = new ViewHolder();
			holder.apkName = (TextView) convertView.findViewById(R.id.appname);		
			convertView.setTag(holder);
			
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final PackageInfo packageInfo = (PackageInfo) getItem(position);		
		Drawable appIcon = packageManager.getApplicationIcon(packageInfo.applicationInfo);		
		final String appName = packageManager.getApplicationLabel(packageInfo.applicationInfo).toString();
		checkbox = (CheckBox) convertView.findViewById(R.id.checkbox_app);
		
		checkbox.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(AppModel.check_navi){
					AppModel.checkbox_navi.setChecked(false);
					AppModel.checkbox_navi = (CheckBox) v;					
				}else{
					AppModel.checkbox_navi = (CheckBox) v;					
					AppModel.check_navi = true;
				}							
				AppModel.navi = packageInfo.packageName;
				AppModel.appName_navi = appName;				
			}
		});
		
		appIcon.setBounds(0, 0, 100, 100);
		holder.apkName.setCompoundDrawables(appIcon, null, null, null);
		holder.apkName.setCompoundDrawablePadding(15);		
		holder.apkName.setText(appName);

		return convertView;
	}

	

}
