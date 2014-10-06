package sw.call;

import com.srph.simplesapprovider.R;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

public class CallSetting extends Activity {

	private ListView lv;
	
	// 주소록에서 정보를 불러오는 필드들
	static final String[] field = new String[] {
			ContactsContract.CommonDataKinds.Phone._ID,
			ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
			ContactsContract.CommonDataKinds.Phone.NUMBER,
			ContactsContract.CommonDataKinds.Phone.CONTACT_ID };

	private Context context = this;
	Intent intent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		// TODO Auto-generated method stub
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.call_main);
		
		lv = (ListView) findViewById(R.id.list);

		Cursor c = getURI();

		ContactListItemAdapter adapter = 
				new ContactListItemAdapter(this, R.layout.call_list, c);
		lv.setAdapter(adapter);
		
		// 몇번째 연락처에 저장하는지 call_setting에서 번호를 가져온다.
		intent = getIntent();
		final String call = intent.getStringExtra("call_setting");
		
		
		// 각각의 list마다 ClickListener가 들어간다.
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				
				// Child는 list의 열에 대한 View이다.
				final View child = view;
				// Dialog를 띄어준다.
				AlertDialog.Builder dialog = new AlertDialog.Builder(context);				
				dialog.setTitle("추가하기");
				dialog.setMessage(call + " 번째로 " + "추가 하시겠습니까 ? ");
				
				// 확인 버튼을 누르면 실행되는것
				dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						TextView phone = (TextView)child.findViewById(R.id.phone);
						TextView name = (TextView)child.findViewById(R.id.name);
						SharedPreferences Contract = getSharedPreferences("contract", MODE_PRIVATE);
						SharedPreferences.Editor edit_Contract = Contract.edit();
						
						edit_Contract.putString("contract"+call, phone.getText().toString());
						edit_Contract.putString("contract_name"+call, name.getText().toString());
						
						edit_Contract.commit();
						setResult(RESULT_OK,intent);
						finish();
					}
				});
				
				dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				});
				dialog.show();
			}
		});
	}

	private Cursor getURI() {
		// 주소록 URI
		Uri people = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

		// 검색할 컬럼 정하기
		String[] projection = field;

		// 쿼리 날려서 커서 얻기
		String[] selectionArgs = null;
		String sortOrder = ContactsContract.Contacts.DISPLAY_NAME
				+ " COLLATE LOCALIZED ASC";

		// managedquery 는 activity 메소드이므로 아래와 같이 처리함
		return getContentResolver().query(people, projection, null,
				selectionArgs, sortOrder);
	}

	private final class ContactListItemAdapter extends ResourceCursorAdapter {
		@SuppressWarnings("deprecation")
		public ContactListItemAdapter(Context context, int layout, Cursor c) {
			super(context, layout, c);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			ContactListItemCache cache = (ContactListItemCache) view.getTag();
			TextView nameView = cache.nameView;
			TextView phoneView = cache.phoneView;

			// 이름 표시
			int nameIdx = cursor.getColumnIndex(Contacts.DISPLAY_NAME);
			String name = cursor.getString(nameIdx);
			nameView.setText(name);

			// 번호 표시
			int phoneIdx = cursor
					.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
			String phone = cursor.getString(phoneIdx);			
			phoneView.setText(phone);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			View view = super.newView(context, cursor, parent);
			ContactListItemCache cache = new ContactListItemCache();
			cache.nameView = (TextView) view.findViewById(R.id.name);
			cache.phoneView = (TextView) view.findViewById(R.id.phone);
			view.setTag(cache);
			return view;
		}
	}

	final static class ContactListItemCache {
		public TextView nameView, phoneView;
		/*public ImageView photoView;*/
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getActionBar().setTitle("연락처설정");
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#4ad99c")));
        return true;
    }

}
