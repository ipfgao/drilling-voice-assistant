package com.example.ddvoice;

import android.database.Cursor;

public class QueryDB {
	private MainActivity mActivity;
	public QueryDB(MainActivity activity){
		mActivity=activity;
	}
	public void start(){
		DBHelper helper = new DBHelper(mActivity);
		Cursor c= helper.query();
		if (c.moveToFirst() == false)
		{
			mActivity.speak("���ݿ���û������",false);
		//Ϊ�յ�Cursor
		return;
		}
		//��ȡ������
		int reactionColumnIndex=-1;
		int likeColumnIndex=-1;
		String reaction=null;
		int like=0;
		//��õ�һ��ֵ
		reactionColumnIndex = c.getColumnIndex("reaction");
		reaction = c.getString(reactionColumnIndex);
		likeColumnIndex=c.getColumnIndex("like");
		like=c.getInt(likeColumnIndex);
		mActivity.speak(reaction+":"+like,false);
		//��ȡʣ�µ�ֵ
		while(c.moveToNext())
		{
			reactionColumnIndex = c.getColumnIndex("reaction");
			reaction = c.getString(reactionColumnIndex);
			likeColumnIndex=c.getColumnIndex("like");
			like=c.getInt(likeColumnIndex);
			mActivity.speak(reaction+":"+like,false);
		//����ƶ��ɹ�
		//������ȡ��
		}
		
	}
}
