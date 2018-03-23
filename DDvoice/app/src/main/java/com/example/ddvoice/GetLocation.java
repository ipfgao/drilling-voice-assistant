package com.example.ddvoice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class GetLocation {//�õ�����λ��
	
	private LocationManager locationManager;
	private String locationProvider;  
	MainActivity mActivity=null;
	
	public GetLocation(MainActivity activity){
		mActivity=activity;
	}
	
	public void start(){
		//��ȡ����λ�ù�����  
        locationManager = (LocationManager) mActivity.getSystemService(mActivity.LOCATION_SERVICE);  
        //��ȡ���п��õ�λ���ṩ��  
        List<String> providers = locationManager.getProviders(true);  
        if(providers.contains(LocationManager.GPS_PROVIDER)){  
            //�����GPS  
            locationProvider = LocationManager.GPS_PROVIDER;  
        }else if(providers.contains(LocationManager.NETWORK_PROVIDER)){  
            //�����Network  
            locationProvider = LocationManager.NETWORK_PROVIDER;  
        }else{  
        	mActivity.speak("û�п��õ�λ���ṩ��", false);
            return ;  
        }  
        //��ȡLocation  
        Location location = locationManager.getLastKnownLocation(locationProvider);  
        if(location!=null){  
            //��Ϊ��,��ʾ����λ�þ�γ��  
            showLocation(location);  
        }  
        //���ӵ���λ�ñ仯  
        locationManager.requestLocationUpdates(locationProvider, 3000, 1, locationListener);  
          
    }  
	
	
	 /** 
     * ��ʾ����λ�þ��Ⱥ�γ����Ϣ 
     * @param location 
     */  
    private void showLocation(Location location){  
        String locationStr = "ά�ȣ�" + location.getLatitude() +"\n"   
                + "���ȣ�" + location.getLongitude();  
        mActivity.speak(locationStr, false);
        mActivity.speak(LocationToCity(location.getLatitude(),location.getLongitude()), false);
        //postionView.setText(locationStr);  
    }  
    
    
    /** 
     * LocationListern������ 
     * ����������λ���ṩ��������λ�ñ仯��ʱ������λ�ñ仯�ľ�������LocationListener������ 
     */  
      
    LocationListener locationListener =  new LocationListener() {  
          
        @Override  
        public void onStatusChanged(String provider, int status, Bundle arg2) {  
              
        }  
          
        @Override  
        public void onProviderEnabled(String provider) {  
              
        }  
          
        @Override  
        public void onProviderDisabled(String provider) {  
              
        }  
          
        @Override  
        public void onLocationChanged(Location location) {  
            //���λ�÷����仯,������ʾ  
            showLocation(location);  
              
        }

    };  
    
    private String LocationToCity(double latitude,double longitude){//ʹ�ðٶȵ�ͼAPIʵ�ִӾ�γ��ת��Ϊ����
    	
    	
    	//listview = (ListView)this.findViewById(R.id.listview);
    	 // String length = mActivity.getResources().getString(R.string.length);
    /*	  
    	  try
    	  {
    	   List<News> newses = NewsService.getLastNews();//�õ�XML
    	   List<HashMap<String,Object>> data = new ArrayList<HashMap<String,Object>>();
    	   for(News news:newses)
    	   {
    	    HashMap<String,Object> item = new HashMap<String,Object>();
    	    item.put("id", news.getId());
    	    item.put("title", news.getTitle());
    	    //item.put("timelength", length+news.getTimelength());
    	    data.add(item);
    	   }
    	   
    	   //SimpleAdapter adapter = new SimpleAdapter(this, data,R.layout.item ,new String[]{"title","timelength"}, new int[]{R.id.title,R.id.timelength});
    	   //listview.setAdapter(adapter);
    	  }catch(Exception e)
    	  {
    	   e.printStackTrace();
    	   
    	  }
    */
    	
    	
    	
    	
    	
		return locationProvider;
    	
    }
    
    /*protected void onDestroy() {  
        super.onDestroy();  
        if(locationManager!=null){  
            //�Ƴ�������  
            locationManager.removeUpdates(locationListener);  
        }  
    }  
    @Override  
    public boolean onCreateOptionsMenu(Menu menu) {  
        // Inflate the menu; this adds items to the action bar if it is present.  
        getMenuInflater().inflate(R.menu.main, menu);  
        return true;  
    }  */
	
}