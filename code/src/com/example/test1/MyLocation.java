package com.example.test1;

import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

/**
 * 
 * ���ܣ�ʵ���ҵĶ�λ
 * ���ݵĲ�����1.mMapView
 * 			2.mBaiduMap
 * 			3.activity��Ҳ����This��
 * ���ô�����Ҫ��new MyLocation(mMapView,mBaiduMap,activity);
 * 
 * @author YzGuo
 *
 */

public class MyLocation{

	public MyLocationListenner myListener = new MyLocationListenner();
	
	private LocationClient mLocClient;
	private MapView mMapView ;
	private BaiduMap mBaiduMap ;
	private MainActivity activity ;
	public double mJD = 0;
	public double mWD = 0;
	

	public MyLocation(){

	}
	
	public MyLocation(MapView index , BaiduMap index1 ,MainActivity index2){
		
		this.mMapView = index;
		this.mBaiduMap = index1;
		this.activity = index2;
		this.getLocation();
	}
	
	public double getMyJD(){
		return mJD;
	}
	
	public double getMyWD(){
		return mWD;
	}
	
	/**
	 * ��λSDK��������
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view ���ٺ��ڴ����½��յ�λ��
			
			if (location == null || mMapView == null){
				System.out.print("location is null");
				return;
			}
			
			mWD = location.getLatitude();
			mJD = location.getLongitude();
			
			//Log.d("TAG", msg);
			//�Ӱٶȵ�ͼAPI�л�ȡ��λ��Ϣ������mBaiduMap��
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					//�˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			
			//���õ�ǰ���ڽ���ľ�γ��Ϊ���ڵ�λ��
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(new LatLng(location.getLatitude(),location.getLongitude()));
			mBaiduMap.animateMapStatus(u);
			
			//���õ�ͼ�����ż�Ϊ15
			MapStatusUpdate n = MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(15).build());
			mBaiduMap.setMapStatus(n);//�޷�ʵ��ÿ�ε����ť���������ż���һ��
			
			//ֹͣ��λ
			mLocClient.stop();
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}
	
	public void getLocation() {
		// ��λ��ʼ��
		mLocClient = new LocationClient(activity);
		mLocClient.registerLocationListener(myListener);

		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);//��gps
		option.setCoorType("bd09ll"); //������������
		option.setScanSpan(5000); //��λʱ����
		mLocClient.setLocOption(option);

		mLocClient.start();
	}
}
