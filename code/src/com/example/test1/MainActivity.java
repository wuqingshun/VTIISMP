package com.example.test1;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.test1.Info;
import com.lidroid.xutils.BitmapUtils;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thinkland.sdk.android.DataCallBack;
import com.thinkland.sdk.android.JuheData;
import com.thinkland.sdk.android.Parameters;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//bGcvrBWY8TVLHEIuDpdasY8ZTHLepIPp ��˾����
//aKLN6Kadxyuqb0tbx1WLrwLb �ʼǱ�
public class MainActivity extends Activity {
	
	private MapView mMapView = null;
	private RelativeLayout mMarkerInfoLy;//����������Ϣ��Ĳ���
	private BaiduMap mBaiduMap;
	private BitmapDescriptor mIconMaker;
	private MainActivity mActivity = this;
	private EditText mEditText = null;
	private Button back1 = null;
	private Context mContext = null;
	private JSONArray jsonarray = null;//����Ҫ���������˷��صĻ�ȡ���
	private Bitmap bm = null;//�洢Marker���ͼ��
	private MyLocation mylocation = null;
	
	private class ViewHolder
	{
		ImageView infoImg;
		TextView infoName;
		TextView infoAddress;
		TextView infoKcw;
		TextView infoZcw;
	}
	
	protected void popupInfo(RelativeLayout mMarkerLy, Info info)//���Marker�㣬�ȴ���ViewHolder��Ȼ����ʾ�ڿؼ���
	{															 //����IF��Ϊ���ж��Ƿ��� ����һ�Σ����MARKER,�Ӷ�֪�������ļ����Ƿ���������
																//�����initMapClickEvent�󣬲�����ʧ��tag���ڡ�
		ViewHolder viewHolder = null;
		
		if (mMarkerLy.getTag() == null)
		{
			viewHolder = new ViewHolder();
			viewHolder.infoImg = (ImageView) mMarkerLy
					.findViewById(R.id.info_img);
			viewHolder.infoName = (TextView) mMarkerLy
					.findViewById(R.id.info_name);
			viewHolder.infoAddress = (TextView) mMarkerLy
					.findViewById(R.id.info_distance);
			viewHolder.infoKcw = (TextView) mMarkerLy
					.findViewById(R.id.info_kcw);
			viewHolder.infoZcw = (TextView) mMarkerLy
					.findViewById(R.id.info_zcw);

			mMarkerLy.setTag(viewHolder);
		}
		
		viewHolder = (ViewHolder) mMarkerLy.getTag();//info.getPicture()
		
		BitmapUtils bitmapUtils= new BitmapUtils(MainActivity.this);
		bitmapUtils.display(viewHolder.infoImg, info.getPicture());//����ͼƬ
		viewHolder.infoAddress.setText(info.getAddress());
		viewHolder.infoName.setText(info.getName());
		viewHolder.infoKcw.setText(info.getKcw());
		viewHolder.infoZcw.setText(info.getZcw());
		
		NaviSkipClickEvent(info);
	}
		
	
	public void setStaticDataToInfo(){
		Info.infos.clear();
		
		//����ѧУ�ڵ�ͣ����
		Info.infos.add(new Info( "116.425130", "39.977404", "http://images.juheapi.com/park/6202.jpg" , "http://images.juheapi.com/park/P1004.png", "����������ѧ�д���", "�����г���������������ѧ����", "123", "88"));
		Info.infos.add(new Info( "116.427915", "39.978952", "http://images.juheapi.com/park/6202.jpg" , "http://images.juheapi.com/park/P1001.png", "����������ѧ�Ƽ�����", "�����г���������������ѧ", "123", "77"));
		Info.infos.add(new Info( "116.430466", "39.976727", "http://images.juheapi.com/park/6202.jpg" , "http://images.juheapi.com/park/P1003.png", "����������ѧ���¥", "�����г���������������ѧ", "123", "66"));
		
		addInfosOverlay(Info.infos);//�Ѵ洢��Info�е���Ϣ��䵽Marker���У���ʾ�ڵ�ͼ�ϡ�
	}
	
	//����������ѧ �ٶȵ�ͼ��39.9776840000,116.4280790000
	//		       �ߵµ�ͼ��39.9715961669,116.4215747657
	//			��ֵ  ��00.00608        00.00650   
	
