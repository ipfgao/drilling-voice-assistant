package com.example.ddvoice;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.content.Intent;
import android.provider.AlarmClock;
import android.provider.CalendarContract;

public class ScheduleCreate {
	private String mName,mTime,mDate,mContent;
	private MainActivity mActivity;
	public ScheduleCreate(String name,String time,String date,String content,MainActivity activity){
		mName=name;
		mTime=time;
		mDate=date;
		mContent=content;
		mActivity=activity;
	}
	
	public void start(){
		switch(mName){
			case "clock":{//������������
				setClock();
				break;
			}
			case "reminder":{//������������
				setCalendar();
				break;
			}
			default:break;
		}
	}
	
	private void setClock(){
		
		Intent alarmas = new Intent(AlarmClock.ACTION_SET_ALARM);
        mActivity.startActivity(alarmas);
	/*	  AlarmManager aManager;
		  Calendar currentTime=Calendar.getInstance();
		     ��ȡ���ӹ����ʵ�� 
		  aManager = (AlarmManager)mActivity.getSystemService(mActivity.ALARM_SERVICE);
           �������� */
		  //aManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
	}
	@SuppressLint("NewApi") private void setCalendar(){
		Intent intent = new Intent(Intent.ACTION_INSERT);	                    
		intent.setData(CalendarContract.Events.CONTENT_URI); 
		mActivity.startActivity(intent); 
	}
}
