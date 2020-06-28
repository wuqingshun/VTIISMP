package com.example.test1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SearchActivity extends ListActivity {

	private Button mButton = null;
	private EditText mEditText = null;
	private ImageView delete = null;
	private ArrayList<String> NameInfo = new ArrayList<String>();//�����ݿ������еص���������������NameInfo��Ϊ�˽���ƥ��
	private Handler mhandler = null;
	private ArrayList<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();
	//����ƥ���ַ��������ݼ��ϣ�Ϊ�˴����������ʾ���б���
	//��Ҫʵ����ʾ������Ϣ�б�Ļ����Ͱѳ�ʼ��ʱ��NameInfo�е���Ϣд��mData
	private MyAdapter adapter = null;
	
	
	private Runnable eChanged = new Runnable() {
		
	    @Override
	    public void run() {
	          String data = mEditText.getText().toString();
				
	          mData.clear();
				
	          getmDataSub(data);//ƥ��������ַ����������ݼ�NameInfo�Ƚϣ����ѽ��д�����ݼ�mData
			  
	          adapter.notifyDataSetChanged();	
	    }
	};
	
	
	private void getmDataSub(String data)//ƥ���㷨������ֱ�Ӳ���Runnable��Run������
	{
	     for(int i = 0; i < NameInfo.size(); ++i){
	           if(NameInfo.get(i).contains(data)){
	        	    Map<String, Object> map = new HashMap<String, Object>();
	        	    map.put("textview", NameInfo.get(i));
	        	    mData.add(map);
	        	    //System.out.println("This is the mData:" + NameInfo.get(i).toString());
	            }
	     }
	}   

	
	private void Init(){//��ʼ����Ϣ�������ݿ��е�Name����Ϣ����䵽NameInfo�У�����֮��Ĳ���	
		
		for(Info info : Info.infos){
			NameInfo.add(info.getName());
		}
		Log.d("TAG", NameInfo.size()+"");
		mData.clear();
	    
	    /** �Զ���һ��Adapter
		 * ��һ������Ϊ��ǰActivity
		 * �ڶ���arrayΪָ����Ҫ��ʾ�����ݼ���
		 * �����������ǽ�Ҫ��ʾÿ�����ݵ�View���֣�
		 * ���ĸ���Ҫ�ǽ�Map�����е�����ӳ�䵽������һһ��Ӧ
		 * �����������int���飬��Ӧ�����ļ��е�id��
		 */
		adapter = new MyAdapter(this, mData, R.layout.listview,
				new String[]{"textview"},
				new int[]{R.id.textview});//��������Ϊ��ֵ�ԣ���4�������Ǽ�ֵ�Եĵ�һ������
		
		setListAdapter(adapter);
	}
	
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// ��Ӧlist����¼�
		super.onListItemClick(l, v, position, id);
		TextView user = (TextView) v.findViewById(R.id.textview);
		Toast.makeText(this, "��ѡ���ˣ�" + user.getText(), 1000).show();
		
		Intent intent = new Intent(this,MainActivity.class);

		Bundle bundle = new Bundle();
		bundle.putString("click_item", user.getText().toString());
		
		intent.putExtras(bundle);
		startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_search);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.custom_title_search);
		Init();//��ʼ��
		//ArrayList<Map<String, Object>> array = new ArrayList<Map<String, Object>>();
		//Map<String, Object> map1 = new HashMap<String, Object>();	
			
		mhandler = new Handler();
		
		mButton = (Button) findViewById(R.id.btnSearch2);
		mButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(SearchActivity.this,MainActivity.class);
				startActivity(intent);
			}
		});
		
		delete = (ImageView) findViewById(R.id.ivDeleteText);
		delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mEditText.setText("");
			}
		});	
		
		mEditText = (EditText) findViewById(R.id.etSearch2);
		mEditText.addTextChangedListener(new TextWatcher() {	
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				if(mEditText.length() == 0){
					delete.setVisibility(View.GONE);
				}else{
					delete.setVisibility(View.VISIBLE);
					mhandler.post(eChanged);//handler��runable��̫��⣡
				}
			}
		});	
	}
	
	//ͨ�õ�������
	class MyAdapter extends SimpleAdapter
	{
		public MyAdapter(Context context, ArrayList<? extends Map<String, ?>> data,
				int resource, String[] from, int[] to) {
			super(context, data, resource, from, to);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View result = super.getView(position, convertView, parent);
			
			if (result == null) {
				convertView = getLayoutInflater().inflate(R.layout.listview, null);	
			}
			return result;
		}
	
	}
	
}