	public void getDatafromJuhe(){//����API��ֵ��������JSON���ݣ�ֻ��ѯ��������500m��ͣ��������ý��JSONArray
		jsonarray = new JSONArray();//ÿ�λ�ȡ����ʱ��Ҫ��array���
		Parameters params = new Parameters();
		
		params.add("key", "your key");//ʹ�øߵµ�ͼ
		params.add("JD", "116.4215747657");
		params.add("WD", "39.9715961669");
		params.add("JLCX", 700);
		params.add("SDXX", 1);
		/**
		 * ����ķ��� ����: ��һ������ ��ǰ�����context �ڶ������� �ӿ�id ���������� �ӿ������url ���ĸ����� �ӿ�����ķ�ʽ
		 * ��������� �ӿ�����Ĳ���,��ֵ��com.thinkland.sdk.android.Parameters����; ����������
		 * ����Ļص�����,com.thinkland.sdk.android.DataCallBack;
		 * ����һ���첽���������̲���ȴ������Եĵ���json�����޷�SET��Infos�У���������
		 */
		JuheData.executeWithAPI(mContext, 133, "http://japi.juhe.cn/park/nearPark.from",
				JuheData.GET, params, new DataCallBack() {
					/**
					 * ����ɹ�ʱ���õķ��� statusCodeΪhttp״̬��,responseStringΪ���󷵻�����.
					 */
					@Override
					public void onSuccess(int statusCode, String responseString) {
						// TODO Auto-generated method stub
						
						try {
							JSONObject jsonobject = new JSONObject(responseString);
							JSONArray indexJsonArray = jsonobject.getJSONArray("result");
							Log.d("TAG","This is the total length of data:" + indexJsonArray.length()); 
							jsonarray = indexJsonArray;
							setDatatoInfo();//�ѵõ��������ȴ洢��Info��
							
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}						
						
					}

					/**
					 * �������ʱ���õķ���,���۳ɹ�����ʧ�ܶ������.
					 */
					@Override
					public void onFinish() {
						// TODO Auto-generated method stub
						Toast.makeText(getApplicationContext(), "finish",
								Toast.LENGTH_SHORT).show();
					}

					/**
					 * ����ʧ��ʱ���õķ���,statusCodeΪhttp״̬��,throwableΪ���񵽵��쳣
					 * statusCode:30002 û�м�⵽��ǰ����. 30003 û�н��г�ʼ��. 0
					 * δ���쳣,����鿴Throwable��Ϣ. �����쳣�����http״̬��.
					 */
					@Override
					public void onFailure(int statusCode, String responseString, Throwable throwable) {
						// TODO Auto-generated method stub
						Log.d("TAG","statusCode:" + statusCode + "/n" + throwable.getMessage());
					}
				});
		
	}
	
	
	public void setDatatoInfo(){//�ѵõ��������ȴ洢��Info��
		//Info.infos.clear();
		
		String jd = "";
		String wd = "";
		String ccmc = "";
		String ccdz = "";
		String zcw = "";
		String kcw = "";
		String cctp = "";
		String kcwzt = "";
		
		for(int i=0; i<jsonarray.length();i++){
			try {
				//tv.append("CCMC:" + jsonobject2.getString("CCMC") +"KCW:"+ jsonobject2.getString("KCW")+ "\n");
				JSONObject jsonobject = jsonarray.getJSONObject(i);
				
				jd =   jsonobject.getString("JD");//����
				wd =   jsonobject.getString("WD");//ά��
				
				double index1 = Double.parseDouble(jd);
				double index2 = Double.parseDouble(wd);
				
				//�ߵ�����ת��Ϊ�ٶ�����
				index1 = index1 + 0.00650;
				index2 = index2 + 0.00608;
				
				jd = index1 + "";
				wd = index2 + "";
				
				ccmc = jsonobject.getString("CCMC");//��������
				ccdz = jsonobject.getString("CCDZ");//������ַ
				zcw =  jsonobject.getString("ZCW");//�ܳ�λ
				kcw =  jsonobject.getString("KCW");//�ճ�λ
				cctp = "http://images.juheapi.com/park/" + jsonobject.getString("CCTP");//ͣ����ͼƬ
				kcwzt = "http://images.juheapi.com/park/" + jsonobject.getString("KCWZT");//��λ״̬ͼƬ
				
				//Log.d("TAG",kcwzt);
				//Log.d("TAG",wd+"!");
				
				Info.infos.add(new Info( jd, wd, cctp, kcwzt, ccmc, ccdz, zcw, kcw));
				
			} catch (JSONException e) {
				e.printStackTrace();
				
			}
		}
		Log.d("TAG","Finish set data to Infos");
		addInfosOverlay(Info.infos);//�Ѵ洢��Info�е���Ϣ��䵽Marker���У���ʾ�ڵ�ͼ�ϡ�
	}
	
	
	/**Bitmap�Ŵ�ķ���*/ 
	private static Bitmap big(Bitmap bitmap) { 
		Matrix matrix = new Matrix(); 
		matrix.postScale(2.0f,2.0f); //���Ϳ�Ŵ���С�ı��� 
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true); 
		return resizeBmp; 
	} 
	
	
	public void addInfosOverlay(final List<Info> infos)//��ʼ��������INFO����Marker��,
	{
		new Thread (){//һ�����Ϳ����½��̣���Ϊ�����ȡͼƬ���������߳��в���
			
		@SuppressLint("NewApi") public void run() {
		
		//mBaiduMap.clear();
		LatLng latLng = null;
		OverlayOptions overlayOptions = null;
		Marker marker = null;
		
		for (Info info : infos)
		{
			Double latitude = Double.parseDouble(info.getLatitude());//ת��ΪDouble����
			Double longitude = Double.parseDouble(info.getLongitude());//ת��ΪDoubleγ��
			
			latLng = new LatLng(longitude,latitude);//γ�ȣ�����
			
			//mIconMaker = BitmapDescriptorFactory.fromResource(R.drawable.maker);//��ͼƬת��ΪBitmapDescriptor��ʽ
			
					try {
						Log.d("TAG","123" + info.getState());
						URL url = new URL(info.getState().toString());
						//BitmapFactory.Options options = new BitmapFactory.Options();   
	        	        bm = BitmapFactory.decodeStream(url.openStream());//�����ϵ�ͼƬ��URL��ַ��ת����Bitmapֻ��Ҫ��һ��
	        	        Bitmap bitmap = big(bm);//�Ŵ�ͼƬ,��ʵ���½���һ��bitmap
	        	        mIconMaker = BitmapDescriptorFactory.fromBitmap(bitmap);//��ͼƬת��ΪBitmapDescriptor��ʽ
	        	        
	        		} catch (MalformedURLException e) {
	        			e.printStackTrace();
	        		} catch (IOException e) {
	        			e.printStackTrace();
	        		}
			
			overlayOptions = new MarkerOptions().position(latLng)
					.icon(mIconMaker).zIndex(10);//zIndexûʲô��
			marker = (Marker) (mBaiduMap.addOverlay(overlayOptions));
			//��ʼ��marker��ʱ��ֻ��Ҫ���������Ϣ��1.��γ��2.��ʾͼƬ
			//�������Ϣ��Ҫ��Bundle�洢��marker���Info����Դ
			//�������Ϣ�����ֵ���У�ÿ�ζ�NEWһ��Bundle����
			//ȡ��ʱֻ��Ҫ��marker.getExtraInfo().get("info");
			Bundle bundle = new Bundle();//bundel������session
			bundle.putSerializable("info", info);
			marker.setExtraInfo(bundle);//��INFO��Ϣ����marker����
		}
		Log.d("TAG","Finish add Markers");
		
		//�����ɵ����һ����ľ�γ����Ϣ����Ϊ��ͼ��ʾ�����ĵ㡣
		//MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latLng);
		//mBaiduMap.setMapStatus(u);
		
		skip_from_search();//run��������ʱ��һϵ�в�����ɺ��ж��ǲ��Ǵ�search��ת����
		//��marker���λ��Ū��ȷ������У԰��ĵ㣨��setDataToInfo�����м���info,add��������
		}
		}.start();
		
	}
	
	
	private void initMarkerClickEvent(){//Marker����¼�
		//�� marker ��ӵ����Ӧ�¼�
        mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
    		@Override
    		public boolean onMarkerClick(Marker marker) {
    			
    			Info info = (Info) marker.getExtraInfo().get("info");
    			
    			mMarkerInfoLy.setVisibility(View.VISIBLE);
    			popupInfo(mMarkerInfoLy, info);//�Ѵ���marker���е���Ϣ���벼�ֿ����
    			
    			//Toast.makeText(getApplicationContext(), "MarkerA������ˣ�", Toast.LENGTH_SHORT).show();
    			return false;
    		}
    	});
	}
	
	
	private void initMapClickEvent()//�����ͼ��ʹ������ʧ
	{
		mBaiduMap.setOnMapClickListener(new OnMapClickListener()
		{

			@Override
			public boolean onMapPoiClick(MapPoi arg0)
			{
				return false;
			}

			@Override
			public void onMapClick(LatLng arg0)
			{
				mMarkerInfoLy.setVisibility(View.GONE);
			}
		});
	}
	
	
	private void IninMapLocation(){//��ʼ������λ��ͼ����
		LatLng cenpt = new LatLng(39.9776840000,116.4280790000); 
        //�����ͼ״̬
        MapStatus mMapStatus = new MapStatus.Builder()
        .target(cenpt)
        .zoom(16)//����Խ�󣬵�ͼԽ�Ŵ�
        .build();
        //����MapStatusUpdate�����Ա�������ͼ״̬��Ҫ�����ı仯
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //�ı��ͼ״̬
        mBaiduMap.setMapStatus(mMapStatusUpdate);
        mBaiduMap.setMyLocationEnabled(true);//ʹ�ðٶȵ�ͼ��λͼ��ʱ��Ҫ�ȿ�����λ
	}
	
	
	private void TheMarker(){//Marker
		//����Maker�����  
        //LatLng point = new LatLng(39.9768880000,116.4270740000);  
        //LatLng point1 = new LatLng(39.9789740000,116.4268280000);  
        
        //BitmapDescriptor bitmap = BitmapDescriptorFactory  
          //  .fromResource(R.drawable.icon_marka);  
        //BitmapDescriptor bitmap1 = BitmapDescriptorFactory  
	      //      .fromResource(R.drawable.icon_markb);
        //��������
        //MarkerOptions option = new MarkerOptions().position(point).icon(bitmap)
          //      .zIndex(5).period(10);
        	//option.animateType(MarkerAnimateType.grow);
        	
        //MarkerOptions option1 = new MarkerOptions().position(point1).icon(bitmap1)
	      //      .zIndex(5).period(10);
	        //option1.animateType(MarkerAnimateType.grow);
	        	
        //final Marker markera = (Marker) mBaiduMap.addOverlay(option);
        //final Marker markerb = (Marker) mBaiduMap.addOverlay(option1);
	}
	
	
	private void MyLocationClickEvent(){//�����λ��ť����ʾ��ǰ��λ��Ϣ
		//���д���д�ڳ�ʼ�������У�mBaiduMap.setMyLocationEnabled(true);//ʹ�ðٶȵ�ͼ��λͼ��ʱ��Ҫ�ȿ�����λ
		Button mButton =(Button) findViewById(R.id.btn_location);
		
		mButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mylocation = new MyLocation(mMapView,mBaiduMap,mActivity);
			}
		});
	}
	
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			mMarkerInfoLy.setVisibility(View.VISIBLE);
			Info info = (Info) msg.getData().getSerializable("info8");
			if(info == null ){
				Log.d("TAG", "info is null");
			}
			else{	
				popupInfo(mMarkerInfoLy, info);//�Ѵ���marker���е���Ϣ���벼�ֿ����
			}
		}
	};
	
		
	private void skip_from_search(){//��serach����ת�� ��ʱ�򣬰�marker����ʾ����
		
		Bundle bundle = new Bundle();
		bundle = getIntent().getExtras();
		
		if(bundle != null){
			Log.d("TAG", "�� ����ҳ�� ��ת������");
			String str = bundle.getString("click_item");//�õ�ͣ�������֣�Ȼ���Infos��ѭ���ж����ĸ���Ȼ�����popinfo��
			
			for(Info info : Info.infos){
				if(str.equals(info.getName())){
					Log.d("TAG", "ѭ���"+info.getName());					
					Bundle index = new Bundle();
					Message msg = new Message();
					index.putSerializable("info8", info);
					msg.setData(index);
					handler.sendMessage(msg);
				}
			}
		}
	}
	
	
	private void NaviSkipClickEvent(final Info info){//���������ȥ����ť����ת��������ʼ������
		Button navi = (Button) findViewById(R.id.navi);
		
		
		navi.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {//Ҫ�ȵ����λ��ť��������
				Intent intent = new Intent(MainActivity.this,NaviInitActivity.class);
				Bundle bundle = new Bundle();
				
				String start_wd = "" , start_jd = "", end_wd = "", end_jd = "";
				
				end_jd = info.getLatitude();//ת��ΪDoubleγ��
				end_wd = info.getLongitude();//ת��ΪDouble����
				
				Log.d("TAG","������Bundle��Ŀ�ĵؾ�γ�ȣ�" + end_wd + "  "+ end_jd);
				
				bundle.putString("end_wd", end_wd);
				bundle.putString("end_jd", end_jd);
				
				if(mylocation != null){
					start_jd = mylocation.getMyJD() + "";
					start_wd = mylocation.getMyWD() + "";
				}
				
				Log.d("TAG","������Bundle����λ�ľ�γ�ȣ�" + start_wd + "  "+ start_jd);
				
				bundle.putString("start_wd", start_wd);
				bundle.putString("start_jd", start_jd);
				
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}
	
	
	private void NormalMapClickEvent(){//�����ť����ʾ��ͨ��ͼ
		Button button = (Button) findViewById(R.id.btn_normal_map);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);//��ͨ��ͼ
				mBaiduMap.setTrafficEnabled(false);//��ͨͼ
			}
		});		
	}
	
	
	private void SatellitMapClickEvent(){//�����ť����ʾ���ǵ�ͼ
		Button button = (Button) findViewById(R.id.btn_weixing_map);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);//����ͼ
			}
		});
	}
	
	
	private void TrafficMapClickEvent(){//�����ť����ʾ��ͨ��ͼ
		Button button = (Button) findViewById(R.id.btn_traffic_map);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mBaiduMap.setTrafficEnabled(true);//��ͨͼ
			}
		});
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext()); 
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.custom_title_main);
		mContext = this;
		
		mEditText = (EditText) findViewById(R.id.etSearch1);
		mEditText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MainActivity.this,SearchActivity.class);
				startActivity(intent);
			}
		});
		
		back1 = (Button) findViewById(R.id.back1);
		back1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MainActivity.this,HomePageActivity.class);
				startActivity(intent);
			}
		});
		
		mMarkerInfoLy = (RelativeLayout) findViewById(R.id.id_marker_info);
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);//��ͨ��ͼ

		IninMapLocation();
		
		Log.d("TAG","This is the start!");
		
		setStaticDataToInfo();
		getDatafromJuhe();//�ӷ������˻�ȡ������䵽Info��
		//������������˳��ִ�У�����
		//setDatatoInfo();//�ѵõ��������ȴ洢��Info��
	    //addInfosOverlay(Info.infos);//�Ѵ洢��Info�е���Ϣ��䵽Marker���У���ʾ�ڵ�ͼ�ϡ�
		Log.d("TAG","This is the end!");
		
		initMarkerClickEvent();//���Marker��
	    initMapClickEvent();//�����ͼ
	    
	    
	    MyLocationClickEvent();//��ǰ��λ
	    NormalMapClickEvent();//��ʾ������ͼ
	    TrafficMapClickEvent();//��ʾ��ͨͼ
	    SatellitMapClickEvent();//��ʾ����ͼ
	    
	}
	@Override  
    protected void onDestroy() {  
        super.onDestroy();  
        //��activityִ��onDestroyʱִ��mMapView.onDestroy()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onDestroy();  
        mIconMaker.recycle();
		mMapView = null;
		JuheData.cancelRequests(mContext);
    }  
    @Override  
    protected void onResume() {  
        super.onResume();  
        //��activityִ��onResumeʱִ��mMapView. onResume ()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onResume();  
        }  
    @Override  
    protected void onPause() {  
        super.onPause();  
        //��activityִ��onPauseʱִ��mMapView. onPause ()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onPause();  
    }  
    
	
}
