package com.example.ddvoice;

import java.util.List;

import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;

public class SendMessage {
	private String mPerson=null;
    private String number=null;
    private String mcontent=null;
    MainActivity mActivity;
    
    
    public SendMessage(String person,String code,String content,MainActivity activity){
    	 mPerson = person;
    	 number=code;
    	 mcontent=content;
 	    mActivity=activity;
 	   mActivity.serviceFlag=true;
    }
    
    public void start(){
    	if((number==null)||(number.equals(""))){
    		if ((mPerson == null) || (mPerson.equals("")))
    	    {
    			mActivity.serviceFlag=false;
    			 //mActivity.speak("���ٸ��������ֻ��ߺ���ɣ�", false);
    			Intent intent = new Intent("android.intent.action.SENDTO",
                        Uri.parse("smsto:" + ""));
    					intent.putExtra("sms_body",""); //Ĭ�϶�������
    					mActivity.startActivity(intent);
    					
    	    }else{
    	    	 mPerson=mPerson.trim();
    	    	number=getNumberByName(mPerson,mActivity);
    	    	 if(number == null)
    	         {
    	           mActivity.speak("ͨѶ¼û���ҵ�"+mPerson, false);
    	           mActivity.serviceFlag=false;
    	         }else{	 
    	        	 if((mcontent==null)||(mcontent.equals(""))){
    	     			
    	     			mActivity.speak("��Ҫ����ʲô�����أ�", false);
    	     			//mActivity.startSpeenchRecognition();
    	     			//
    	     			Thread mThread= new Thread(){
    	     				public void run(){
    	     					while((mActivity.SRResult==null)||(mActivity.SRResult.equals(""))){
    	     						//��ת
    	     					}
    	     					//mActivity.speak("�߳���", false);
    	     					mcontent=mActivity.SRResult;
    	             			SmsManager smsManager = SmsManager.getDefault();
    	         	        	 if(mcontent.length() > 70) {
    	                             List<String> contents = smsManager.divideMessage(mcontent);
    	                             for(String sms : contents) {
    	                                 smsManager.sendTextMessage(number, null, sms, null, null);
    	                                 insertDB(number,sms);
    	                             }
    	                         } else {
    	                          smsManager.sendTextMessage(number, null, mcontent, null, null);
    	                          insertDB(number,mcontent);
    	                         }
    	         	        	 mActivity.serviceFlag=false;
    	     				}
    	     			};
    	     			mThread.start();
    	     			//mThread.destroy();
    	     		}
    	        	 else{
    	        		 //������
        	        	 SmsManager smsManager = SmsManager.getDefault();
        	        	 if(mcontent.length() > 70) {
                             List<String> contents = smsManager.divideMessage(mcontent);
                             for(String sms : contents) {
                                 smsManager.sendTextMessage(number, null, sms, null, null);
                                 insertDB(number,sms);
                             }
                         } else {
                          smsManager.sendTextMessage(number, null, mcontent, null, null);
                          insertDB(number,mcontent);
                         }
        	        	 mActivity.serviceFlag=false;
    	        	 }
    	        	
    	        	
    	        }
    	    }
    		
    	}
    	else{
    		if((mcontent==null)||(mcontent.equals(""))){
     			mActivity.speak("��Ҫ����ʲô�����أ�", false);
     			//mActivity.startSpeenchRecognition();
     			//
     			Thread mThread= new Thread(){
     				public void run(){
     					while((mActivity.SRResult==null)||(mActivity.SRResult.equals(""))){
     						//��ת
     					}
     					//mActivity.speak("�߳���", false);
     					mcontent=mActivity.SRResult;
             			SmsManager smsManager = SmsManager.getDefault();
         	        	 if(mcontent.length() > 70) {
                             List<String> contents = smsManager.divideMessage(mcontent);
                             for(String sms : contents) {
                                 smsManager.sendTextMessage(number, null, sms, null, null);
                                 insertDB(number,sms);
                             }
                         } else {
                          smsManager.sendTextMessage(number, null, mcontent, null, null);
                          insertDB(number,mcontent);
                         }
         	        	 mActivity.serviceFlag=false;
     				}
     			};
     			mThread.start();
     			//mThread.destroy();
     		}
        	 else{
        		 //������
            	 SmsManager smsManager = SmsManager.getDefault();
            	 if(mcontent.length() > 70) {
                     List<String> contents = smsManager.divideMessage(mcontent);
                     for(String sms : contents) {
                         smsManager.sendTextMessage(number, null, sms, null, null);
                         insertDB(number,sms);
                     }
                 } else {
                  smsManager.sendTextMessage(number, null, mcontent, null, null);
                  insertDB(number,mcontent);
                 }
            	 mActivity.serviceFlag=false;
        	 }
    	}
    	
    	
    	
    }
    
    private void insertDB(String number,String content){//�����͵Ķ��Ų���ϵͳ���ݿ��У�ʹ���ڶ��Ž�����ʾ 
    	//////////////////////���׳�null���쳣---�ѽ��--- mActivity.getContentResolver()�ſ���
    	try{
	    	ContentValues values = new ContentValues();
	    	values.put("date", System.currentTimeMillis());
	    	 //�Ķ�״̬              
	        values.put("read", 0);             
	         //1Ϊ�� 2Ϊ��             
	       values.put("type", 2);           
	         //�ʹ����              
	      // values.put("status", -1);
	       values.put("address",number);             
	         //�ʹ�����            
	       values.put("body", content);             
	         //������ſ�    
	      // getContentResolver
	       mActivity.getContentResolver().insert(Uri.parse("content://sms/sent"), values);
	       mActivity.speak("���ŷ��ͳɹ�",false);
    	}catch (Exception e) { 
            Log.d("dd", "�������ݿ����⣺"+e.getMessage()); 
    	  }
    }
    
    private  String getNumberByName(String name, Context context)
	  {
		 Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI, name);
		  ContentResolver  resolver  = context.getContentResolver();
		  Cursor cursor = resolver.query(uri, new String[]{ContactsContract.Contacts._ID}, null, null, null);  
		  if((cursor!=null)&&(cursor.moveToFirst())){
			  int idCoulmn = cursor.getColumnIndex(ContactsContract.Contacts._ID);
			  long id = cursor.getLong(idCoulmn);
		      cursor.close();
		      cursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,  new String[]{"data1"}, "contact_id = ?",  new String[]{Long.toString(id)}, null);
		      if ((cursor != null) && (cursor.moveToFirst()))
		      {
		        int m = cursor.getColumnIndex("data1");
		        String num = cursor.getString(m);
		        cursor.close();
		       return num;
		      }	      
		  }
		  return null;
	  }
}
